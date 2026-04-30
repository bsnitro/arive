import "dotenv/config";
import { postWebhookEvent } from "./post-webhook-event.js";

const EVENT_PAYLOAD = {
  sysGUID: Number(process.env.WEBHOOK_TEST_SYS_GUID ?? "16602527"),
  triggers: ["LEAD_UPDATED"]
} as const;

async function run(): Promise<void> {
  await postWebhookEvent(EVENT_PAYLOAD);
}

run().catch((error) => {
  console.error("LEAD_UPDATED webhook test failed.");
  console.error(error instanceof Error ? error.message : error);
  process.exit(1);
});
