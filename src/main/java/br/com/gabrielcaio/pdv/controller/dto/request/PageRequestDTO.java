package br.com.gabrielcaio.pdv.controller.dto.request;

import org.springframework.web.bind.annotation.RequestParam;

public record PageRequestDTO(
    Integer page,
    Integer size,
    String sort,
    String direction) {

  public PageRequestDTO {
    if (page == null) page = 0;
    if (size == null) size = 10;
    if (direction == null) direction = "asc";
  }
}
