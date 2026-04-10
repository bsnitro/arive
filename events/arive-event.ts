import { z } from "zod";

/** All webhook trigger values supported by Arive (per Arive docs). */
export const ARIVE_WEBHOOK_TRIGGERS = [
  "LOAN_CREATED",
  "LEAD_CREATED",
  "LEAD_UPDATED",
  "LEAD_DELETED",
  "LOAN_TRACKERS_UPDATED",
  "LOAN_STAGE_CHANGED",
  "LOAN_DATE_CHANGED",
  "LOAN_APP_SUBMITTED"
] as const;

export type AriveWebhookTrigger = (typeof ARIVE_WEBHOOK_TRIGGERS)[number];

export const AriveWebhookTriggerSchema = z.enum(ARIVE_WEBHOOK_TRIGGERS);

/**
 * Payload posted by Arive webhooks.
 * sysGUID identifies the loan; use GET /loans/{sysGUID} for full loan details.
 */
export const AriveWebhookEventSchema = z.object({
  sysGUID: z.coerce.number().int().positive(),
  triggers: z.array(AriveWebhookTriggerSchema).min(1)
});

export type AriveWebhookEvent = z.infer<typeof AriveWebhookEventSchema>;
