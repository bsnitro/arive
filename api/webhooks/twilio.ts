import type { VercelRequest, VercelResponse } from "@vercel/node";

import { createTwilioWebhookController } from "../../controllers/twilio-webhook-controller.js";

const twilioWebhookController = createTwilioWebhookController();

/**
 * POST /api/webhooks/twilio — reserved for future Twilio integration.
 */
export default async function twilioWebhook(req: VercelRequest, res: VercelResponse) {
  const result = await twilioWebhookController({
    method: req.method,
    headers: req.headers,
    body: req.body,
    rawBody: typeof req.body === "string" ? req.body : JSON.stringify(req.body ?? {})
  });

  res.status(result.statusCode).json(result.body);
}
