export class AppError extends Error {
  constructor(
    message: string,
    readonly code: string,
    readonly statusCode = 500
  ) {
    super(message);
    this.name = "AppError";
  }
}

export class UnauthorizedError extends AppError {
  constructor(message: string) {
    super(message, "UNAUTHORIZED", 401);
    this.name = "UnauthorizedError";
  }
}
