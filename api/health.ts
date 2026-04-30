import type { VercelRequest, VercelResponse } from "@vercel/node";

export default function health(_req: VercelRequest, res: VercelResponse): void {
  res.status(200).json({
    ok: true,
    service: "arive-salesforce-integration",
    timestamp: new Date().toISOString()
  });
}
