import type SalesforceAuth from "../api-client/salesforceAuth.js";
import type { AriveWebhookEvent } from "../events/arive-event.js";
import { logger } from "../src/logger.js";
import type { OutboundSystemService, ProcessingContext } from "./outbound-system-service.js";

type GenericObject = Record<string, unknown>;
const DEFAULT_RLA_OWNER_ID = "0055w00000CWpTPAA1";
const DEFAULT_BROKER_COMP_PERCENT = 2.375;
const LENDING_BORROWER_RECORD_TYPE_ID = "0125w000000BR5PAAW";

// ─── SECTION 1: Type coercion utilities ──────────────────────────────────────
//
// Safe converters that accept `unknown` and return a typed value or null.
// These never throw. Use them whenever reading from the loan payload.
//
// toStringValue  — returns a trimmed non-empty string, or null
// toNumberValue  — returns a finite number, or null
// toBooleanValue — returns a boolean, or null (handles "true"/"false" strings)
// toDateValue    — returns "YYYY-MM-DD", or null (strips time from ISO strings)
// toSalesforcePercent — converts a percent value to Salesforce decimal form
//                       (e.g. 26.51 → 0.2651; already-decimal 0.26 passes through)
// toInterestRatePercent — normalises a rate to percent form
//                         (e.g. 0.0525 → 5.25; already-percent 5.25 passes through)
// ─────────────────────────────────────────────────────────────────────────────

function asObject(value: unknown): GenericObject | null {
  return value && typeof value === "object" && !Array.isArray(value) ? (value as GenericObject) : null;
}

function asArray(value: unknown): unknown[] {
  return Array.isArray(value) ? value : [];
}

function toStringValue(value: unknown): string | null {
  if (typeof value === "string") {
    const trimmed = value.trim();
    return trimmed.length > 0 ? trimmed : null;
  }
  if (typeof value === "number" || typeof value === "boolean") {
    return String(value);
  }
  return null;
}

function toPropertyStringOrNull(value: unknown): string | null {
  const str = toStringValue(value);
  if (!str) return null;
  const normalized = str.trim().toLowerCase();
  if (normalized === "null" || normalized === "n/a" || normalized === "na") return null;
  return str;
}

function toPropertyStringOrTbd(value: unknown): string {
  return toPropertyStringOrNull(value) ?? "TBD";
}

function toNumberValue(value: unknown): number | null {
  if (typeof value === "number" && Number.isFinite(value)) {
    return value;
  }
  if (typeof value === "string" && value.trim().length > 0) {
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : null;
  }
  return null;
}

function toBooleanValue(value: unknown): boolean | null {
  if (typeof value === "boolean") {
    return value;
  }
  if (typeof value === "number") {
    if (value === 1) return true;
    if (value === 0) return false;
  }
  if (typeof value === "string") {
    const normalized = value.trim().toLowerCase();
    if (normalized === "true" || normalized === "yes" || normalized === "y" || normalized === "1") return true;
    if (normalized === "false" || normalized === "no" || normalized === "n" || normalized === "0") return false;
  }
  return null;
}

function toDateValue(value: unknown): string | null {
  const str = toStringValue(value);
  if (!str) return null;
  const candidate = str.includes("T") ? str.split("T")[0] : str;
  if (/^\d{4}-\d{2}-\d{2}$/.test(candidate)) return candidate;

  // Fallback for date-like strings returned in other formats.
  const parsed = new Date(str);
  if (!Number.isNaN(parsed.getTime())) {
    return parsed.toISOString().split("T")[0] ?? null;
  }
  return null;
}

function toSalesforcePercent(value: unknown): number | null {
  const numeric = toNumberValue(value);
  if (numeric === null) return null;
  // Values > 1 are already in percent form (e.g. 26.51) — convert to decimal.
  // Values <= 1 are already decimal (e.g. 0.2651) — pass through.
  return Math.abs(numeric) > 1 ? numeric / 100 : numeric;
}

function toInterestRatePercent(value: unknown): number | null {
  const numeric = toNumberValue(value);
  if (numeric === null) return null;
  // Values <= 1 are in decimal form (e.g. 0.0525) — convert to percent.
  // Values > 1 are already percent (e.g. 5.25) — pass through.
  return Math.abs(numeric) <= 1 ? numeric * 100 : numeric;
}

// Escapes a string for use inside a SOQL single-quoted literal.
function escapeSoql(value: string): string {
  return value.replace(/\\/g, "\\\\").replace(/'/g, "\\'");
}

// Walks a list of keys on an object and returns the first non-empty string found.
function findByKeyValues(root: GenericObject, keys: string[]): string | null {
  for (const key of keys) {
    const value = toStringValue(root[key]);
    if (value) return value;
  }
  return null;
}

function normalizeKeyName(value: string): string {
  return value.replace(/[_\s-]+/g, "").toLowerCase();
}

function getKeyDateValue(keyDates: unknown, keys: string[]): unknown {
  const objectDates = asObject(keyDates);
  if (objectDates) {
    for (const key of keys) {
      if (objectDates[key] !== undefined) return objectDates[key];
    }
  }

  const keySet = new Set(keys.map((k) => normalizeKeyName(k)));
  for (const itemRaw of asArray(keyDates)) {
    const item = asObject(itemRaw);
    if (!item) continue;
    const name = toStringValue(item.name ?? item.key ?? item.type);
    if (!name || !keySet.has(normalizeKeyName(name))) continue;
    return item.value ?? item.date ?? item.dateValue ?? item.timestamp ?? null;
  }
  return null;
}

// ─── SECTION 2: Value translation functions ───────────────────────────────────
//
// Each function maps a raw Arive field value to its Salesforce picklist/string
// equivalent. Add new translations here as new field mappings are confirmed.
// ─────────────────────────────────────────────────────────────────────────────

function mapLoanPurpose(raw: string | null): string | null {
  if (!raw) return null;
  if (raw === "Purchase" || raw === "PreApproval") return "Purchase";
  if (raw === "RefinanceRateandTerm") return "Rate/Term Refinance";
  if (raw === "RefinanceCashOut") return "Cash-Out Refinance";
  return "Other";
}

// Passes lender name through as-is from Arive. Only strips sentinel non-values.
function mapLenderName(raw: string | null): string | null {
  if (!raw || raw === "Undefined") return null;
  if (raw === "Jmac Lending") return "JMAC Lending";
  if (raw === "Pennymac" || raw === "PennyMac") return "PennyMac Financial";
  if (raw === "Rocket Mortgage") return "Rocket Pro TPO";
  if (raw === "Sierra Pacific") return "Sierra Pacific Mortgage";
  return raw;
}

function mapMortgageType(raw: string | null): string | null {
  if (!raw) return null;
  const lower = raw.toLowerCase();
  if (raw === "Conforming") return "Conventional";
  if (raw === "Unknown") return "";
  if (raw === "Second Mortgage") return "Other";
  if (raw === "HELOAN") return "HELOC";
  if (lower.includes("fha")) return "FHA";
  if (lower.includes("va")) return "VA";
  if (lower.includes("usda")) return "USDA";
  if (lower.includes("nonqm") || lower.includes("nonconforming") || lower.includes("non-conforming") || lower.includes("non-qm")) return "Non-QM";
  return raw;
}

function mapLockStatus(raw: string | null): string {
  if (!raw || raw.toLowerCase() === "none") return "Not Locked";
  return raw;
}

// Arive values: "No Cash Out" | "Limited Cash Out" | "Cash Out"
function mapRefinanceType(raw: string | null): string | null {
  if (!raw) return null;
  if (raw === "No Cash Out" || raw === "Limited Cash Out" || raw === "Cash Out") return raw;
  return null;
}

function mapPrepayPenalty(raw: string | null): boolean | null {
  if (!raw) return null;
  const lower = raw.toLowerCase();
  if (lower === "no") return false;
  if (lower === "yes") return true;
  return null;
}

function mapChannel(raw: string | null): string {
  if (!raw) return "Brokered";
  if (raw.toLowerCase() === "broker") return "Brokered";
  return raw;
}

function mapStateToFullName(raw: string | null): string | null {
  if (!raw) return null;
  const normalized = raw.trim().toUpperCase();
  const stateMap: Record<string, string> = {
    AL: "Alabama", AK: "Alaska", AZ: "Arizona", AR: "Arkansas", CA: "California", CO: "Colorado", CT: "Connecticut",
    DE: "Delaware", FL: "Florida", GA: "Georgia", HI: "Hawaii", ID: "Idaho", IL: "Illinois", IN: "Indiana",
    IA: "Iowa", KS: "Kansas", KY: "Kentucky", LA: "Louisiana", ME: "Maine", MD: "Maryland", MA: "Massachusetts",
    MI: "Michigan", MN: "Minnesota", MS: "Mississippi", MO: "Missouri", MT: "Montana", NE: "Nebraska", NV: "Nevada",
    NH: "New Hampshire", NJ: "New Jersey", NM: "New Mexico", NY: "New York", NC: "North Carolina", ND: "North Dakota",
    OH: "Ohio", OK: "Oklahoma", OR: "Oregon", PA: "Pennsylvania", RI: "Rhode Island", SC: "South Carolina",
    SD: "South Dakota", TN: "Tennessee", TX: "Texas", UT: "Utah", VT: "Vermont", VA: "Virginia", WA: "Washington",
    WV: "West Virginia", WI: "Wisconsin", WY: "Wyoming", DC: "District of Columbia"
  };
  return stateMap[normalized] ?? raw;
}

// Arive propertyUsageType values → Salesforce Occupancy_Type__c
function mapOccupancy(raw: string | null): string | null {
  if (!raw) return null;
  if (raw === "PrimaryResidence") return "Primary";
  if (raw === "SecondHome") return "Second Home";
  if (raw === "InvestmentProperty" || raw === "Investor") return "Investment";
  return raw;
}

function mapLienPosition(raw: unknown): { numberValue: string | null; textValue: string | null } {
  const numeric = toNumberValue(raw);
  if (numeric === 1) return { numberValue: "1", textValue: "FirstLien" };
  if (numeric === 2) return { numberValue: "2", textValue: "SecondLien" };
  const str = toStringValue(raw);
  if (str === "FirstLien") return { numberValue: "1", textValue: "FirstLien" };
  if (str === "SecondLien") return { numberValue: "2", textValue: "SecondLien" };
  return { numberValue: null, textValue: null };
}

function getLenderPaidFlatFeeDeduction(
  translatedLenderName: string | null,
  transactionType: string | null,
  tridOrApplicationDate: string | null
): number | null {
  const kindWindsorCutoff = "2026-03-31";
  const tlsCutoff = "2026-04-02";
  if (!translatedLenderName) return null;

  if (translatedLenderName === "Kind Lending") {
    if (!tridOrApplicationDate) return 750;
    return tridOrApplicationDate < kindWindsorCutoff ? 150 : 750;
  }
  if (translatedLenderName === "Windsor Mortgage") {
    if (!tridOrApplicationDate) return 1000;
    if (tridOrApplicationDate < kindWindsorCutoff) return null;
    return 1000;
  }
  if (translatedLenderName === "The Loan Store") {
    if (!tridOrApplicationDate) return 750;
    if (tridOrApplicationDate < tlsCutoff) return null;
    return 750;
  }
  if (
    translatedLenderName === "Rocket Pro TPO" &&
    Boolean(transactionType && transactionType.toLowerCase().includes("purchase"))
  ) {
    return 150;
  }
  if (translatedLenderName === "Plaza Home Mortgage") return 150;
  return null;
}

function normalizeAriveStage(value: string): string {
  return value.trim().replace(/[\s-]+/g, "_").toUpperCase();
}

function resolveAriveStageCode(loan: GenericObject, currentLoanStatus: GenericObject): string | null {
  const raw =
    findByKeyValues(currentLoanStatus, ["status", "displayName", "name"]) ??
    findByKeyValues(loan, ["loanStage", "stage"]);
  if (!raw) return null;
  return normalizeAriveStage(raw);
}

function mapAriveStageToCurrentMilestone(stageCode: string | null): string | null {
  if (!stageCode) return null;
  const explicitMap: Record<string, string> = {
    APPLICATION_INTAKE: "Started",
    LOAN_SETUP: "ReadyToRegister",
    DISCLOSURE_SENT: "Missing Docs",
    UNDERWRITING_SUBMITTED: "Submitted",
    APPROVED_WITH_CONDITION: "Approved",
    RE_SUBMITTAL: "Approved",
    CLEAR_TO_CLOSE: "ClearToClose",
    LOAN_FUNDED: "Funded",
    SUSPENDED: "Suspended"
  };
  return explicitMap[stageCode] ?? null;
}

function mapAriveStageToLoanSubStatus(stageCode: string | null): string | null {
  if (!stageCode) return null;
  const explicitMap: Record<string, string> = {
    APPLICATION_INTAKE: "APPLICATION_INTAKE",
    QUALIFICATION: "QUALIFICATION",
    LOAN_SETUP: "Application Submitted",
    DISCLOSURE_SENT: "Application Submitted",
    RE_SUBMITTAL: "RE_SUBMITTAL"
  };
  return explicitMap[stageCode] ?? null;
}

function shouldSetPreApprovalDate(stageCode: string | null): boolean {
  return stageCode === "PREAPPROVED" || stageCode === "PRE_APPROVAL";
}

// ─── SECTION 3: Payload assembly helpers ─────────────────────────────────────
//
// Functions that extract or derive composite values from the loan payload.
// ─────────────────────────────────────────────────────────────────────────────

function findLoanTeamMemberByRole(loanTeam: unknown[], role: string): GenericObject | null {
  for (const memberRaw of loanTeam) {
    const member = asObject(memberRaw);
    if (!member) continue;
    if (toStringValue(member.loanRole)?.toLowerCase() === role.toLowerCase()) return member;
  }
  return null;
}

// Subtracts financed upfront fees (FHA MIP, VA Funding Fee) from the gross loan
// amount to produce the net Loan_Amount__c value written to Salesforce.
function calculateLoanAmountAfterFees(loanAmount: number | null, fees: unknown[]): number | null {
  if (loanAmount === null) return null;
  let adjusted = loanAmount;
  const processedTypes = new Set<string>();
  for (const feeRaw of fees) {
    const fee = asObject(feeRaw);
    if (!fee) continue;
    const feeType = toStringValue(fee.feeType);
    if (!feeType || processedTypes.has(feeType)) continue;
    if (feeType !== "FhaUpfrontMortgageInsurancePremium" && feeType !== "VaFundingFee") continue;
    const amount =
      toNumberValue(fee.amount) ??
      [fee.borrowerPacAmount, fee.borrowerPocAmount, fee.sellerPacAmount, fee.sellerPocAmount, fee.othersPaidAmount]
        .map((v) => toNumberValue(v) ?? 0)
        .reduce((acc, curr) => acc + curr, 0);
    if (amount !== 0) {
      adjusted -= amount;
      processedTypes.add(feeType);
    }
  }
  return adjusted;
}

// Builds a single comma-separated property address string from subject property fields.
function composePropertyAddress(subjectProperty: GenericObject | null): string | null {
  if (!subjectProperty) return null;
  const line1 = toPropertyStringOrNull(subjectProperty.addressLineText ?? subjectProperty.street1);
  const city = toPropertyStringOrNull(subjectProperty.city);
  const state = toPropertyStringOrNull(subjectProperty.state);
  const postal = toPropertyStringOrNull(subjectProperty.postalCode ?? subjectProperty.zipCode);
  const parts = [line1, city, state, postal].filter(Boolean) as string[];
  return parts.length > 0 ? parts.join(", ") : null;
}

function resolveCompensationType(raw: string | null): string {
  if (!raw) return "Lender Paid";
  if (raw.toLowerCase().includes("lender")) return "Lender Paid";
  if (raw.toLowerCase().includes("borrower")) return "Borrower Paid";
  return "Lender Paid";
}

// Returns the total dollar amount of the first BrokerFee fee record found.
function getFirstBrokerFeeAmount(fees: unknown[]): number | null {
  for (const feeRaw of fees) {
    const fee = asObject(feeRaw);
    if (!fee || toStringValue(fee.feeType) !== "BrokerFee") continue;
    const amount = [fee.borrowerPacAmount, fee.borrowerPocAmount, fee.sellerPacAmount, fee.sellerPocAmount, fee.othersPaidAmount]
      .map((v) => toNumberValue(v) ?? 0)
      .reduce((acc, curr) => acc + curr, 0);
    if (amount > 0) return amount;
  }
  return null;
}

function toIntegerValue(value: unknown): number | null {
  if (typeof value === "number" && Number.isFinite(value)) return Math.trunc(value);
  if (typeof value === "string" && value.trim().length > 0) {
    const parsed = Number(value);
    return Number.isFinite(parsed) ? Math.trunc(parsed) : null;
  }
  return null;
}

function findContactWithLowestRef(contacts: unknown[], contactType: string, role: string | null): GenericObject | null {
  let result: GenericObject | null = null;
  let lowestRef: number | null = null;

  for (const contactRaw of contacts) {
    const contact = asObject(contactRaw);
    if (!contact) continue;
    const type = toStringValue(contact.contactType);
    if (!type || type.toLowerCase() !== contactType.toLowerCase()) continue;

    if (role !== null) {
      const contactRole = toStringValue(contact.role);
      if (!contactRole || contactRole.toLowerCase() !== role.toLowerCase()) continue;
    }

    const refNum = toIntegerValue(contact.contactRef);
    if (refNum === null) continue;
    if (lowestRef === null || refNum < lowestRef) {
      lowestRef = refNum;
      result = contact;
    }
  }

  return result;
}

function buildFullName(firstName: unknown, lastName: unknown): string | null {
  const parts = [toStringValue(firstName), toStringValue(lastName)].filter(Boolean) as string[];
  return parts.length > 0 ? parts.join(" ") : null;
}

// ─── SECTION 4: Borrower field builders ──────────────────────────────────────
//
// WHY isPlaceholderBorrower EXISTS:
//   During early loan intake in the previous system (Sonar), loans could be
//   created before a real borrower was identified. Sonar used "Jane Doe" as a
//   placeholder name in those cases. If written to Salesforce, this placeholder
//   would corrupt real contact records. The check nulls out all personal fields
//   for any borrower whose name matches a known placeholder pattern.
//
//   Arive may behave the same way — confirm whether Arive also uses "Jane Doe"
//   (or another placeholder) so this guard can be updated or removed if not needed.
//
// HOW TO ADD A NEW BORROWER FIELD:
//   - For the primary borrower: add to buildPrimaryBorrowerFields() below.
//   - For the co-borrower: add to buildCoBorrowerFields() below.
//   Both functions return a partial Salesforce payload object that is spread
//   into the main rlaPayload in handleEvent().
// ─────────────────────────────────────────────────────────────────────────────

function isPlaceholderBorrower(firstName: string | null, lastName: string | null): boolean {
  const full = `${firstName ?? ""} ${lastName ?? ""}`.trim().toLowerCase();
  return full === "jane doe";
}

function buildPrimaryBorrowerFields(borrower: GenericObject | null): Record<string, unknown> {
  const firstName = toStringValue(borrower?.firstName);
  const lastName = toStringValue(borrower?.lastName);

  if (!borrower || isPlaceholderBorrower(firstName, lastName)) {
    return {
      BorrowerFirstNamec__c: null,
      BorrowerLastNamec__c: null,
      BorrowerEmailc__c: null,
      BorrowerHomePhonec__c: null,
      BorrowerCellc__c: null,
      BorrowerTypec__c: null,
      BorrowerSSNc__c: null,
      Borrowerdobc__c: null,
      BorrowerMaritalStatusc__c: null
    };
  }

  return {
    BorrowerFirstNamec__c: firstName,
    BorrowerLastNamec__c: lastName,
    BorrowerEmailc__c: findByKeyValues(borrower, ["email", "emailAddressText"]),
    BorrowerHomePhonec__c: findByKeyValues(borrower, ["phone", "mobilePhone10digit", "homePhone"]),
    BorrowerCellc__c: findByKeyValues(borrower, ["workPhone"]),
    BorrowerTypec__c: findByKeyValues(borrower, ["applicantType", "roleType"]),
    BorrowerSSNc__c: findByKeyValues(borrower, ["taxIdentifier"]),
    Borrowerdobc__c: toDateValue(borrower.dateOfBirth ?? borrower.birthDate),
    BorrowerMaritalStatusc__c: findByKeyValues(borrower, ["maritalStatus", "maritalStatusType"])
  };
}

function buildCoBorrowerFields(borrower: GenericObject | null): Record<string, unknown> {
  const firstName = toStringValue(borrower?.firstName);
  const lastName = toStringValue(borrower?.lastName);
  const hasCoBorrower = Boolean(borrower);

  if (!borrower || isPlaceholderBorrower(firstName, lastName)) {
    return {
      HasCoborrowerc__c: hasCoBorrower,
      CoBorrowerAuthorizedCreditReportc__c: hasCoBorrower,
      CoBorrowerMortgageServicec__c: hasCoBorrower,
      CoBorrowerFirstNamec__c: null,
      CoBorrowerLastNamec__c: null,
      CoBorrowerEmailc__c: null,
      CoBorrowerCellc__c: null,
      CoBorrowerHomePhonec__c: null,
      CoBorrowerdobc__c: null,
      CoBorrowerMaritalStatusc__c: null,
      // Clear co-borrower FICO/credit fields when there is no co-borrower
      CoBorrowerDaysConsentc__c: null,
      CoBorrowerEquifaxc__c: null,
      CoBorrowerExperianFICOc__c: null,
      CoBorrowerMinimumFICOc__c: null,
      CoBorrowerSSNc__c: null,
      CoBorrowerTransactionPurposec__c: null,
      CoBorrowerTransUnionc__c: null
    };
  }

  return {
    HasCoborrowerc__c: true,
    CoBorrowerAuthorizedCreditReportc__c: true,
    CoBorrowerMortgageServicec__c: true,
    CoBorrowerFirstNamec__c: firstName,
    CoBorrowerLastNamec__c: lastName,
    CoBorrowerEmailc__c: findByKeyValues(borrower, ["email", "emailAddressText"]),
    CoBorrowerCellc__c: findByKeyValues(borrower, ["phone", "mobilePhone10digit", "homePhone"]),
    CoBorrowerHomePhonec__c: findByKeyValues(borrower, ["workPhone"]),
    CoBorrowerdobc__c: toDateValue(borrower.dateOfBirth ?? borrower.birthDate),
    CoBorrowerMaritalStatusc__c: findByKeyValues(borrower, ["maritalStatus", "maritalStatusType"])
  };
}

// ─── SECTION 5: Salesforce loan handler ──────────────────────────────────────

export class SalesforceLoanHandler implements OutboundSystemService {
  readonly name = "salesforce-loan-sync";

  constructor(
    private readonly salesforceAuthClient: SalesforceAuth,
    private readonly options: { leadSyncEnabled: boolean; logViewEnabled: boolean }
  ) {}

  private logView(message: string, data?: unknown): void {
    if (!this.options.logViewEnabled) return;
    if (data !== undefined) {
      console.log(`[LOG_VIEW] ${message}: ${JSON.stringify(data, null, 2)}`);
      return;
    }
    console.log(`[LOG_VIEW] ${message}`);
  }

  // Looks up an existing RLA record by ApplicationExtIdentifier__c (Arive sysGUID)
  // then falls back to LOS_ID__c (Arive loan ID).
  private async findExistingRla(applicationExtId: string | null, losId: string): Promise<string | null> {
    if (applicationExtId) {
      const q = `SELECT Id FROM ResidentialLoanApplication__c WHERE ApplicationExtIdentifier__c = '${escapeSoql(applicationExtId)}' LIMIT 1`;
      const result = await this.salesforceAuthClient.query(q);
      if (result.records.length > 0) return toStringValue(result.records[0].Id);
    }

    const q = `SELECT Id FROM ResidentialLoanApplication__c WHERE LOS_ID__c = '${escapeSoql(losId)}' LIMIT 1`;
    const result = await this.salesforceAuthClient.query(q);
    return result.records.length > 0 ? toStringValue(result.records[0].Id) : null;
  }

  private async resolveRlaOwnerId(loan: GenericObject): Promise<string> {
    const loanOriginatorEmail = toStringValue(loan.loanOriginatorEmail);
    if (!loanOriginatorEmail) return DEFAULT_RLA_OWNER_ID;

    const q = `SELECT Id FROM User WHERE Email = '${escapeSoql(loanOriginatorEmail)}' AND IsActive = true LIMIT 1`;
    const result = await this.salesforceAuthClient.query(q);
    const ownerId = result.records.length > 0 ? toStringValue(result.records[0].Id) : null;
    if (!ownerId) {
      logger.warn("Could not resolve Salesforce User from loanOriginatorEmail; using default RLA owner.", {
        loanOriginatorEmail,
        defaultOwnerId: DEFAULT_RLA_OWNER_ID
      });
      return DEFAULT_RLA_OWNER_ID;
    }
    return ownerId;
  }

  private async ensureLeadForLoan(
    event: AriveWebhookEvent,
    loan: GenericObject,
    primaryBorrower: GenericObject | null,
    subjectProperty: GenericObject | null
  ): Promise<void> {
    this.logView("Lead pre-sync check started", { sysGUID: event.sysGUID });
    const borrowerEmail =
      findByKeyValues(primaryBorrower ?? {}, ["email", "emailAddressText"]) ??
      findByKeyValues(loan, ["borrowerEmail", "emailAddressText"]);
    if (!borrowerEmail) {
      logger.warn("Skipping lead pre-create for loan because borrower email is missing.", { sysGUID: event.sysGUID });
      this.logView("Lead pre-sync skipped: borrower email missing", { sysGUID: event.sysGUID });
      return;
    }

    const existingLeads = await this.salesforceAuthClient.query(
      `SELECT Id, Status, IsConverted FROM Lead WHERE Email = '${escapeSoql(borrowerEmail)}' ORDER BY LastModifiedDate DESC LIMIT 10`
    );
    const hasEligibleLead = existingLeads.records.some((record) => {
      const status = toStringValue(record.Status);
      return record.IsConverted === false && status === "Application";
    });
    if (hasEligibleLead) {
      this.logView("Lead pre-sync: eligible existing lead found", {
        sysGUID: event.sysGUID,
        borrowerEmail
      });
      return;
    }

    const ownerId = await this.resolveRlaOwnerId(loan);
    const firstName = toStringValue(primaryBorrower?.firstName);
    const lastName = toStringValue(primaryBorrower?.lastName) ?? "Unknown";
    const householdBaseName = [firstName, lastName].filter(Boolean).join(" ").trim() || "Borrower";
    const streetAddress = toPropertyStringOrNull(subjectProperty?.addressLineText ?? subjectProperty?.street1);
    const stateRaw = toPropertyStringOrNull(subjectProperty?.state);
    const leadPayload: Record<string, unknown> = {
      RecordTypeId: LENDING_BORROWER_RECORD_TYPE_ID,
      External_ID__c: String(event.sysGUID),
      External_System__c: "ARIVE",
      Response_to_Advertising__c: "ARIVE POS",
      Status: "Application",
      LeadSource: "Self Gen",
      Lead_Sub_Source__c: "Auto-Generated",
      OwnerId: ownerId,
      FirstName: firstName,
      LastName: lastName,
      Email: borrowerEmail,
      Phone: findByKeyValues(primaryBorrower ?? {}, ["homePhone", "phone"]),
      MobilePhone: findByKeyValues(primaryBorrower ?? {}, ["mobilePhone10digit", "workPhone"]),
      Company: `${householdBaseName} - Household`,
      Street: streetAddress,
      City: toPropertyStringOrNull(subjectProperty?.city),
      State: mapStateToFullName(stateRaw),
      PostalCode: toPropertyStringOrNull(subjectProperty?.postalCode ?? subjectProperty?.zipCode),
      Property_Address__c: toPropertyStringOrTbd(streetAddress),
      Property_State__c: mapStateToFullName(stateRaw)
    };

    const createResult = await this.salesforceAuthClient.createRecord("Lead", leadPayload);
    if (!createResult.success || !createResult.id) {
      throw new Error(`Failed to create Lead during loan sync: ${JSON.stringify(createResult.errors)}`);
    }
    console.log(
      `[LeadFromLoan] Created lead ${createResult.id} for loan sysGUID=${event.sysGUID} email=${borrowerEmail} before RLA sync.`
    );
    logger.info("Created Lead during loan sync pre-check.", {
      leadId: createResult.id,
      sysGUID: event.sysGUID,
      borrowerEmail
    });
    this.logView("Lead pre-sync created lead", {
      leadId: createResult.id,
      sysGUID: event.sysGUID,
      borrowerEmail,
      leadPayload
    });
  }

  async handleEvent(event: AriveWebhookEvent, context: ProcessingContext): Promise<void> {
    const loan = asObject(context.loanDetails);
    if (!loan) {
      throw new Error("Loan details payload is not an object.");
    }

    const product = asObject(context.productDetails) ?? {};
    const transaction = asObject(context.transactionDetails) ?? {};

    // ── Identifiers ────────────────────────────────────────────────────────────
    const applicationExtId = String(event.sysGUID);
    const losId = toStringValue(loan.ariveLoanId) ?? String(event.sysGUID);
    const lenderLoanIdentifier = toStringValue(loan.lenderLoanIdentifier);
    const ariveLoanIdStr = toStringValue(loan.ariveLoanId);

    // ── Loan status ────────────────────────────────────────────────────────────
    const currentLoanStatus = asObject(loan.currentLoanStatus) ?? {};

    // ── Borrowers ──────────────────────────────────────────────────────────────
    const borrowers = asArray(loan.loanBorrowers);
    const primaryBorrower = asObject(borrowers[0] ?? null);
    const coBorrower = asObject(borrowers[1] ?? null);

    // ── Loan team ──────────────────────────────────────────────────────────────
    const loanTeam = asArray(loan.loanTeam);
    const teamOriginator = findLoanTeamMemberByRole(loanTeam, "Originator");
    const teamProcessor = findLoanTeamMemberByRole(loanTeam, "Processor");
    const teamOriginatorName =
      [toStringValue(teamOriginator?.firstName), toStringValue(teamOriginator?.lastName)].filter(Boolean).join(" ") || null;

    // ── Property ───────────────────────────────────────────────────────────────
    const subjectProperty = asObject(loan.subjectProperty);
    const propertyAddress = composePropertyAddress(subjectProperty);
    const propertyStreetAddress = toPropertyStringOrNull(subjectProperty?.addressLineText ?? subjectProperty?.street1);

    // Ensure a Salesforce Lead exists in expected pre-conversion state before RLA sync.
    if (this.options.leadSyncEnabled) {
      await this.ensureLeadForLoan(event, loan, primaryBorrower, subjectProperty);
    } else {
      this.logView("Lead pre-sync disabled by LEAD_SYNC=No", {
        sysGUID: event.sysGUID,
        triggers: event.triggers
      });
    }

    // ── Key dates ──────────────────────────────────────────────────────────────
    const keyDates = loan.keyDates;
    const tridDate = toDateValue(getKeyDateValue(keyDates, ["tridDate", "TridDate"]));
    const applicationDate = toDateValue(loan.applicationDate ?? loan.createDateTime);
    const initialLeSentDate = toDateValue(getKeyDateValue(keyDates, ["initialLESentDate", "InitialLESentDate"]));
    const mostRecentLeSentDate = toDateValue(getKeyDateValue(keyDates, ["mostRecentLESentDate", "MostRecentLESentDate"]));
    const initialCdSentDate = toDateValue(getKeyDateValue(keyDates, ["initialCDSentDate", "InitialCDSentDate"]));
    const mostRecentCdSentDate = toDateValue(getKeyDateValue(keyDates, ["mostRecentCDSentDate", "MostRecentCDSentDate"]));
    const revisedCdSentDate = toDateValue(getKeyDateValue(keyDates, ["revisedCDSentDate", "RevisedCDSentDate"]));
    const disclosuresSentDate = mostRecentLeSentDate ?? initialLeSentDate;
    const latestCdSentDate = revisedCdSentDate ?? (initialCdSentDate ? mostRecentCdSentDate : null);
    const closingDisclosureSentDate = latestCdSentDate ?? initialCdSentDate;
    const estimatedClosingDate =
      toDateValue(getKeyDateValue(keyDates, ["closingContingency", "ClosingContingency"])) ??
      toDateValue(getKeyDateValue(keyDates, ["earliestClosingDate", "EarliestClosingDate"])) ??
      toDateValue(getKeyDateValue(keyDates, ["estimatedFundingDate", "EstimatedFundingDate"]));

    // ── Loan contacts ──────────────────────────────────────────────────────────
    const contacts = asArray(loan.contacts);
    const titleAgent = findContactWithLowestRef(contacts, "TitleAgent", null);
    const buyerAgent = findContactWithLowestRef(contacts, "RealEstateAgent", "Buyer");
    const sellerAgent = findContactWithLowestRef(contacts, "RealEstateAgent", "Seller");

    // ── Loan financials ────────────────────────────────────────────────────────
    const loanAmount = toNumberValue(loan.loanAmount) ?? toNumberValue(loan.totalLoanAmount);
    const fees = asArray(loan.fees);
    const loanAmountAfterFees = calculateLoanAmountAfterFees(loanAmount, fees);
    const lienPosition = mapLienPosition(transaction.lienPosition ?? loan.lienPosition);
    const topEndDti = toNumberValue(loan.backEndDTI);

    // ── Product & compensation ─────────────────────────────────────────────────
    const mortgageTypeRaw = findByKeyValues(product, ["mortgageType"]) ?? findByKeyValues(loan, ["mortgageType", "loanType"]);
    const productName = findByKeyValues(product, ["productName", "productCode", "lenderProductName"]);
    const loanTerm = toNumberValue(product.loanTerm) ?? toNumberValue(loan.loanTerm) ?? toNumberValue(loan.amortizationTerm);
    const interestRatePercent = toInterestRatePercent(product.rate) ?? toInterestRatePercent(loan.noteRate);
    const firstMortgagePayment = toNumberValue(product.principalAndInterest) ?? toNumberValue(loan.firstMortgagePrincipalAndInterestMonthlyAmt);
    const monthlyMortgageInsurance = toStringValue(product.monthlyMortgageInsurance) ?? toStringValue(loan.mIPremiumMonthlyAmt);
    const firstBrokerFeeAmount = getFirstBrokerFeeAmount(fees);
    const compensationType = resolveCompensationType(findByKeyValues(product, ["compensation"]) ?? findByKeyValues(loan, ["compensationType"]));
    const lenderNameRaw = findByKeyValues(loan, ["lenderName"]) ?? findByKeyValues(product, ["lenderName"]);
    const translatedLenderName = mapLenderName(lenderNameRaw);
    const transactionType = findByKeyValues(loan, ["transactionType", "loanPurpose"]);
    const tridOrApplicationDate = tridDate ?? applicationDate;
    const flatFeeDeduction =
      compensationType === "Lender Paid"
        ? getLenderPaidFlatFeeDeduction(translatedLenderName, transactionType, tridOrApplicationDate)
        : null;
    const calculatedBrokerCompPercent =
      firstBrokerFeeAmount !== null && loanAmount !== null && loanAmount > 0
        ? ((Math.max(firstBrokerFeeAmount - (flatFeeDeduction ?? 0), 0) / loanAmount) * 100)
        : null;

    // ── Salesforce record lookup ───────────────────────────────────────────────
    const existingId = await this.findExistingRla(applicationExtId, losId);
    const rlaOwnerId = !existingId ? await this.resolveRlaOwnerId(loan) : undefined;

    const isLoanArchived = event.triggers.includes("LOAN_ARCHIVED");
    if (isLoanArchived) {
      if (!existingId) {
        logger.warn("Received LOAN_ARCHIVED for unknown ResidentialLoanApplication__c.", {
          applicationExtId,
          losId,
          sysGUID: event.sysGUID
        });
        return;
      }

      const archivedUpdate = await this.salesforceAuthClient.updateRecord("ResidentialLoanApplication__c", existingId, {
        LoanSubStatus__c: "Archived in LOS"
      });
      if (!archivedUpdate.success) {
        throw new Error(`Failed to update ResidentialLoanApplication__c ${existingId}: ${JSON.stringify(archivedUpdate.errors)}`);
      }
      logger.info("Updated ResidentialLoanApplication__c for LOAN_ARCHIVED.", { id: existingId, applicationExtId, losId });
      return;
    }

    const shouldForceFullSync = event.triggers.some((trigger) =>
      ["LOAN_STATUS_CHANGED", "LOAN_DATE_CHANGED", "LOAN_TRACKERS_CHANGED", "LOAN_TRACKERS_UPDATED"].includes(trigger)
    );

    const isLoanStageChanged = event.triggers.includes("LOAN_STAGE_CHANGED");
    if (isLoanStageChanged && !shouldForceFullSync) {
      if (!existingId) {
        logger.warn("Received LOAN_STAGE_CHANGED for unknown ResidentialLoanApplication__c.", {
          applicationExtId,
          losId,
          sysGUID: event.sysGUID
        });
        return;
      }

      const stageCode = resolveAriveStageCode(loan, currentLoanStatus);
      const currentMilestone = mapAriveStageToCurrentMilestone(stageCode);
      const loanSubStatus = mapAriveStageToLoanSubStatus(stageCode);
      const preApprovalDate = shouldSetPreApprovalDate(stageCode) ? new Date().toISOString().slice(0, 10) : undefined;

      if (!currentMilestone && !loanSubStatus && !preApprovalDate) {
        logger.warn("LOAN_STAGE_CHANGED did not include a stage with mapped outputs.", {
          applicationExtId,
          losId,
          sysGUID: event.sysGUID,
          stageCode
        });
        return;
      }
      const stageUpdate = await this.salesforceAuthClient.updateRecord("ResidentialLoanApplication__c", existingId, {
        milestoneCurrentName__c: currentMilestone,
        LoanSubStatus__c: loanSubStatus,
        Pre_Approval_Date__c: preApprovalDate
      });
      this.logView("LOAN_STAGE_CHANGED update payload", {
        existingId,
        stageCode,
        currentMilestone,
        loanSubStatus,
        preApprovalDate
      });
      if (!stageUpdate.success) {
        throw new Error(`Failed to update ResidentialLoanApplication__c ${existingId}: ${JSON.stringify(stageUpdate.errors)}`);
      }
      logger.info("Updated ResidentialLoanApplication__c milestone for LOAN_STAGE_CHANGED.", {
        id: existingId,
        applicationExtId,
        losId,
        stageCode,
        currentMilestone,
        loanSubStatus
      });
      return;
    }

    const isLoanAppSubmitted = event.triggers.includes("LOAN_APP_SUBMITTED");
    const shouldUseBrokerCompDefault =
      event.triggers.includes("LOAN_CREATED") || event.triggers.includes("LOAN_APP_SUBMITTED");
    const resolvedBrokerCompPercent = calculatedBrokerCompPercent ?? (shouldUseBrokerCompDefault ? DEFAULT_BROKER_COMP_PERCENT : null);
    const brokerRevenueFromCompensation =
      toNumberValue(loan.compensation) ??
      toNumberValue(loan.compensationAmount) ??
      toNumberValue(loan.compensationAmt) ??
      toNumberValue(product.compensation);
    const brokerRevenueFromPercent =
      loanAmount !== null && resolvedBrokerCompPercent !== null ? loanAmount * (resolvedBrokerCompPercent / 100) : null;
    const brokerRevenue = brokerRevenueFromCompensation ?? brokerRevenueFromPercent;

    // ── RLA payload ───────────────────────────────────────────────────────────
    const rlaPayload: Record<string, unknown> = {

      // Identifiers
      OwnerId: rlaOwnerId,
      LOS_ID__c: losId,
      ApplicationExtIdentifier__c: applicationExtId,
      Loan_Number__c: lenderLoanIdentifier ?? ariveLoanIdStr,
      API_Details__c: `Loan updated by API ${new Date().toISOString()}`,

      // Loan status
      Status__c: findByKeyValues(currentLoanStatus, ["status"]),
      LoanSubStatus__c: findByKeyValues(currentLoanStatus, ["adverseReason"]),
      LoanSubStatusDate__c: toDateValue(currentLoanStatus.date),
      milestoneCurrentName__c: isLoanAppSubmitted ? "ApplicationSubmitted" : !existingId ? "Started" : undefined,

      // Loan purpose & type
      LoanPurpose__c: mapLoanPurpose(findByKeyValues(loan, ["transactionType", "loanPurpose"])),
      Loan_Product__c: mapMortgageType(mortgageTypeRaw),
      Additional_Loan_Product_Details__c: productName?.slice(0, 255),
      RefinanceType__c: mapRefinanceType(toStringValue(loan.refinanceType)),
      Refinance_Purpose__c: toStringValue(loan.cashoutPurpose),

      // Loan amounts
      Total_Loan_Amount__c: loanAmount,
      Loan_Amount__c: loanAmountAfterFees,

      // Rate & term
      InterestRate__c: interestRatePercent,
      Loan_Term_Months__c: loanTerm,
      loanAmortizationType__c: findByKeyValues(loan, ["amortizationType"]) ?? "Fixed Rate",
      IsInterestOnly__c: toBooleanValue(loan.interestOnlyInd) ?? false,
      IntOnlyTermMonthCount__c: toNumberValue(loan.interestOnlyTermMonthsCount),
      MonthsBeforeFirstAdj__c: toNumberValue(loan.initialFixedPeriodEffectiveMonthsCount),
      MonthsBetweenAdjustments__c: toNumberValue(loan.normalRateAdjustmentPeriod),
      HasPrepaymentPenalty__c: mapPrepayPenalty(toStringValue(loan.prepayPenalty)),

      // LTV / DTI
      Loan_to_Value__c: toSalesforcePercent(loan.ltv),
      Combined_Loan_to_Value__c: toSalesforcePercent(loan.cltv),
      High_Credit_Loan_to_Value__c: toSalesforcePercent(loan.hcltv),
      Top_End_Debt_to_Income__c: toSalesforcePercent(topEndDti),
      Bottom_End_Debt_to_Income__c: toSalesforcePercent(loan.frontEndDTI),

      // Lender & compensation
      Lender_Name__c: translatedLenderName,
      Compensation_Type__c: compensationType,
      Broker_Compensation_Percentage__c: resolvedBrokerCompPercent ?? undefined,
      Listed_Revenue__c: brokerRevenue,

      // Lock
      Lock_Status__c: mapLockStatus(findByKeyValues(loan, ["lockStatus"])),
      Lock_Date__c: toDateValue(loan.lockDate),
      Lock_Expiration_Date__c: toDateValue(loan.lockExpirationDate),

      // Channel & position
      Channelc__c: mapChannel(findByKeyValues(loan, ["industryChannel"])) || "Brokered",
      Lien_Position_Number__c: lienPosition.numberValue,
      Lien_Position__c: lienPosition.textValue,

      // Proposed monthly payments
      Proposed_Payment_First_Mortgage__c: firstMortgagePayment,
      Proposed_Payment_Mortgage_Insurance__c: monthlyMortgageInsurance,
      Proposed_Payment_Property_Taxes__c: toNumberValue(loan.realEstateTaxMonthlyAmt),
      Proposed_Payment_Homeowners_Insurance__c: toNumberValue(loan.homeownersInsuranceMonthlyAmt),
      Proposed_Payment_HOA_Dues__c: toNumberValue(loan.homeownersAssociationDuesAndCondominiumFeesMonthlyAmt),
      Proposed_Payment_Other_Expenses__c: toNumberValue(loan.floodInsuranceMonthlyAmt),
      Total_Proposed_Monthly_Payment__c: toNumberValue(loan.totalMonthlyHousingExpenseAmt),

      // Subject property
      Property_Address__c: toPropertyStringOrTbd(propertyStreetAddress ?? propertyAddress),
      Property_City__c: toPropertyStringOrNull(findByKeyValues(subjectProperty ?? {}, ["city"])),
      Property_State__c: toPropertyStringOrNull(findByKeyValues(subjectProperty ?? {}, ["state"])),
      Property_Zip_Code__c: toPropertyStringOrNull(findByKeyValues(subjectProperty ?? {}, ["postalCode", "zipCode"])),
      Subject_Property_County__c: toStringValue(subjectProperty?.county),
      Subject_Property_Unit_Number__c: toStringValue(subjectProperty?.addressUnitIdentifier),
      Subject_Property_Number_of_Units__c: toNumberValue(subjectProperty?.financedUnitCount),
      Property_Type__c: toStringValue(subjectProperty?.housingType),
      Property_Value__c:
        toNumberValue(subjectProperty?.estimatedValue) ??
        toNumberValue(loan.purchasePriceOrEstimatedValue) ??
        toNumberValue(subjectProperty?.salesContractAmt),
      Subject_Property_Purchase_Price__c: toNumberValue(subjectProperty?.salesContractAmt),
      Subject_Property_Down_Payment_Amount__c: toNumberValue(loan.downPayment),
      Occupancy_Type__c: mapOccupancy(findByKeyValues(subjectProperty ?? {}, ["propertyUsageType", "occupancyType"])),
      Title_Company_File_Number__c: null,

      // Purchase details
      Seller_Credit_Amount__c: toNumberValue(loan.sellerCredit),
      Settlement_Case_Number__c: toStringValue(loan.settlementNumber),
      Waive_Escrows__c: toStringValue(loan.impoundWaiver),

      // Financial misc
      Expected_Discount_Rebate_Points__c: toNumberValue(loan.discountPoints),
      Credit_Score__c: toNumberValue(loan.fico),

      // Application dates
      Date_File_Started__c: applicationDate,
      Application_Date__c: applicationDate ?? tridDate,
      First_Time_Homebuyer__c: toBooleanValue(loan.firstTimeHomeBuyer) ?? toBooleanValue(primaryBorrower?.firstTimeHomeBuyer) ?? false,

      // Key dates
      Appraisal_Order_Date__c: toDateValue(getKeyDateValue(keyDates, ["appraisalOrderedDate", "AppraisalOrderedDate"])),
      Appraisal_Received_Date__c: toDateValue(getKeyDateValue(keyDates, ["appraisalDeliveryDate", "AppraisalDeliveryDate"])),
      Appraisal_Contingency_Date__c: toDateValue(getKeyDateValue(keyDates, ["appraisalContingency", "AppraisalContingency"])),
      Approval_Contingency_Date__c: toDateValue(getKeyDateValue(keyDates, ["loanContingency", "LoanContingency"])),
      Title_Order_Date__c: toDateValue(getKeyDateValue(keyDates, ["titleOrderedDate", "TitleOrderedDate"])),
      Purchase_Contract_Date__c: toDateValue(getKeyDateValue(keyDates, ["salesContractDate", "ContractDate", "SalesContractDate"])),
      Disclosures_Sent_Date__c: disclosuresSentDate,
      Closing_Disclosure_Send_Date__c: closingDisclosureSentDate,
      Estimated_Closing_Date__c: estimatedClosingDate,
      Closing_Date__c: toDateValue(getKeyDateValue(keyDates, ["closingContingency", "ClosingContingency"])),
      Initial_CD_Signature_Date__c: toDateValue(getKeyDateValue(keyDates, ["initialCDSignedDate", "InitialCDSignedDate"])),
      Initial_Credit_Pull_Date__c: toDateValue(getKeyDateValue(keyDates, ["creditOrderDate", "CreditOrderDate"])),
      Last_Credit_Pull_Date__c: toDateValue(getKeyDateValue(keyDates, ["creditExpirationDate", "CreditExpirationDate"])),

      // Contacts (Title Agent, Buyer Agent, Seller Agent)
      Title_Company_Name__c: toStringValue(titleAgent?.companyName),
      Title_Company_Email_Address__c: toStringValue(titleAgent?.email),
      Buyer_Agent_Company__c: toStringValue(buyerAgent?.companyName),
      Buyer_Agent_Email__c: toStringValue(buyerAgent?.email),
      Buyer_Agent_Phone__c: toStringValue(buyerAgent?.phone),
      Buyer_Agent_Name__c: buildFullName(buyerAgent?.firstName, buyerAgent?.lastName),
      Buyer_s_Real_Estate_Agent__c: buyerAgent ? undefined : null,
      Seller_Agent_Company__c: toStringValue(sellerAgent?.companyName),
      Seller_Agent_Email__c: toStringValue(sellerAgent?.email),
      Seller_Agent_Phone__c: toStringValue(sellerAgent?.phone),
      Seller_Agent_Name__c: buildFullName(sellerAgent?.firstName, sellerAgent?.lastName),
      Seller_s_Real_Estate_Agent__c: sellerAgent ? undefined : null,

      // Loan team
      Loan_Officer__c: findByKeyValues(loan, ["loanOriginatorName"]) ?? teamOriginatorName,
      Loan_Officer_Email__c: findByKeyValues(loan, ["loanOriginatorEmail"]) ?? findByKeyValues(teamOriginator ?? {}, ["emailAddressText", "email"]),
      Loan_Officer_Assistant_Email__c: findByKeyValues(loan, ["loanOfficerAssistantEmail"]),
      Processor_Email__c: findByKeyValues(loan, ["loanProcessorEmail"]) ?? findByKeyValues(teamProcessor ?? {}, ["emailAddressText", "email"]),

      // Borrower fields (see buildPrimaryBorrowerFields / buildCoBorrowerFields above)
      ...buildPrimaryBorrowerFields(primaryBorrower),
      ...buildCoBorrowerFields(coBorrower)
    };
    this.logView("RLA payload built", {
      existingId,
      sysGUID: event.sysGUID,
      triggers: event.triggers,
      payload: rlaPayload
    });

    if (!existingId) {
      this.logView("RLA create path", { sysGUID: event.sysGUID, applicationExtId, losId });
      const createResult = await this.salesforceAuthClient.createRecord("ResidentialLoanApplication__c", rlaPayload);
      if (!createResult.success || !createResult.id) {
        throw new Error(`Failed to create ResidentialLoanApplication__c: ${JSON.stringify(createResult.errors)}`);
      }
      logger.info("Created ResidentialLoanApplication__c.", { id: createResult.id, applicationExtId, losId });
      // await this.syncFees(createResult.id, fees);
      // await this.syncConditions(createResult.id, losId, asArray(loan.conditions));
      return;
    }

    this.logView("RLA update path", { id: existingId, sysGUID: event.sysGUID, applicationExtId, losId });
    const updateResult = await this.salesforceAuthClient.updateRecord("ResidentialLoanApplication__c", existingId, rlaPayload);
    if (!updateResult.success) {
      throw new Error(`Failed to update ResidentialLoanApplication__c ${existingId}: ${JSON.stringify(updateResult.errors)}`);
    }
    logger.info("Updated ResidentialLoanApplication__c.", { id: existingId, applicationExtId, losId });
    // await this.syncFees(existingId, fees);
    // await this.syncConditions(existingId, losId, asArray(loan.conditions));
  }

  // Fee__c and Condition__c sync disabled — revisit when Arive fee/condition field names are confirmed.

  // private async syncFees(rlaId: string, fees: unknown[]): Promise<void> { ... }
  // private async syncConditions(rlaId: string, losId: string, conditions: unknown[]): Promise<void> { ... }
}
