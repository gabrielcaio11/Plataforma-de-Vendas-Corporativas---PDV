package br.com.gabrielcaio.pdv.controller.exception.error;

public enum ErrorType {
  RESOURCE_NOT_FOUND("resource-not-found", "Resource not found"),
  VALIDATION_ERROR("validation-error", "Validation failed"),
  BUSINESS_RULE_VIOLATION("business-rule-violation", "Business rule violated"),
  ENTITY_ALREADY_EXISTS("entity-already-exists", "Entity already exists"),
  DATABASE_ERROR("database-error", "Database operation failed"),
  FORBIDDEN("forbidden", "Access denied"),
  UNAUTHORIZED("unauthorized", "Authentication required"),
  INTERNAL_ERROR("internal-error", "Internal server error");

  private static final String BASE_URI = "https://api.seusistema.com/errors/";

  private final String path;
  private final String title;

  ErrorType(String path, String title) {
    this.path = path;
    this.title = title;
  }

  public String getTypeUri() {
    return BASE_URI + path;
  }

  public String getTitle() {
    return title;
  }
}
