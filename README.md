# Arive + Salesforce Integration

TypeScript-based webhook integration service that receives Arive events in Vercel functions, authenticates with Arive and Salesforce, and routes events to downstream systems.

## Project structure

```text
api/
  health.ts
  webhook.ts
  webhooks/arive.ts
api-client/
  arive-auth-client.ts
  arive-loans-client.ts
  salesforceAuth.ts
controllers/
  webhook-controller.ts
events/
  arive-event.ts
routes/
  arive-routes.ts
services/
  app-bootstrap.ts
  outbound-system-service.ts
  process-arive-event-service.ts
  salesforce-loan-handler.ts
scripts/
  dev-webhook-server.ts
src/
  env.ts
  errors.ts
  logger.ts
  webhook-signature.ts
```

## Quick start

1. Install dependencies:
   - `npm install`
2. Ensure your `.env` includes required variables:
   - `ARIVE_API_KEY`
   - `ARIVE_CLIENT_ID`
   - `ARIVE_SECRET_ID`
   - `ARIVE_BASE_URL` (e.g. `https://gwapiconnect.myarive.com/api`)
   - `ARIVE_WEBHOOK_SECRET` (optional; only needed if signature verification is enabled)
3. Run checks:
   - `npm run typecheck`
   - `npm run lint`
4. (Optional) run local webhook stub:
   - `npm run dev:webhook`
5. Test Arive auth + leads API connection:
   - `npm run test:arive-connection`
   - optional args: `npm run test:arive-connection -- 25 0` (`limit`, `offset`)
6. Send local webhook test event (`sysGUID: 16541669`, `LOAN_STAGE_CHANGED`):
   - `npm run test:webhook:event`

## Notes

- Webhook URLs:
  - **Arive (recommended):** `POST https://<your-deployment>/api/webhooks/arive`
  - **Arive (alias):** `POST https://<your-deployment>/api/webhook` (same handler as `/api/webhooks/arive`)
  - **Twilio (v2 scaffold):** `POST https://<your-deployment>/api/webhooks/twilio` (currently returns `501 Not Implemented`)
  - **Local (`npm run dev:webhook`):** `POST http://localhost:8787/webhook` or `/api/webhook`.
- `routes/arive-routes.ts` controls where each event type should be sent.
- `services/process-arive-event-service.ts` handles processing logic for incoming events.
- `api-client/*` contains Arive and Salesforce API authentication clients.
- Current sample payload mapping coverage is tracked in `docs/arive-rla-mapping-sample.md`.
