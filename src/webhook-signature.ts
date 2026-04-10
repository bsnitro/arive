import { createHmac, timingSafeEqual } from "node:crypto";

import { UnauthorizedError } from "./errors.js";

export function verifyWebhookSignature(input: {
  rawBody: string;
  signature: string | undefined;
  secret: string;
}): void {
  const { rawBody, signature, secret } = input;
  if (!signature) {
    throw new UnauthorizedError("Missing webhook signature header.");
  }

  const expected = createHmac("sha256", secret).update(rawBody).digest("hex");
  const actual = signature.replace(/^sha256=/, "");

  const expectedBuffer = Buffer.from(expected, "utf8");
  const actualBuffer = Buffer.from(actual, "utf8");

  if (
    expectedBuffer.length !== actualBuffer.length ||
    !timingSafeEqual(expectedBuffer, actualBuffer)
  ) {
    throw new UnauthorizedError("Invalid webhook signature.");
  }
}
