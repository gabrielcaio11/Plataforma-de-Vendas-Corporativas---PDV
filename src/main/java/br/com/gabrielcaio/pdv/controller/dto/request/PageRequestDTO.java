package br.com.gabrielcaio.pdv.controller.dto.request;

import org.springframework.web.bind.annotation.RequestParam;

public record PageRequestDTO(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    String sort,
    @RequestParam(defaultValue = "asc") String direction
) {

}
