import { z } from "zod";

/** All webhook trigger values supported by Arive (per Arive docs). */
export const ARIVE_WEBHOOK_TRIGGERS = [
  "LOAN_CREATED",
  "LEAD_CREATED",
  "LEAD_UPDATED",
  "LEAD_DELETED",
  "LOAN_ARCHIVED",
  "LOAN_STATUS_CHANGED",
  "LOAN_TRACKERS_CHANGED",
  "LOAN_TRACKERS_UPDATED",
  "LOAN_STAGE_CHANGED",
  "LOAN_DATE_CHANGED",
  "LOAN_APP_SUBMITTED"
] as const;

export type AriveWebhookTrigger = (typeof ARIVE_WEBHOOK_TRIGGERS)[number];
export const ARIVE_LEAD_WEBHOOK_TRIGGERS = ["LEAD_CREATED", "LEAD_UPDATED", "LEAD_DELETED"] as const;
export const ARIVE_LOAN_WEBHOOK_TRIGGERS = [
  "LOAN_CREATED",
  "LOAN_ARCHIVED",
  "LOAN_STATUS_CHANGED",
  "LOAN_TRACKERS_CHANGED",
  "LOAN_TRACKERS_UPDATED",
  "LOAN_STAGE_CHANGED",
  "LOAN_DATE_CHANGED",
  "LOAN_APP_SUBMITTED"
] as const;

export const AriveWebhookTriggerSchema = z.enum(ARIVE_WEBHOOK_TRIGGERS);

/**
 * Payload posted by Arive webhooks.
 * sysGUID identifies the lead/loan record associated to the trigger.
 */
export const AriveWebhookEventSchema = z.object({
  sysGUID: z.coerce.number().int().positive(),
  triggers: z.array(AriveWebhookTriggerSchema).min(1)
});

export type AriveWebhookEvent = z.infer<typeof AriveWebhookEventSchema>;

const LEAD_TRIGGER_SET = new Set<AriveWebhookTrigger>(ARIVE_LEAD_WEBHOOK_TRIGGERS);
const LOAN_TRIGGER_SET = new Set<AriveWebhookTrigger>(ARIVE_LOAN_WEBHOOK_TRIGGERS);

export function hasLeadTrigger(triggers: AriveWebhookTrigger[]): boolean {
  return triggers.some((trigger) => LEAD_TRIGGER_SET.has(trigger));
}

export function hasLoanTrigger(triggers: AriveWebhookTrigger[]): boolean {
  return triggers.some((trigger) => LOAN_TRIGGER_SET.has(trigger));
}
