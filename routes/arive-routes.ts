import type { AriveWebhookEvent } from "../events/arive-event.js";
import { hasLeadTrigger, hasLoanTrigger } from "../events/arive-event.js";
import type { OutboundSystemService } from "../services/outbound-system-service.js";

export function resolveDestinationServices(
  event: AriveWebhookEvent,
  allServices: OutboundSystemService[]
): OutboundSystemService[] {
  const includeLead = hasLeadTrigger(event.triggers);
  const includeLoan = hasLoanTrigger(event.triggers);

  return allServices.filter((service) => {
    if (service.name === "salesforce-lead-sync") return includeLead;
    if (service.name === "salesforce-loan-sync") return includeLoan;
    return true;
  });
}
