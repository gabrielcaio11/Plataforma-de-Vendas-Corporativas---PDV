package br.com.gabrielcaio.pdv.controller.error;

public class EntityExistsException extends RuntimeException {
  public EntityExistsException(String message) {
    super(message);
  }
}
