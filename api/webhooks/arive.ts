import type { VercelRequest, VercelResponse } from "@vercel/node";

import { bootstrapApp } from "../../services/app-bootstrap.js";

const app = bootstrapApp();

export default async function ariveWebhook(req: VercelRequest, res: VercelResponse) {
  const result = await app.webhookController({
    method: req.method,
    headers: req.headers,
    body: req.body,
    rawBody: typeof req.body === "string" ? req.body : JSON.stringify(req.body ?? {})
  });

  res.status(result.statusCode).json(result.body);
}
