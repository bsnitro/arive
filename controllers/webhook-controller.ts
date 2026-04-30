import { ZodError } from "zod";

import {
  AriveWebhookEventSchema,
  type AriveWebhookEvent
} from "../events/arive-event.js";
import type { AppEnv } from "../src/env.js";
import { AppError } from "../src/errors.js";
import { logger } from "../src/logger.js";
import { verifyWebhookSignature } from "../src/webhook-signature.js";

type WebhookControllerInput = {
  env: AppEnv;
  processAriveEvent: (event: AriveWebhookEvent) => Promise<void>;
};

type WebhookRequest = {
  method?: string;
  headers: Record<string, string | string[] | undefined>;
  body: unknown;
  rawBody?: string;
};

type WebhookResponse = {
  statusCode: number;
  body: Record<string, unknown>;
};

function resolveHeader(
  headers: Record<string, string | string[] | undefined>,
  name: string
): string | undefined {
  const value = headers[name] ?? headers[name.toLowerCase()] ?? headers[name.toUpperCase()];
  return Array.isArray(value) ? value[0] : value;
}

export function createWebhookController(input: WebhookControllerInput) {
  return async function handleWebhook(request: WebhookRequest): Promise<WebhookResponse> {
    try {
      if (request.method !== "POST") {
        return {
          statusCode: 405,
          body: { error: "Method Not Allowed" }
        };
      }

      const rawBody = request.rawBody ?? JSON.stringify(request.body ?? {});
      const signature = resolveHeader(request.headers, "x-arive-signature");

      if (input.env.ARIVE_WEBHOOK_SECRET) {
        verifyWebhookSignature({
          rawBody,
          signature,
          secret: input.env.ARIVE_WEBHOOK_SECRET
        });
      } else {
        logger.warn("ARIVE_WEBHOOK_SECRET is not set; skipping webhook signature verification.");
      }

      const parsedEvent = AriveWebhookEventSchema.parse(request.body);
      await input.processAriveEvent(parsedEvent);

      return {
        statusCode: 202,
        body: {
          accepted: true,
          sysGUID: parsedEvent.sysGUID,
          triggers: parsedEvent.triggers
        }
      };
    } catch (error) {
      if (error instanceof ZodError) {
        return {
          statusCode: 400,
          body: {
            error: "Invalid Arive payload.",
            details: error.flatten()
          }
        };
      }

      if (error instanceof AppError) {
        return {
          statusCode: error.statusCode,
          body: { error: error.message, code: error.code }
        };
      }

      logger.error("Unhandled webhook error.", {
        message: error instanceof Error ? error.message : "Unknown error"
      });

      return {
        statusCode: 500,
        body: { error: "Internal Server Error" }
      };
    }
  };
}
