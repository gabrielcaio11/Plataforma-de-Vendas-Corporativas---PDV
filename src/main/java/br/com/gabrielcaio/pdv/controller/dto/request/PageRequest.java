package br.com.gabrielcaio.pdv.controller.dto.request;

public record PageRequest(Integer page, Integer size, String sort, String direction) {

  public PageRequest {
    if (page == null) page = 0;
    if (size == null) size = 10;
    if (direction == null) direction = "asc";
  }
}
