import type SalesforceAuth from "../api-client/salesforceAuth.js";
import type { AriveWebhookEvent } from "../events/arive-event.js";
import { logger } from "../src/logger.js";
import type { OutboundSystemService, ProcessingContext } from "./outbound-system-service.js";

type GenericObject = Record<string, unknown>;

const LENDING_BORROWER_RECORD_TYPE_ID = "0125w000000BR5PAAW";

function asObject(value: unknown): GenericObject | null {
  return value && typeof value === "object" && !Array.isArray(value) ? (value as GenericObject) : null;
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

function escapeSoql(value: string): string {
  return value.replace(/\\/g, "\\\\").replace(/'/g, "\\'");
}

function findByKeyValues(root: GenericObject, keys: string[]): string | null {
  for (const key of keys) {
    const value = toStringValue(root[key]);
    if (value) return value;
  }
  return null;
}

function normalizeLeadSource(raw: string | null): string | null {
  if (!raw) return null;
  const normalized = raw.trim().toLowerCase();
  if (normalized === "company provided" || normalized === "company") return "Company";
  // LeadSource picklist is restricted to Self Gen / Company.
  return "Self Gen";
}

function normalizeLeadSubSource(raw: string | null, leadSource: string | null, rawLeadSource: string | null): string | null {
  const candidate = raw ?? rawLeadSource;
  if (!candidate) {
    // Only force Auto-Generated when no source/sub-source value is present.
    return leadSource ? null : "Auto-Generated";
  }

  const normalized = candidate.trim().toLowerCase();
  if (normalized === "real estate agent") return "Real Estate Agent";
  if (normalized === "social media") return "Social Media";
  if (normalized === "referral - friend / family") return "Family / Friend Referral";
  if (normalized === "return client") return "Past Client";

  // Unknown values should not be pushed into restricted picklists.
  return null;
}

function buildLeadPayload(lead: GenericObject, event: AriveWebhookEvent): Record<string, unknown> {
  const borrower = asObject(lead.borrower) ?? {};
  const borrowerResidence = asObject(borrower.currentResidence);
  const subjectProperty = asObject(lead.subjectProperty) ?? {};
  const address =
    borrowerResidence ??
    asObject(lead.address) ??
    asObject(lead.mailingAddress) ??
    asObject(lead.propertyAddress) ??
    subjectProperty ??
    {};

  const rawLeadSource = findByKeyValues(lead, ["leadSource", "source", "sourceType", "leadProvidedBy"]);
  const leadSource = normalizeLeadSource(rawLeadSource);
  const leadSubSource = normalizeLeadSubSource(
    findByKeyValues(lead, ["leadSubSource", "subSource", "sourceDetail", "leadSubSourceType", "otherSourceDesc"]),
    leadSource,
    rawLeadSource
  );

  const firstName = findByKeyValues(lead, ["firstName", "borrowerFirstName", "applicantFirstName"]) ?? toStringValue(borrower.firstName);
  const lastName =
    findByKeyValues(lead, ["lastName", "borrowerLastName", "applicantLastName"]) ?? toStringValue(borrower.lastName) ?? "Unknown";

  return {
    RecordTypeId: LENDING_BORROWER_RECORD_TYPE_ID,
    External_ID__c: String(event.sysGUID),
    External_System__c: "ARIVE",
    Response_to_Advertising__c: "ARIVE POS",
    Status: "New",
    LeadSource: leadSource,
    Lead_Sub_Source__c: leadSubSource,
    FirstName: firstName,
    LastName: lastName,
    Email: findByKeyValues(lead, ["email", "emailAddress", "emailAddressText"]) ?? findByKeyValues(borrower, ["email", "emailAddressText"]),
    Phone: findByKeyValues(lead, ["phone", "homePhone", "phoneNumber"]) ?? findByKeyValues(borrower, ["homePhone10digit", "homePhone"]),
    MobilePhone:
      findByKeyValues(lead, ["mobilePhone", "mobilePhone10digit", "cellPhone", "workPhone"]) ??
      findByKeyValues(borrower, ["mobilePhone10digit", "cellPhone", "CellPhone"]),
    Company: findByKeyValues(lead, ["companyName", "company"]) ?? "Borrower",
    Street:
      findByKeyValues(address, ["addressLineText", "street1", "street", "line1", "lineText"]) ??
      findByKeyValues(subjectProperty, ["lineText", "addressLineText"]),
    City: findByKeyValues(address, ["city", "addressCity"]) ?? findByKeyValues(subjectProperty, ["city"]),
    State: findByKeyValues(address, ["state", "addressState"]) ?? findByKeyValues(subjectProperty, ["state"]),
    PostalCode:
      findByKeyValues(address, ["postalCode", "zipCode", "zip", "addressPostalCode"]) ??
      findByKeyValues(subjectProperty, ["postalCode", "zipCode"])
  };
}

export class SalesforceLeadHandler implements OutboundSystemService {
  readonly name = "salesforce-lead-sync";

  constructor(private readonly salesforceAuthClient: SalesforceAuth) {}

  private async findExistingLeadByExternalId(externalId: string): Promise<{ id: string; status: string | null } | null> {
    const q = `SELECT Id, Status FROM Lead WHERE External_ID__c = '${escapeSoql(externalId)}' LIMIT 1`;
    const result = await this.salesforceAuthClient.query(q);
    if (result.records.length === 0) return null;
    const id = toStringValue(result.records[0].Id);
    if (!id) return null;
    return {
      id,
      status: toStringValue(result.records[0].Status)
    };
  }

  async handleEvent(event: AriveWebhookEvent, context: ProcessingContext): Promise<void> {
    const externalId = String(event.sysGUID);
    const isLeadCreated = event.triggers.includes("LEAD_CREATED");
    const isLeadUpdated = event.triggers.includes("LEAD_UPDATED");
    const isLeadDeleted = event.triggers.includes("LEAD_DELETED");

    if (!isLeadCreated && !isLeadUpdated && !isLeadDeleted) {
      return;
    }

    if (isLeadDeleted) {
      const existingLead = await this.findExistingLeadByExternalId(externalId);
      if (!existingLead) {
        logger.warn("Received LEAD_DELETED for unknown Salesforce lead.", {
          sysGUID: event.sysGUID,
          externalId
        });
        return;
      }

      const updateResult = await this.salesforceAuthClient.updateRecord("Lead", existingLead.id, {
        Status: "Deferred",
        lead_sub_status__c: "Deferred by system",
        External_System__c: "ARIVE"
      });

      if (!updateResult.success) {
        throw new Error(`Failed to update Lead ${existingLead.id}: ${JSON.stringify(updateResult.errors)}`);
      }

      logger.info("Deferred Lead after LEAD_DELETED event.", { id: existingLead.id, externalId });
      return;
    }

    const lead = asObject(context.leadDetails);
    if (!lead) {
      throw new Error("Lead details payload is not an object.");
    }

    const payload = buildLeadPayload(lead, event);
    const existingLead = await this.findExistingLeadByExternalId(externalId);
    if (isLeadUpdated) {
      payload.Status = existingLead?.status === "Engaged" ? undefined : "Engaged";
    }

    if (!existingLead) {
      const createResult = await this.salesforceAuthClient.createRecord("Lead", payload);
      if (!createResult.success || !createResult.id) {
        throw new Error(`Failed to create Lead: ${JSON.stringify(createResult.errors)}`);
      }
      logger.info("Created Lead from Arive event.", {
        id: createResult.id,
        externalId,
        triggers: event.triggers
      });
      return;
    }

    const updateResult = await this.salesforceAuthClient.updateRecord("Lead", existingLead.id, payload);
    if (!updateResult.success) {
      throw new Error(`Failed to update Lead ${existingLead.id}: ${JSON.stringify(updateResult.errors)}`);
    }

    logger.info("Updated Lead from Arive event.", {
      id: existingLead.id,
      externalId,
      triggers: event.triggers
    });
  }
}
