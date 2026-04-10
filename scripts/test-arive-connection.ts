import "dotenv/config";

import { z } from "zod";

import { AriveAuthClient } from "../api-client/arive-auth-client.js";

const LocalAriveEnvSchema = z.object({
  ARIVE_API_KEY: z.string().min(1),
  ARIVE_CLIENT_ID: z.string().min(1),
  ARIVE_SECRET_ID: z.string().min(1),
  ARIVE_AUTH_URL: z
    .string()
    .url()
    .default("https://gwapiconnect.myarive.com/api/auth/access-token"),
  ARIVE_LEADS_URL: z.string().url().default("https://gwapiconnect.myarive.com/api/leads")
});

type LeadQuery = {
  limit: number;
  offset: number;
};

function parseQueryFromArgs(): LeadQuery {
  const limitArg = Number(process.argv[2] ?? "10");
  const offsetArg = Number(process.argv[3] ?? "0");

  const limit = Number.isFinite(limitArg) ? Math.min(Math.max(limitArg, 1), 100) : 10;
  const offset = Number.isFinite(offsetArg) ? Math.max(offsetArg, 0) : 0;
  return { limit, offset };
}

function resolveLeadCount(payload: unknown): number | undefined {
  if (Array.isArray(payload)) {
    return payload.length;
  }
  if (!payload || typeof payload !== "object") {
    return undefined;
  }

  const objectPayload = payload as Record<string, unknown>;
  if (Array.isArray(objectPayload.items)) {
    return objectPayload.items.length;
  }
  if (Array.isArray(objectPayload.data)) {
    return objectPayload.data.length;
  }
  return undefined;
}

async function run(): Promise<void> {
  const env = LocalAriveEnvSchema.parse(process.env);
  const query = parseQueryFromArgs();

  const authClient = new AriveAuthClient({
    authUrl: env.ARIVE_AUTH_URL,
    clientId: env.ARIVE_CLIENT_ID,
    secret: env.ARIVE_SECRET_ID,
    apiKey: env.ARIVE_API_KEY
  });

  console.log("Requesting Arive access token...");
  const token = await authClient.getAccessToken();

  const leadsUrl = new URL(env.ARIVE_LEADS_URL);
  leadsUrl.searchParams.set("limit", String(query.limit));
  leadsUrl.searchParams.set("offset", String(query.offset));

  console.log(`Requesting leads from ${leadsUrl.origin}${leadsUrl.pathname}...`);
  const response = await fetch(leadsUrl, {
    method: "GET",
    headers: {
      authorization: `Bearer ${token.accessToken}`,
      "x-api-key": env.ARIVE_API_KEY
    }
  });

  const rawBody = await response.text();
  if (!response.ok) {
    throw new Error(`Leads request failed. status=${response.status} body=${rawBody}`);
  }

  let payload: unknown = null;
  try {
    payload = rawBody.length > 0 ? JSON.parse(rawBody) : null;
  } catch {
    payload = rawBody;
  }

  const count = resolveLeadCount(payload);
  console.log("Arive connection test passed.");
  console.log(`HTTP ${response.status}`);
  if (typeof count === "number") {
    console.log(`Received ${count} lead records in this response.`);
  }
}

run().catch((error) => {
  const message = error instanceof Error ? error.message : "Unknown error";
  console.error("Arive connection test failed.");
  console.error(message);
  process.exit(1);
});
