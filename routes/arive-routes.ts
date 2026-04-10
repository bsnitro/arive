import type { AriveWebhookEvent } from "../events/arive-event.js";
import type { OutboundSystemService } from "../services/outbound-system-service.js";

export function resolveDestinationServices(
  _event: AriveWebhookEvent,
  allServices: OutboundSystemService[]
): OutboundSystemService[] {
  // Route logic goes here as requirements are added.
  return allServices;
}
