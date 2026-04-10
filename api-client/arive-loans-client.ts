import { logger } from "../src/logger.js";

type TokenGetter = () => Promise<{ accessToken: string }>;

function normalizeBaseUrl(baseUrl: string): string {
  return baseUrl.replace(/\/+$/, "");
}

/**
 * Arive REST: GET {baseUrl}/loans/{id} with Bearer JWT + X-API-KEY.
 * baseUrl is typically https://gwapiconnect.myarive.com/api (no trailing slash).
 */
export class AriveLoansClient {
  constructor(
    private readonly baseUrl: string,
    private readonly apiKey: string,
    private readonly getAccessToken: TokenGetter
  ) {}

  private async requestJson(path: string, required = true): Promise<unknown | null> {
    const root = normalizeBaseUrl(this.baseUrl);
    const token = await this.getAccessToken();
    const url = `${root}${path}`;
    logger.info("Calling Arive API.", { path, url });

    const response = await fetch(url, {
      method: "GET",
      headers: {
        authorization: `Bearer ${token.accessToken}`,
        "x-api-key": this.apiKey,
        accept: "application/json"
      }
    });

    if (!response.ok) {
      const body = await response.text();
      logger.error("Arive API request failed.", { path, status: response.status });
      if (!required) {
        return null;
      }
      throw new Error(`Arive GET ${path} failed: status=${response.status} body=${body}`);
    }

    logger.info("Arive API request succeeded.", { path, status: response.status });
    return response.json() as Promise<unknown>;
  }

  async getLoanById(loanId: number): Promise<unknown> {
    const payload = await this.requestJson(`/loans/${loanId}`, true);
    return payload as unknown;
  }

  async getSelectedMortgageProduct(loanId: number): Promise<unknown | null> {
    return this.requestJson(`/loans/${loanId}/selected-mortgage-product`, false);
  }

  async getTransaction(loanId: number): Promise<unknown | null> {
    return this.requestJson(`/loans/${loanId}/transaction`, false);
  }
}
