package br.com.gabrielcaio.pdv.controller.error;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends ErrorMessage {
  private List<FieldMessage> errors = new ArrayList<FieldMessage>();

  public ValidationError(Instant timestamp, Integer status, String error, String path) {
    super(timestamp, status, error, path);
  }

  public void addError(String field, String defaultMessage) {
    errors.add(new FieldMessage(field, defaultMessage));
  }

  public List<FieldMessage> getErrors() {
    return errors;
  }

  public ValidationError(List<FieldMessage> errors) {
    this.errors = errors;
  }
  public ValidationError() {
  }

  public ValidationError(Instant timestamp, Integer status, String error, String path,
      List<FieldMessage> errors) {
    super(timestamp, status, error, path);
    this.errors = errors;
  }
}
