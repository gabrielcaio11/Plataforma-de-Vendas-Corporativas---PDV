package br.com.gabrielcaio.pdv.controller.error;

public class DataBaseException extends RuntimeException {
  public DataBaseException(String message) {
    super(message);
  }
}
