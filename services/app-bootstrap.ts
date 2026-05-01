import { AriveAuthClient } from "../api-client/arive-auth-client.js";
import { AriveLoansClient } from "../api-client/arive-loans-client.js";
import SalesforceAuth from "../api-client/salesforceAuth.js";
import { createWebhookController } from "../controllers/webhook-controller.js";
import { readEnv } from "../src/env.js";
import { AriveLoanAppSubmittedPatchService } from "./arive-loan-app-submitted-patch-service.js";
import { createProcessAriveEventService } from "./process-arive-event-service.js";
import { SalesforceLeadHandler } from "./salesforce-lead-handler.js";
import { SalesforceLoanHandler } from "./salesforce-loan-handler.js";
import { StubOutboundSystemService } from "./outbound-system-service.js";

export function bootstrapApp() {
  const env = readEnv();

  const ariveAuthClient = new AriveAuthClient({
    authUrl: env.ARIVE_AUTH_URL,
    clientId: env.ARIVE_CLIENT_ID,
    secret: env.ARIVE_SECRET_ID,
    apiKey: env.ARIVE_API_KEY
  });

  const ariveLoansClient = new AriveLoansClient(
    env.ARIVE_BASE_URL,
    env.ARIVE_API_KEY,
    () => ariveAuthClient.getAccessToken()
  );

  const salesforceAuthClient = new SalesforceAuth();
  const salesforceLoanSyncService = new SalesforceLoanHandler(salesforceAuthClient);
  const salesforceLeadSyncService = new SalesforceLeadHandler(salesforceAuthClient);
  const ariveLoanAppSubmittedPatchService = new AriveLoanAppSubmittedPatchService(ariveLoansClient);
  const outboundServices = [
    salesforceLoanSyncService,
    salesforceLeadSyncService,
    ariveLoanAppSubmittedPatchService,
    new StubOutboundSystemService("future-third-party-system")
  ];

  const processAriveEvent = createProcessAriveEventService({
    ariveAuthClient,
    ariveLoansClient,
    salesforceAuthClient,
    outboundServices
  });

  return {
    env,
    webhookController: createWebhookController({
      env,
      processAriveEvent
    })
  };
}
