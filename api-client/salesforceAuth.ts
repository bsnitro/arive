/**
 * Salesforce Authentication using JWT Bearer Token
 * Production-ready authentication for webhook automation
 */

import * as fs from 'fs';
import * as crypto from 'crypto';
import axios, { AxiosInstance } from 'axios';

interface SalesforceJWTResponse {
  access_token: string;
  scope: string;
  instance_url: string;
  id: string;
  token_type: string;
}

interface SalesforceQueryResponse {
  totalSize: number;
  done: boolean;
  records: any[];
}

interface SalesforceCreateResponse {
  id?: string;
  success: boolean;
  errors: any[];
}

export type SalesforceAccessToken = {
  accessToken: string;
  instanceUrl: string;
};

class SalesforceAuth {
  private client: AxiosInstance;
  private accessToken: string | null = null;
  private instanceUrl: string | null = null;
  private isAuthenticated: boolean = false;
  private privateKey: string;
  private consumerKey: string;
  private username: string;
  private audience: string;

  constructor() {
    // Load configuration
    this.consumerKey = process.env.SALESFORCE_CONSUMER_KEY || '';
    this.username = process.env.SALESFORCE_USERNAME || 'apistudio@onerealmortgage.com';
    this.audience = process.env.SALESFORCE_JWT_AUDIENCE || 'https://onerealmortgage.my.salesforce.com';
    
    console.log('🔧 [SalesforceAuth] Initializing JWT Authentication...');
    console.log('   - Consumer Key:', this.consumerKey ? `${this.consumerKey.substring(0, 15)}...` : 'NOT SET');
    console.log('   - Username:', this.username);
    console.log('   - Audience:', this.audience);

    // Load private key
    this.privateKey = this.loadPrivateKey();
    
    this.client = axios.create({
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });
  }

  private normalizePrivateKey(key: string): string {
    let normalized = key.trim();

    // Strip wrapping quotes often introduced in hosted env variable UIs.
    if (
      (normalized.startsWith('"') && normalized.endsWith('"')) ||
      (normalized.startsWith("'") && normalized.endsWith("'"))
    ) {
      normalized = normalized.slice(1, -1);
    }

    // Replace escaped CR/LF sequences (common when storing multiline keys in env vars).
    normalized = normalized.replace(/\\r\\n/g, "\n").replace(/\\n/g, "\n").replace(/\r\n/g, "\n");

    // If a PEM header exists, return as-is after newline normalization.
    if (normalized.includes("BEGIN") && normalized.includes("PRIVATE KEY")) {
      return normalized;
    }

    // Some deployments store the PEM as base64 to avoid newline formatting issues.
    // Try decoding and return decoded PEM when recognizable.
    try {
      const decoded = Buffer.from(normalized, "base64").toString("utf8").trim();
      if (decoded.includes("BEGIN") && decoded.includes("PRIVATE KEY")) {
        return decoded.replace(/\r\n/g, "\n");
      }
    } catch {
      // Ignore decode errors and fall through to raw value.
    }

    return normalized;
  }

  private loadPrivateKey(): string {
    // First try environment variable (for production deployment)
    const keyFromEnv = process.env.SALESFORCE_PRIVATE_KEY;
    if (keyFromEnv) {
      return this.normalizePrivateKey(keyFromEnv);
    }
    
    // Fallback to file (for local development)
    const keyPath = process.env.SALESFORCE_PRIVATE_KEY_PATH || 'apistudio.key';
    
    try {
      if (!fs.existsSync(keyPath)) {
        throw new Error(`Private key not found in environment variable SALESFORCE_PRIVATE_KEY or file: ${keyPath}`);
      }
      
      const keyContent = fs.readFileSync(keyPath, 'utf8');
      return this.normalizePrivateKey(keyContent);
    } catch (error) {
      console.error('❌ [SalesforceAuth] Failed to load private key:', error);
      throw error;
    }
  }

  private createJWT(): string {
    const now = Math.floor(Date.now() / 1000);
    const exp = now + (5 * 60); // 5 minutes from now
    
    const header = {
      alg: 'RS256',
      typ: 'JWT'
    };
    
    const payload = {
      iss: this.consumerKey,
      sub: this.username,
      aud: this.audience,
      exp: exp,
      iat: now
    };
    
    // Encode header and payload
    const encodedHeader = Buffer.from(JSON.stringify(header)).toString('base64url');
    const encodedPayload = Buffer.from(JSON.stringify(payload)).toString('base64url');
    
    // Create signature
    const signingInput = `${encodedHeader}.${encodedPayload}`;
    
    try {
      if (this.privateKey.includes("BEGIN ENCRYPTED PRIVATE KEY")) {
        throw new Error(
          "Encrypted private keys are not supported without a passphrase. Provide an unencrypted RSA private key in SALESFORCE_PRIVATE_KEY."
        );
      }
      const keyObject = crypto.createPrivateKey(this.privateKey);
      const signature = crypto
        .createSign('RSA-SHA256')
        .update(signingInput)
        .sign(keyObject, 'base64url');
      
      return `${signingInput}.${signature}`;
    } catch (error) {
      console.error('❌ [SalesforceAuth] JWT signing failed:', error);
      console.error("❌ [SalesforceAuth] Ensure SALESFORCE_PRIVATE_KEY is valid PEM (or base64-encoded PEM) and includes BEGIN/END PRIVATE KEY markers.");
      throw error;
    }
  }

  async authenticate(): Promise<void> {
    if (!this.consumerKey) {
      throw new Error('SALESFORCE_CONSUMER_KEY is required for JWT authentication');
    }

    console.log('🚀 [SalesforceAuth] Starting JWT authentication...');
    
    const jwt = this.createJWT();
    const tokenUrl = `${this.audience}/services/oauth2/token`;
    
    const params = new URLSearchParams({
      grant_type: 'urn:ietf:params:oauth:grant-type:jwt-bearer',
      assertion: jwt
    });
    
    try {
      console.log('📤 [SalesforceAuth] Sending authentication request...');
      
      const response = await axios.post<SalesforceJWTResponse>(tokenUrl, params, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'Accept': 'application/json'
        },
        timeout: 30000
      });
      
      console.log('✅ [SalesforceAuth] Authentication successful!');
      
      this.accessToken = response.data.access_token;
      this.instanceUrl = response.data.instance_url;
      this.isAuthenticated = true;
      
      // Update client headers
      this.client.defaults.headers.common['Authorization'] = `Bearer ${this.accessToken}`;
      
      console.log('   - Instance URL:', this.instanceUrl);
      
    } catch (error: any) {
      console.error('❌ [SalesforceAuth] Authentication failed!');
      console.error('   - Error:', error.response?.data || error.message);
      
      throw new Error(`JWT authentication failed: ${error.response?.data?.error_description || error.message}`);
    }
  }

  async query(soql: string): Promise<SalesforceQueryResponse> {
    if (!this.isAuthenticated) {
      await this.authenticate();
    }

    try {
      const url = `${this.instanceUrl}/services/data/v58.0/query/?q=${encodeURIComponent(soql)}`;
      const response = await this.client.get(url);
      return response.data;
    } catch (error: any) {
      if (error.response?.status === 401) {
        console.log('🔄 [SalesforceAuth] Token expired, re-authenticating...');
        this.isAuthenticated = false;
        await this.authenticate();
        return this.query(soql);
      }
      throw error;
    }
  }

  async createRecord(objectType: string, recordData: any): Promise<SalesforceCreateResponse> {
    if (!this.isAuthenticated) {
      await this.authenticate();
    }

    try {
      const url = `${this.instanceUrl}/services/data/v58.0/sobjects/${objectType}/`;
      console.log(`📤 [SalesforceAuth] Creating ${objectType} record...`);
      console.log(`   - URL: ${url}`);
      console.log(`   - Record data:`, JSON.stringify(recordData, null, 2));
      
      const response = await this.client.post(url, recordData);
      return response.data;
    } catch (error: any) {
      if (error.response?.status === 401) {
        console.log('🔄 [SalesforceAuth] Token expired, re-authenticating...');
        this.isAuthenticated = false;
        await this.authenticate();
        return this.createRecord(objectType, recordData);
      }
      
      // Log detailed error information for debugging
      console.error(`❌ [SalesforceAuth] Failed to create ${objectType} record:`);
      console.error(`   - Status: ${error.response?.status || 'N/A'}`);
      console.error(`   - Status Text: ${error.response?.statusText || 'N/A'}`);
      console.error(`   - Error Response:`, JSON.stringify(error.response?.data, null, 2));
      console.error(`   - Request Data:`, JSON.stringify(recordData, null, 2));
      
      // Return error response in Salesforce format
      if (error.response?.data) {
        const errors = Array.isArray(error.response.data) 
          ? error.response.data.map((e: any) => e.message || JSON.stringify(e))
          : error.response.data.errors 
            ? error.response.data.errors.map((e: any) => e.message || e.statusCode || JSON.stringify(e))
            : [error.response.data.message || JSON.stringify(error.response.data)];
        
        return {
          success: false,
          errors: errors
        };
      }
      
      throw error;
    }
  }

  async updateRecord(objectType: string, recordId: string, recordData: any): Promise<SalesforceCreateResponse> {
    if (!this.isAuthenticated) {
      await this.authenticate();
    }

    try {
      const url = `${this.instanceUrl}/services/data/v58.0/sobjects/${objectType}/${recordId}`;
      const response = await this.client.patch(url, recordData);
      
      // Salesforce PATCH returns 204 No Content on success, so we construct a success response
      if (response.status === 204) {
        return {
          id: recordId,
          success: true,
          errors: []
        };
      }
      
      // If there's a response body, use it
      return response.data || {
        id: recordId,
        success: true,
        errors: []
      };
    } catch (error: any) {
      if (error.response?.status === 401) {
        console.log('🔄 [SalesforceAuth] Token expired, re-authenticating...');
        this.isAuthenticated = false;
        await this.authenticate();
        return this.updateRecord(objectType, recordId, recordData);
      }

      // Log 4xx/5xx so production logs show the actual Salesforce error
      if (error.response?.status) {
        console.error(`❌ [SalesforceAuth] updateRecord failed: ${objectType} ${recordId}`);
        console.error(`   - Status: ${error.response.status} ${error.response.statusText || ''}`);
        console.error(`   - Response:`, JSON.stringify(error.response?.data, null, 2));
        console.error(`   - Payload:`, JSON.stringify(recordData, null, 2));
      }

      // Handle Salesforce API errors
      if (error.response?.data) {
        const errors = Array.isArray(error.response.data)
          ? error.response.data.map((e: any) => e.message || JSON.stringify(e))
          : [error.response.data.message || JSON.stringify(error.response.data)];

        return {
          id: recordId,
          success: false,
          errors: errors
        };
      }

      throw error;
    }
  }

  async deleteRecord(objectType: string, recordId: string): Promise<SalesforceCreateResponse> {
    if (!this.isAuthenticated) {
      await this.authenticate();
    }

    try {
      const url = `${this.instanceUrl}/services/data/v58.0/sobjects/${objectType}/${recordId}`;
      console.log(`🗑️ [SalesforceAuth] Deleting ${objectType} record: ${recordId}`);
      console.log(`   - URL: ${url}`);
      
      const response = await this.client.delete(url);
      
      // Salesforce DELETE returns 204 No Content on success
      if (response.status === 204) {
        return {
          id: recordId,
          success: true,
          errors: []
        };
      }
      
      return response.data || {
        id: recordId,
        success: true,
        errors: []
      };
    } catch (error: any) {
      if (error.response?.status === 401) {
        console.log('🔄 [SalesforceAuth] Token expired, re-authenticating...');
        this.isAuthenticated = false;
        await this.authenticate();
        return this.deleteRecord(objectType, recordId);
      }
      
      // Handle Salesforce API errors
      if (error.response?.data) {
        const errors = Array.isArray(error.response.data) 
          ? error.response.data.map((e: any) => e.message || JSON.stringify(e))
          : [error.response.data.message || JSON.stringify(error.response.data)];
        
        return {
          id: recordId,
          success: false,
          errors: errors
        };
      }
      
      throw error;
    }
  }

  async testConnection(): Promise<boolean> {
    try {
      const result = await this.query('SELECT Id FROM User LIMIT 1');
      console.log(`✅ [SalesforceAuth] Connection test successful - ${result.totalSize} users found`);
      return true;
    } catch (error) {
      console.error('❌ [SalesforceAuth] Connection test failed:', error);
      return false;
    }
  }

  get initialized(): boolean {
    return this.isAuthenticated;
  }

  get instance(): string | null {
    return this.instanceUrl;
  }

  async getAccessToken(): Promise<SalesforceAccessToken> {
    if (!this.isAuthenticated || !this.accessToken || !this.instanceUrl) {
      await this.authenticate();
    }

    if (!this.accessToken || !this.instanceUrl) {
      throw new Error("Salesforce authentication did not produce access token and instance URL.");
    }

    return {
      accessToken: this.accessToken,
      instanceUrl: this.instanceUrl
    };
  }

  /**
   * Get authenticated axios client for use in other services
   */
  async getAuthenticatedClient(): Promise<AxiosInstance> {
    if (!this.isAuthenticated) {
      await this.authenticate();
    }
    return this.client;
  }
}

// Export as class (not singleton) for flexibility
export default SalesforceAuth;