package br.com.gabrielcaio.pdv.controller.exception.error;

public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
