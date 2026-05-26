package br.com.gabrielcaio.pdv.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record UpdateProductRequest(
    @NotBlank(message = "O nome do produto é obrigatório") String name,
    @NotNull(message = "O preço do produto é obrigatório")
        @PositiveOrZero(message = "O preço deve ser maior ou igual a zero")
        BigDecimal price,
    Integer stock,
    Long company_id) {}
