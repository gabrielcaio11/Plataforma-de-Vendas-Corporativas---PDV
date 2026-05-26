package br.com.gabrielcaio.pdv.controller.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record PageRequest(
    @Min(value = 0, message = "A página não pode ser negativa") Integer page,
    @Min(value = 1, message = "O tamanho da página deve ser no mínimo 1")
        @Max(value = 100, message = "O tamanho da página deve ser no máximo 100")
        Integer size,
    String sort,
    @Pattern(
            regexp = "asc|desc",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "A direção deve ser asc ou desc")
        String direction) {

  public PageRequest {
    if (page == null) page = 0;
    if (size == null) size = 10;
    if (direction == null) direction = "asc";
  }
}
