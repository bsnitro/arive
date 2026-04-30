import "dotenv/config";
import { postWebhookEvent } from "./post-webhook-event.js";

const EVENT_PAYLOAD = {
  sysGUID: 16602527,
  triggers: ["LOAN_CREATED"]
} as const;

async function run(): Promise<void> {
  await postWebhookEvent(EVENT_PAYLOAD);
}

run().catch((error) => {
  console.error("Webhook test failed.");
  console.error(error instanceof Error ? error.message : error);
  process.exit(1);
});
