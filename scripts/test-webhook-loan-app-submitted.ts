import "dotenv/config";
import { postWebhookEvent } from "./post-webhook-event.js";

function resolveSysGuid(): number {
  const cliArg = process.argv[2];
  if (cliArg) {
    const parsed = Number(cliArg);
    if (Number.isFinite(parsed) && parsed > 0) return parsed;
    throw new Error(`Invalid sysGUID argument: ${cliArg}`);
  }

  const fromEnv = Number(process.env.WEBHOOK_TEST_SYS_GUID ?? "16602527");
  if (!Number.isFinite(fromEnv) || fromEnv <= 0) {
    throw new Error(`Invalid WEBHOOK_TEST_SYS_GUID value: ${process.env.WEBHOOK_TEST_SYS_GUID}`);
  }
  return fromEnv;
}

const EVENT_PAYLOAD = {
  sysGUID: resolveSysGuid(),
  triggers: ["LOAN_APP_SUBMITTED"]
} as const;

async function run(): Promise<void> {
  await postWebhookEvent(EVENT_PAYLOAD);
}

run().catch((error) => {
  console.error("LOAN_APP_SUBMITTED webhook test failed.");
  console.error(error instanceof Error ? error.message : error);
  process.exit(1);
});
