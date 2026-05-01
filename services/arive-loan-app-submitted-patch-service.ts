import type { AriveLoansClient } from "../api-client/arive-loans-client.js";
import type { AriveWebhookEvent } from "../events/arive-event.js";
import { logger } from "../src/logger.js";
import type { OutboundSystemService, ProcessingContext } from "./outbound-system-service.js";

const ARIVE_QUALIFICATION_STAGE = "QUALIFICATION";

export class AriveLoanAppSubmittedPatchService implements OutboundSystemService {
  readonly name = "arive-loan-app-submitted-patch";

  constructor(private readonly ariveLoansClient: AriveLoansClient) {}

  async handleEvent(event: AriveWebhookEvent, context: ProcessingContext): Promise<void> {
    void context;
    if (!event.triggers.includes("LOAN_APP_SUBMITTED")) {
      return;
    }

    await this.ariveLoansClient.patchLoanStage(event.sysGUID, ARIVE_QUALIFICATION_STAGE);
    logger.info("Applied Arive stage writeback for LOAN_APP_SUBMITTED.", {
      sysGUID: event.sysGUID,
      stage: ARIVE_QUALIFICATION_STAGE
    });
  }
}
