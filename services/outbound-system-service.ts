import type { AriveWebhookEvent } from "../events/arive-event.js";
import { logger } from "../src/logger.js";

export type ProcessingContext = {
  correlationId: string;
  receivedAtIso: string;
  /** Response body from GET /loans/{sysGUID}. */
  loanDetails: unknown;
  /** Response body from GET /loans/{sysGUID}/selected-mortgage-product. */
  productDetails: unknown | null;
  /** Response body from GET /loans/{sysGUID}/transaction. */
  transactionDetails: unknown | null;
};

export interface OutboundSystemService {
  name: string;
  handleEvent(event: AriveWebhookEvent, context: ProcessingContext): Promise<void>;
}

export class StubOutboundSystemService implements OutboundSystemService {
  constructor(readonly name: string) {}

  async handleEvent(event: AriveWebhookEvent, context: ProcessingContext): Promise<void> {
    logger.info("Outbound service received Arive event.", {
      service: this.name,
      sysGUID: event.sysGUID,
      triggers: event.triggers,
      correlationId: context.correlationId,
      hasLoanDetails: context.loanDetails !== undefined && context.loanDetails !== null,
      hasProductDetails: context.productDetails !== undefined && context.productDetails !== null,
      hasTransactionDetails: context.transactionDetails !== undefined && context.transactionDetails !== null
    });
  }
}
