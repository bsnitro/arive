import { z } from "zod";

const EnvSchema = z.object({
  NODE_ENV: z.enum(["development", "test", "production"]).default("development"),
  ARIVE_API_KEY: z.string().min(1),
  ARIVE_CLIENT_ID: z.string().min(1),
  ARIVE_SECRET_ID: z.string().min(1),
  ARIVE_AUTH_URL: z
    .string()
    .url()
    .default("https://gwapiconnect.myarive.com/api/auth/access-token"),
  ARIVE_BASE_URL: z
    .string()
    .url()
    .default("https://gwapiconnect.myarive.com/api"),
  ARIVE_WEBHOOK_SECRET: z.string().min(1).optional(),
  SALESFORCE_AUTH_MODE: z.enum(["shared-module", "local"]).default("shared-module"),
  SALESFORCE_REST_URL: z.string().url().optional(),
  SALESFORCE_INSTANCE_URL: z.string().url().optional(),
  SALESFORCE_CONSUMER_KEY: z.string().optional(),
  SALESFORCE_CONSUMER_SECRET: z.string().optional(),
  SALESFORCE_GRANT_TYPE: z.string().optional(),
  SALESFORCE_JWT_AUDIENCE: z.string().url().optional(),
  SALESFORCE_USERNAME: z.string().optional(),
  SALESFORCE_PASSWORD: z.string().optional(),
  SALESFORCE_SECURITY_TOKEN: z.string().optional(),
  SALESFORCE_PRIVATE_KEY: z.string().optional(),
  SALESFORCE_PRIVATE_KEY_PATH: z.string().optional()
});

export type AppEnv = z.infer<typeof EnvSchema>;

export function readEnv(rawEnv: NodeJS.ProcessEnv = process.env): AppEnv {
  return EnvSchema.parse(rawEnv);
}
