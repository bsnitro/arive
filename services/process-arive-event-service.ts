import type { AriveAuthClient } from "../api-client/arive-auth-client.js";
import type { AriveLoansClient } from "../api-client/arive-loans-client.js";
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

    const loanDetails = await deps.ariveLoansClient.getLoanById(event.sysGUID);
    const productDetails = await deps.ariveLoansClient.getSelectedMortgageProduct(event.sysGUID);
    const transactionDetails = await deps.ariveLoansClient.getTransaction(event.sysGUID);
    logger.info("Arive loan details loaded.", {
      sysGUID: event.sysGUID,
      triggers: event.triggers,
      hasProductDetails: Boolean(productDetails),
      hasTransactionDetails: Boolean(transactionDetails)
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
          transactionDetails
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
