import { createHmac } from "node:crypto";

type WebhookTestEvent = {
  sysGUID: number;
  triggers: readonly string[];
};

export async function postWebhookEvent(event: WebhookTestEvent): Promise<void> {
  const webhookSecret = process.env.ARIVE_WEBHOOK_SECRET;
  const baseUrl = process.env.WEBHOOK_TEST_URL ?? "http://localhost:8787/webhook";
  const body = JSON.stringify(event);
  const headers: Record<string, string> = {
    "content-type": "application/json"
  };

  if (webhookSecret) {
    const signature = createHmac("sha256", webhookSecret).update(body).digest("hex");
    headers["x-arive-signature"] = `sha256=${signature}`;
  } else {
    console.log("ARIVE_WEBHOOK_SECRET not set; sending unsigned test event.");
  }

  console.log(`Posting test event to ${baseUrl}`);
  console.log(`Payload: ${body}`);

  const response = await fetch(baseUrl, {
    method: "POST",
    headers,
    body
  });

  const responseBody = await response.text();
  console.log(`Status: ${response.status}`);
  console.log(responseBody);

  if (!response.ok) {
    process.exit(1);
  }
}
