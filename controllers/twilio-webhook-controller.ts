import { logger } from "../src/logger.js";

type TwilioWebhookRequest = {
  method?: string;
  headers: Record<string, string | string[] | undefined>;
  body: unknown;
  rawBody?: string;
};

type TwilioWebhookResponse = {
  statusCode: number;
  body: Record<string, unknown>;
};

export function createTwilioWebhookController() {
  return async function handleTwilioWebhook(request: TwilioWebhookRequest): Promise<TwilioWebhookResponse> {
    if (request.method !== "POST") {
      return {
        statusCode: 405,
        body: { error: "Method Not Allowed" }
      };
    }

    logger.warn("Twilio webhook endpoint hit before implementation.", {
      hasBody: request.body !== undefined && request.body !== null,
      hasRawBody: Boolean(request.rawBody),
      headerKeys: Object.keys(request.headers ?? {})
    });

    return {
      statusCode: 501,
      body: {
        accepted: false,
        error: "Twilio webhook is not implemented yet."
      }
    };
  };
}
