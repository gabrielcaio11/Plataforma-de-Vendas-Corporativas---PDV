package br.com.gabrielcaio.pdv.controller.exception.error;

public class EntityExistsException extends RuntimeException {

  public EntityExistsException(String message) {
    super(message);
  }
}
