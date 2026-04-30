import "dotenv/config";
import { createServer } from "node:http";

import { createTwilioWebhookController } from "../controllers/twilio-webhook-controller.js";
import { bootstrapApp } from "../services/app-bootstrap.js";

const app = bootstrapApp();
const twilioWebhookController = createTwilioWebhookController();
const port = Number(process.env.PORT ?? "8787");

const server = createServer(async (req, res) => {
  if (!req.url) {
    res.statusCode = 400;
    res.end("Bad request");
    return;
  }

  if (req.url === "/health") {
    res.statusCode = 200;
    res.setHeader("content-type", "application/json");
    res.end(
      JSON.stringify({
        ok: true,
        service: "arive-salesforce-integration",
        timestamp: new Date().toISOString()
      })
    );
    return;
  }

  const path = req.url.split("?")[0] ?? req.url;
  const isWebhookPost =
    req.method === "POST" &&
    (path === "/webhook" ||
      path === "/api/webhook" ||
      path === "/webhooks/arive" ||
      path === "/api/webhooks/arive");
  const isTwilioWebhookPost =
    req.method === "POST" &&
    (path === "/webhooks/twilio" || path === "/api/webhooks/twilio");

  if (isWebhookPost) {
    const chunks: Uint8Array[] = [];
    for await (const chunk of req) {
      chunks.push(typeof chunk === "string" ? Buffer.from(chunk) : chunk);
    }

    const rawBody = Buffer.concat(chunks).toString("utf8");
    const body = rawBody.length > 0 ? JSON.parse(rawBody) : {};

    const response = await app.webhookController({
      method: req.method,
      headers: req.headers,
      body,
      rawBody
    });

    res.statusCode = response.statusCode;
    res.setHeader("content-type", "application/json");
    res.end(JSON.stringify(response.body));
    return;
  }

  if (isTwilioWebhookPost) {
    const chunks: Uint8Array[] = [];
    for await (const chunk of req) {
      chunks.push(typeof chunk === "string" ? Buffer.from(chunk) : chunk);
    }

    const rawBody = Buffer.concat(chunks).toString("utf8");
    const body = rawBody.length > 0 ? JSON.parse(rawBody) : {};
    const response = await twilioWebhookController({
      method: req.method,
      headers: req.headers,
      body,
      rawBody
    });

    res.statusCode = response.statusCode;
    res.setHeader("content-type", "application/json");
    res.end(JSON.stringify(response.body));
    return;
  }

  res.statusCode = 404;
  res.end("Not found");
});

server.listen(port, () => {
  console.log(`Webhook dev server running on http://localhost:${port}`);
});
