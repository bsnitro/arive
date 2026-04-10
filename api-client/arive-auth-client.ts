import { logger } from "../src/logger.js";

export type AriveAccessToken = {
  accessToken: string;
  expiresAtEpochMs?: number;
};

type AriveAuthClientConfig = {
  authUrl: string;
  clientId: string;
  secret: string;
  apiKey: string;
};

function extractToken(payload: unknown): string | undefined {
  if (!payload || typeof payload !== "object") {
    return undefined;
  }

  const objectPayload = payload as Record<string, unknown>;
  const directCandidates = [
    objectPayload.accessToken,
    objectPayload.access_token,
    objectPayload.AccessToken,
    objectPayload.token,
    objectPayload.jwtToken,
    objectPayload.jwt,
    objectPayload.bearerToken
  ];

  for (const candidate of directCandidates) {
    if (typeof candidate === "string" && candidate.length > 0) {
      return candidate;
    }
  }

  const nestedCandidates = [objectPayload.data, objectPayload.result, objectPayload.auth];
  for (const nested of nestedCandidates) {
    const nestedToken = extractToken(nested);
    if (nestedToken) {
      return nestedToken;
    }
  }

  return undefined;
}

function extractExpiresInSeconds(payload: unknown): number | undefined {
  if (!payload || typeof payload !== "object") {
    return undefined;
  }

  const objectPayload = payload as Record<string, unknown>;
  const expiresInCandidates = [
    objectPayload.expiresInSeconds,
    objectPayload.expires_in,
    objectPayload.expiresIn,
    objectPayload.ExpiresIn
  ];
  for (const candidate of expiresInCandidates) {
    if (typeof candidate === "number" && Number.isFinite(candidate)) {
      return candidate;
    }
  }

  const nestedCandidates = [objectPayload.data, objectPayload.result, objectPayload.auth];
  for (const nested of nestedCandidates) {
    const nestedExpires = extractExpiresInSeconds(nested);
    if (typeof nestedExpires === "number") {
      return nestedExpires;
    }
  }

  return undefined;
}

export class AriveAuthClient {
  private cachedToken: AriveAccessToken | null = null;

  constructor(private readonly config: AriveAuthClientConfig) {}

  async getAccessToken(): Promise<AriveAccessToken> {
    const now = Date.now();
    if (
      this.cachedToken?.expiresAtEpochMs &&
      now < this.cachedToken.expiresAtEpochMs - 60_000
    ) {
      return this.cachedToken;
    }

    const response = await fetch(this.config.authUrl, {
      method: "POST",
      headers: {
        "content-type": "application/json",
        "x-api-key": this.config.apiKey
      },
      body: JSON.stringify({
        clientId: this.config.clientId,
        secret: this.config.secret,
        apiKey: this.config.apiKey
      })
    });

    if (!response.ok) {
      const body = await response.text();
      throw new Error(`Failed to authenticate with Arive. status=${response.status} body=${body}`);
    }

    const payload = (await response.json()) as unknown;
    const accessToken = extractToken(payload);
    if (!accessToken) {
      const payloadKeys =
        payload && typeof payload === "object" ? Object.keys(payload as Record<string, unknown>) : [];
      throw new Error(
        `Arive auth response did not include a recognizable token field. topLevelKeys=${payloadKeys.join(",")}`
      );
    }

    const expiresInSeconds = extractExpiresInSeconds(payload);
    const resolvedToken: AriveAccessToken = {
      accessToken,
      expiresAtEpochMs: expiresInSeconds ? now + expiresInSeconds * 1000 : undefined
    };

    this.cachedToken = resolvedToken;
    logger.info("Arive token refreshed.");

    return resolvedToken;
  }
}
