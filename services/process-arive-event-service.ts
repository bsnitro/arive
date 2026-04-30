import type { AriveAuthClient } from "../api-client/arive-auth-client.js";
import type { AriveLoansClient } from "../api-client/arive-loans-client.js";
import { hasLeadTrigger, hasLoanTrigger } from "../events/arive-event.js";
import type SalesforceAuth from "../api-client/salesforceAuth.js";
import type { AriveWebhookEvent } from "../events/arive-event.js";
import { resolveDestinationServices } from "../routes/arive-routes.js";
import { logger } from "../src/logger.js";
import type { OutboundSystemService } from "./outbound-system-service.js";

type ProcessAriveEventServiceDeps = {
  ariveAuthClient: AriveAuthClient;
  ariveLoansClient: AriveLoansClient;
  salesforceAuthClient: SalesforceAuth;
  outboundServices: OutboundSystemService[];
};

export function createProcessAriveEventService(deps: ProcessAriveEventServiceDeps) {
  return async function processAriveEvent(event: AriveWebhookEvent): Promise<void> {
    const correlationId = `${event.sysGUID}:${Date.now()}`;
    const receivedAtIso = new Date().toISOString();

    const ariveToken = await deps.ariveAuthClient.getAccessToken();
    logger.info("Arive token acquired for processing.", {
      sysGUID: event.sysGUID,
      triggers: event.triggers,
      hasExpiry: Boolean(ariveToken.expiresAtEpochMs)
    });

    const includesLoanTrigger = hasLoanTrigger(event.triggers);
    const includesLeadTrigger = hasLeadTrigger(event.triggers);

    const loanDetails = includesLoanTrigger ? await deps.ariveLoansClient.getLoanById(event.sysGUID) : null;
    const productDetails = includesLoanTrigger
      ? await deps.ariveLoansClient.getSelectedMortgageProduct(event.sysGUID)
      : null;
    const transactionDetails = includesLoanTrigger ? await deps.ariveLoansClient.getTransaction(event.sysGUID) : null;
    const leadDetails =
      includesLeadTrigger && !event.triggers.includes("LEAD_DELETED")
        ? await deps.ariveLoansClient.getLeadById(event.sysGUID)
        : null;

    logger.info("Arive event details loaded.", {
      sysGUID: event.sysGUID,
      triggers: event.triggers,
      hasLoanDetails: Boolean(loanDetails),
      hasProductDetails: Boolean(productDetails),
      hasTransactionDetails: Boolean(transactionDetails),
      hasLeadDetails: Boolean(leadDetails)
    });

    const sfToken = await deps.salesforceAuthClient.getAccessToken();
    logger.info("Salesforce token acquired for processing.", {
      sysGUID: event.sysGUID,
      triggers: event.triggers,
      instanceUrl: sfToken.instanceUrl
    });

    const destinations = resolveDestinationServices(event, deps.outboundServices);
    await Promise.all(
      destinations.map((service) =>
        service.handleEvent(event, {
          correlationId,
          receivedAtIso,
          loanDetails,
          productDetails,
          transactionDetails,
          leadDetails
        })
      )
    );

    logger.info("Arive event processed.", {
      sysGUID: event.sysGUID,
      triggers: event.triggers,
      routedServices: destinations.map((service) => service.name)
    });
  };
}
