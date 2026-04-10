import type SalesforceAuth from "../api-client/salesforceAuth.js";
import type { AriveWebhookEvent } from "../events/arive-event.js";
import { logger } from "../src/logger.js";
import type { OutboundSystemService, ProcessingContext } from "./outbound-system-service.js";

type GenericObject = Record<string, unknown>;

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
  if (typeof value === "string") {
    if (value.toLowerCase() === "true") {
      return true;
    }
    if (value.toLowerCase() === "false") {
      return false;
    }
  }
  return null;
}

function toDateValue(value: unknown): string | null {
  const str = toStringValue(value);
  if (!str) {
    return null;
  }
  const candidate = str.includes("T") ? str.split("T")[0] : str;
  return /^\d{4}-\d{2}-\d{2}$/.test(candidate) ? candidate : null;
}

function escapeSoql(value: string): string {
  return value.replace(/\\/g, "\\\\").replace(/'/g, "\\'");
}

function findByKeyValues(root: GenericObject, keys: string[]): string | null {
  for (const key of keys) {
    const value = toStringValue(root[key]);
    if (value) {
      return value;
    }
  }
  return null;
}

function mapLoanPurpose(raw: string | null): string | null {
  if (!raw) {
    return null;
  }
  if (raw === "Purchase" || raw === "PreApproval") {
    return "Purchase";
  }
  if (raw === "RefinanceRateandTerm") {
    return "Rate/Term Refinance";
  }
  if (raw === "RefinanceCashOut") {
    return "Cash-Out Refinance";
  }
  return "Other";
}

function mapLenderName(raw: string | null): string | null {
  if (!raw) {
    return null;
  }
  if (raw === "Undefined") {
    return null;
  }
  if (raw === "Jmac Lending") {
    return "JMAC Lending";
  }
  if (raw === "Pennymac" || raw === "PennyMac") {
    return "PennyMac Financial";
  }
  if (raw === "Rocket Mortgage") {
    return "Rocket Pro TPO";
  }
  if (raw === "Sierra Pacific") {
    return "Sierra Pacific Mortgage";
  }
  return raw;
}

function mapMortgageType(raw: string | null): string | null {
  if (!raw) {
    return null;
  }
  const lower = raw.toLowerCase();
  if (raw === "Conforming") {
    return "Conventional";
  }
  if (lower.includes("fha")) {
    return "FHA";
  }
  if (lower.includes("va")) {
    return "VA";
  }
  if (lower.includes("usda")) {
    return "USDA";
  }
  if (lower.includes("nonqm") || lower.includes("nonconforming") || lower.includes("non-conforming") || lower.includes("non-qm")) {
    return "Non-QM";
  }
  if (raw === "Second Mortgage") {
    return "Other";
  }
  if (raw === "HELOAN") {
    return "HELOC";
  }
  if (raw === "Unknown") {
    return "";
  }
  return raw;
}

function mapLockStatus(raw: string | null): string {
  if (!raw) {
    return "Not Locked";
  }
  if (raw.toLowerCase() === "none") {
    return "Not Locked";
  }
  return raw;
}

function mapChannel(raw: string | null): string {
  if (!raw) {
    return "Brokered";
  }
  if (raw.toLowerCase() === "broker") {
    return "Brokered";
  }
  return raw;
}

function mapOccupancy(raw: string | null): string {
  if (!raw) {
    return "Primary";
  }
  if (raw === "PrimaryResidence") {
    return "Primary";
  }
  return raw;
}

function mapLienPosition(raw: unknown): { numberValue: string | null; textValue: string | null } {
  const numeric = toNumberValue(raw);
  if (numeric === 1) {
    return { numberValue: "1", textValue: "FirstLien" };
  }
  if (numeric === 2) {
    return { numberValue: "2", textValue: "SecondLien" };
  }
  const str = toStringValue(raw);
  if (str === "FirstLien") {
    return { numberValue: "1", textValue: "FirstLien" };
  }
  if (str === "SecondLien") {
    return { numberValue: "2", textValue: "SecondLien" };
  }
  return { numberValue: null, textValue: null };
}

function isJaneDoe(firstName: string | null, lastName: string | null): boolean {
  const full = `${firstName ?? ""} ${lastName ?? ""}`.trim().toLowerCase();
  return full === "jane doe";
}

function calculateLoanAmountAfterFees(loanAmount: number | null, fees: unknown[]): number | null {
  if (loanAmount === null) {
    return null;
  }
  let adjusted = loanAmount;
  const processedTypes = new Set<string>();

  for (const feeRaw of fees) {
    const fee = asObject(feeRaw);
    if (!fee) {
      continue;
    }
    const feeType = toStringValue(fee.feeType);
    if (!feeType || processedTypes.has(feeType)) {
      continue;
    }
    if (feeType !== "FhaUpfrontMortgageInsurancePremium" && feeType !== "VaFundingFee") {
      continue;
    }
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

function composePropertyAddress(subjectProperty: GenericObject | null): string | null {
  if (!subjectProperty) {
    return null;
  }

  const line1 = toStringValue(subjectProperty.addressLineText ?? subjectProperty.street1);
  const city = toStringValue(subjectProperty.city);
  const state = toStringValue(subjectProperty.state);
  const postal = toStringValue(subjectProperty.postalCode ?? subjectProperty.zipCode);

  const parts = [line1, city, state, postal].filter((part) => Boolean(part)) as string[];
  if (parts.length === 0) {
    return null;
  }
  return parts.join(", ");
}

function getAttributeMap(loan: GenericObject): Record<string, string> {
  const map: Record<string, string> = {};
  const attributes = asArray(loan.attributes);
  for (const attributeRaw of attributes) {
    const attribute = asObject(attributeRaw);
    if (!attribute) {
      continue;
    }
    const name = toStringValue(attribute.name);
    const value = toStringValue(attribute.value);
    if (name && value) {
      map[name] = value;
    }
  }
  return map;
}

function resolveCompensationType(raw: string | null): string {
  if (!raw) {
    return "Lender Paid";
  }
  if (raw.toLowerCase().includes("lender")) {
    return "Lender Paid";
  }
  if (raw.toLowerCase().includes("borrower")) {
    return "Borrower Paid";
  }
  return "Lender Paid";
}

function getFirstBrokerFeeAmount(fees: unknown[]): number | null {
  for (const feeRaw of fees) {
    const fee = asObject(feeRaw);
    if (!fee) {
      continue;
    }
    if (toStringValue(fee.feeType) !== "BrokerFee") {
      continue;
    }
    const amount = [
      fee.borrowerPacAmount,
      fee.borrowerPocAmount,
      fee.sellerPacAmount,
      fee.sellerPocAmount,
      fee.othersPaidAmount
    ]
      .map((value) => toNumberValue(value) ?? 0)
      .reduce((acc, curr) => acc + curr, 0);
    if (amount > 0) {
      return amount;
    }
  }
  return null;
}

function getBrokerCompensationString(fees: unknown[]): string | null {
  const values: string[] = [];
  for (const feeRaw of fees) {
    const fee = asObject(feeRaw);
    if (!fee) {
      continue;
    }
    const loanFeeId = toStringValue(fee.loanFeeId);
    if (loanFeeId !== "1917" && loanFeeId !== "1889") {
      continue;
    }
    const feeBuilderArgs = toStringValue(fee.feeBuilderArgs);
    if (feeBuilderArgs) {
      values.push(feeBuilderArgs);
    }
  }
  if (values.length === 0) {
    return null;
  }
  return values.join("; ").slice(0, 32768);
}

export class SalesforceLoanApplicationSyncService implements OutboundSystemService {
  readonly name = "salesforce-sync";

  constructor(private readonly salesforceAuthClient: SalesforceAuth) {}

  private async findExistingRlaIds(sonarGuid: string | null, externalId: string | null, losId: string): Promise<string | null> {
    if (sonarGuid) {
      const q = `SELECT Id FROM ResidentialLoanApplication__c WHERE Sonar_GUID__c = '${escapeSoql(sonarGuid)}' LIMIT 1`;
      const result = await this.salesforceAuthClient.query(q);
      if (result.records.length > 0) {
        return toStringValue(result.records[0].Id);
      }
    }

    if (externalId) {
      const q = `SELECT Id FROM ResidentialLoanApplication__c WHERE ApplicationExtIdentifier__c = '${escapeSoql(externalId)}' AND LOS_ID__c = '${escapeSoql(losId)}' LIMIT 1`;
      const result = await this.salesforceAuthClient.query(q);
      if (result.records.length > 0) {
        return toStringValue(result.records[0].Id);
      }
    }

    const q = `SELECT Id FROM ResidentialLoanApplication__c WHERE LOS_ID__c = '${escapeSoql(losId)}' LIMIT 1`;
    const result = await this.salesforceAuthClient.query(q);
    return result.records.length > 0 ? toStringValue(result.records[0].Id) : null;
  }

  async handleEvent(event: AriveWebhookEvent, context: ProcessingContext): Promise<void> {
    const loan = asObject(context.loanDetails);
    if (!loan) {
      throw new Error("Loan details payload is not an object.");
    }

    const product = asObject(context.productDetails) ?? {};
    const transaction = asObject(context.transactionDetails) ?? {};

    const externalId = findByKeyValues(loan, ["externalId", "sysGUID"]);
    const losId = findByKeyValues(loan, ["loanId", "ariveLoanId"]) ?? String(event.sysGUID);
    const sonarGuid = externalId ? `${externalId}_${losId}` : losId;
    const loanNumber = findByKeyValues(loan, ["loanNumber", "lenderLoanIdentifier"]);

    const loanAmount = toNumberValue(loan.loanAmount) ?? toNumberValue(loan.totalLoanAmount);
    const fees = asArray(loan.fees);
    const loanAmountAfterFees = calculateLoanAmountAfterFees(loanAmount, fees);
    const attributeMap = getAttributeMap(loan);

    const borrowers = asArray(loan.loanBorrowers ?? loan.borrowers);
    const primaryBorrower = (borrowers[0] ? asObject(borrowers[0]) : null) ?? null;
    const coBorrower = (borrowers[1] ? asObject(borrowers[1]) : null) ?? null;

    const primaryFirstName = toStringValue(primaryBorrower?.firstName);
    const primaryLastName = toStringValue(primaryBorrower?.lastName);
    const coFirstName = toStringValue(coBorrower?.firstName);
    const coLastName = toStringValue(coBorrower?.lastName);

    const transactionType = findByKeyValues(loan, ["transactionType", "loanPurpose"]);
    const lienPosition = mapLienPosition(transaction.lienPosition ?? loan.lienPosition);
    const wireReceivedDate = toDateValue(loan.wireReceivedDate);
    const applicationDate = toDateValue(loan.applicationDate ?? loan.createDateTime);
    const currentLoanStatus = asObject(loan.currentLoanStatus) ?? {};
    const subjectProperty = asObject(loan.subjectProperty);
    const keyDates = asObject(loan.keyDates);

    const lenderNameRaw = findByKeyValues(product, ["lenderName"]) ?? findByKeyValues(loan, ["lenderName"]);
    const translatedLenderName = mapLenderName(lenderNameRaw);
    const mortgageTypeRaw = findByKeyValues(product, ["mortgageType"]) ?? findByKeyValues(loan, ["mortgageType", "loanType"]);
    const mappedMortgageType = mapMortgageType(mortgageTypeRaw);
    const productName = findByKeyValues(product, ["productName", "productCode", "lenderProductName"]);
    const tridDate = toDateValue(keyDates?.tridDate);
    const propertyAddress = composePropertyAddress(subjectProperty);
    const lockStatus = mapLockStatus(findByKeyValues(loan, ["lockStatus"]));
    const channel = mapChannel(findByKeyValues(loan, ["industryChannel"]));
    const occupancy = mapOccupancy(findByKeyValues(subjectProperty ?? {}, ["propertyUsageType"]));
    const topEndDti = toNumberValue(loan.dtiRatio) ?? toNumberValue(loan.backEndDTI);
    const firstBrokerFeeAmount = getFirstBrokerFeeAmount(fees);
    const brokerRevenue = firstBrokerFeeAmount ?? toNumberValue(loan.brokerFee) ?? toNumberValue(loan.grossLoanRevenue);
    const compensationType = resolveCompensationType(findByKeyValues(product, ["compensation"]) ?? findByKeyValues(loan, ["compensationType"]));
    const calculatedCompPercentage =
      firstBrokerFeeAmount !== null && loanAmount !== null && loanAmount > 0
        ? (firstBrokerFeeAmount / loanAmount) * 100
        : 2.375;
    const brokerCompensationString = getBrokerCompensationString(fees);
    const firstMortgagePayment =
      toNumberValue(product.principalAndInterest) ?? toNumberValue(loan.firstMortgagePrincipalAndInterestMonthlyAmt);
    const monthlyMortgageInsurance =
      toStringValue(product.monthlyMortgageInsurance) ?? toStringValue(loan.mIPremiumMonthlyAmt);
    const loanTerm = toNumberValue(product.loanTerm) ?? toNumberValue(loan.loanTerm) ?? toNumberValue(loan.amortizationTerm);
    const interestRatePercent =
      toNumberValue(product.rate) !== null
        ? (toNumberValue(product.rate) as number) * 100
        : toNumberValue(loan.noteRate) !== null
          ? (toNumberValue(loan.noteRate) as number) * 100
          : null;

    const existingId = await this.findExistingRlaIds(sonarGuid, externalId, losId);

    const rlaPayload: Record<string, unknown> = {
      LOS_ID__c: losId,
      Sonar_GUID__c: sonarGuid,
      ApplicationExtIdentifier__c: externalId,
      Compensation_Type__c: compensationType,
      Broker_Compensation_Percentage__c: calculatedCompPercentage,
      Lock_Status__c: lockStatus,
      Channelc__c: channel || "Brokered",
      loanAmortizationType__c: findByKeyValues(loan, ["amortizationType"]) ?? "Fixed Rate",
      Occupancy_Type__c: occupancy,
      Loan_Number__c: attributeMap.LenderLoanNumber ?? loanNumber,
      Title_Company_File_Number__c: attributeMap.TitleCompanyFileNumber ?? null,
      Lien_Position_Number__c: lienPosition.numberValue,
      Lien_Position__c: lienPosition.textValue,
      Total_Loan_Amount__c: loanAmount,
      Loan_Amount__c: loanAmountAfterFees,
      Status__c: findByKeyValues(loan, ["loanStatus"]) ?? findByKeyValues(currentLoanStatus, ["status"]),
      LoanSubStatus__c:
        findByKeyValues(loan, ["loanStatusReason"]) ?? findByKeyValues(currentLoanStatus, ["adverseReason"]),
      Date_File_Started__c: applicationDate,
      Application_Date__c: applicationDate ?? tridDate,
      Loan_to_Value__c: toNumberValue(loan.ltv) ?? toNumberValue(loan.ltvRatio),
      Combined_Loan_to_Value__c: toNumberValue(loan.cltv) ?? toNumberValue(loan.cltvRatio),
      Top_End_Debt_to_Income__c: topEndDti,
      API_Details__c: `Loan updated by API ${new Date().toISOString()}`,
      Lender_Name__c: translatedLenderName,
      Loan_Product__c: mappedMortgageType,
      Additional_Loan_Product_Details__c: productName?.slice(0, 255),
      Wire_Check_Amount_Received__c: toNumberValue(loan.wireAmountReceived),
      Wire_Check_Date_Received__c: wireReceivedDate,
      LoanPurpose__c: mapLoanPurpose(transactionType),
      First_Time_Homebuyer__c: toBooleanValue(loan.firstTimeHomeBuyer) ?? toBooleanValue(primaryBorrower?.firstTimeHomeBuyer) ?? false,
      InterestRate__c: interestRatePercent,
      Loan_Term_Months__c: loanTerm,
      Proposed_Payment_First_Mortgage__c: firstMortgagePayment,
      Proposed_Payment_Mortgage_Insurance__c: monthlyMortgageInsurance,
      Proposed_Payment_Property_Taxes__c: toNumberValue(loan.realEstateTaxMonthlyAmt),
      Proposed_Payment_Homeowners_Insurance__c: toNumberValue(loan.homeownersInsuranceMonthlyAmt),
      Proposed_Payment_HOA_Dues__c: toNumberValue(loan.homeownersAssociationDuesAndCondominiumFeesMonthlyAmt),
      Proposed_Payment_Other_Expenses__c: toNumberValue(loan.floodInsuranceMonthlyAmt),
      Total_Proposed_Monthly_Payment__c: toNumberValue(loan.totalMonthlyHousingExpenseAmt),
      Property_Address__c: propertyAddress,
      Processor_Email__c: findByKeyValues(loan, ["loanProcessorEmail"]),
      Listed_Revenue__c: brokerRevenue,
      Broker_Compensation_String__c: brokerCompensationString,
      Property_Value__c:
        toNumberValue(subjectProperty?.estimatedValue) ??
        toNumberValue(loan.purchasePriceOrEstimatedValue) ??
        toNumberValue(subjectProperty?.salesContractAmt),
      BorrowerFirstNamec__c: isJaneDoe(primaryFirstName, primaryLastName) ? null : primaryFirstName,
      BorrowerLastNamec__c: isJaneDoe(primaryFirstName, primaryLastName) ? null : primaryLastName,
      BorrowerEmailc__c: isJaneDoe(primaryFirstName, primaryLastName)
        ? null
        : findByKeyValues(primaryBorrower ?? {}, ["email", "emailAddressText"]),
      BorrowerHomePhonec__c: isJaneDoe(primaryFirstName, primaryLastName)
        ? null
        : findByKeyValues(primaryBorrower ?? {}, ["phone", "mobilePhone10digit", "homePhone"]),
      BorrowerSSNc__c: isJaneDoe(primaryFirstName, primaryLastName)
        ? null
        : findByKeyValues(primaryBorrower ?? {}, ["taxIdentifier"]),
      Borrowerdobc__c: isJaneDoe(primaryFirstName, primaryLastName) ? null : toDateValue(primaryBorrower?.dateOfBirth ?? primaryBorrower?.birthDate),
      BorrowerMaritalStatusc__c: isJaneDoe(primaryFirstName, primaryLastName)
        ? null
        : findByKeyValues(primaryBorrower ?? {}, ["maritalStatus", "maritalStatusType"]),
      CoBorrowerFirstNamec__c: isJaneDoe(coFirstName, coLastName) ? null : coFirstName,
      CoBorrowerLastNamec__c: isJaneDoe(coFirstName, coLastName) ? null : coLastName,
      CoBorrowerEmailc__c: isJaneDoe(coFirstName, coLastName) ? null : findByKeyValues(coBorrower ?? {}, ["email", "emailAddressText"]),
      CoBorrowerCellc__c: isJaneDoe(coFirstName, coLastName)
        ? null
        : findByKeyValues(coBorrower ?? {}, ["phone", "mobilePhone10digit", "homePhone"]),
      CoBorrowerdobc__c: isJaneDoe(coFirstName, coLastName) ? null : toDateValue(coBorrower?.dateOfBirth ?? coBorrower?.birthDate),
      HasCoborrowerc__c: Boolean(coBorrower),
      CoBorrowerAuthorizedCreditReportc__c: Boolean(coBorrower),
      CoBorrowerMortgageServicec__c: Boolean(coBorrower),
      CoBorrowerDaysConsentc__c: coBorrower ? undefined : null,
      CoBorrowerEquifaxc__c: coBorrower ? undefined : null,
      CoBorrowerExperianFICOc__c: coBorrower ? undefined : null,
      CoBorrowerHomePhonec__c: coBorrower ? undefined : null,
      CoBorrowerMaritalStatusc__c: coBorrower ? undefined : null,
      CoBorrowerMinimumFICOc__c: coBorrower ? undefined : null,
      CoBorrowerSSNc__c: coBorrower ? undefined : null,
      CoBorrowerTransactionPurposec__c: coBorrower ? undefined : null,
      CoBorrowerTransUnionc__c: coBorrower ? undefined : null
    };

    if (!existingId) {
      const createResult = await this.salesforceAuthClient.createRecord("ResidentialLoanApplication__c", rlaPayload);
      if (!createResult.success || !createResult.id) {
        throw new Error(`Failed to create ResidentialLoanApplication__c: ${JSON.stringify(createResult.errors)}`);
      }
      logger.info("Created ResidentialLoanApplication__c.", { id: createResult.id, sonarGuid, losId });
      await this.syncFees(createResult.id, fees);
      await this.syncConditions(createResult.id, losId, asArray(loan.conditions));
      return;
    }

    const updateResult = await this.salesforceAuthClient.updateRecord("ResidentialLoanApplication__c", existingId, rlaPayload);
    if (!updateResult.success) {
      throw new Error(`Failed to update ResidentialLoanApplication__c ${existingId}: ${JSON.stringify(updateResult.errors)}`);
    }
    logger.info("Updated ResidentialLoanApplication__c.", { id: existingId, sonarGuid, losId });
    await this.syncFees(existingId, fees);
    await this.syncConditions(existingId, losId, asArray(loan.conditions));
  }

  private async syncFees(rlaId: string, fees: unknown[]): Promise<void> {
    for (const feeRaw of fees) {
      const fee = asObject(feeRaw);
      if (!fee) {
        continue;
      }
      const sonarFeeId = toStringValue(fee.loanFeeId);
      if (!sonarFeeId) {
        continue;
      }

      const existing = await this.salesforceAuthClient.query(
        `SELECT Id FROM Fee__c WHERE Sonar_Fee_ID__c = '${escapeSoql(sonarFeeId)}' AND Residential_Loan_Application__c = '${escapeSoql(rlaId)}' LIMIT 1`
      );

      const totalAmount = [fee.borrowerPacAmount, fee.borrowerPocAmount, fee.sellerPacAmount, fee.sellerPocAmount, fee.othersPaidAmount]
        .map((v) => toNumberValue(v) ?? 0)
        .reduce((acc, curr) => acc + curr, 0);

      const payload: Record<string, unknown> = {
        Residential_Loan_Application__c: rlaId,
        Sonar_Fee_ID__c: sonarFeeId,
        Fee_Type__C: toStringValue(fee.feeType),
        Fee_Section__c: toStringValue(fee.section),
        Total_Amount__c: totalAmount,
        Amount_Paid_Borrower_at_Closing__c: toNumberValue(fee.borrowerPacAmount),
        Amount_Paid_Borrower_Outside_of_Closing__c: toNumberValue(fee.borrowerPocAmount),
        Amount_Paid_Seller_at_Closing__c: toNumberValue(fee.sellerPacAmount),
        Amount_Paid_Seller_Outside_of_Closing__c: toNumberValue(fee.sellerPocAmount),
        Amount_Paid_by_Others__c: toNumberValue(fee.othersPaidAmount),
        Paid_To__c: toStringValue(fee.paidTo),
        Paid_By__c: toStringValue(fee.paidBy),
        Borrower_Can_Shop__c: toBooleanValue(fee.borrowerCanShop),
        Borrower_Did_Shop__c: toBooleanValue(fee.borrowerDidShop),
        Borrower_Did_Select__c: toBooleanValue(fee.borrowerDidSelect),
        Include_in_APR__c: toBooleanValue(fee.includeInApr),
        Can_Finance__c: toBooleanValue(fee.canFinance),
        Refundable__c: toBooleanValue(fee.refundable),
        Optional__c: toBooleanValue(fee.optional)
      };

      if (existing.records.length > 0 && toStringValue(existing.records[0].Id)) {
        await this.salesforceAuthClient.updateRecord("Fee__c", toStringValue(existing.records[0].Id) as string, payload);
      } else {
        await this.salesforceAuthClient.createRecord("Fee__c", payload);
      }
    }
  }

  private async syncConditions(rlaId: string, losId: string, conditions: unknown[]): Promise<void> {
    for (const condRaw of conditions) {
      const condition = asObject(condRaw);
      if (!condition) {
        continue;
      }
      const conditionId = toStringValue(condition.loanConditionId);
      if (!conditionId) {
        continue;
      }

      const existing = await this.salesforceAuthClient.query(
        `SELECT Id FROM Condition__c WHERE Sonar_Condition_ID__c = '${escapeSoql(conditionId)}' AND Sonar_Loan_ID__c = '${escapeSoql(losId)}' LIMIT 1`
      );

      const payload: Record<string, unknown> = {
        Residential_Loan_Application__c: rlaId,
        Sonar_Condition_ID__c: conditionId,
        Sonar_Loan_ID__c: losId,
        Sonar_Borrower_Reference_ID__c: toStringValue(condition.borrowerRef),
        Title__c: toStringValue(condition.name)?.slice(0, 255),
        Description__c: toStringValue(condition.description),
        Scope__c: toStringValue(condition.scope),
        Type__c: toStringValue(condition.type),
        Completed__c: toBooleanValue(condition.completed),
        Critical__c: toBooleanValue(condition.critical)
      };

      if (existing.records.length > 0 && toStringValue(existing.records[0].Id)) {
        await this.salesforceAuthClient.updateRecord("Condition__c", toStringValue(existing.records[0].Id) as string, payload);
      } else {
        await this.salesforceAuthClient.createRecord("Condition__c", payload);
      }
    }
  }
}
