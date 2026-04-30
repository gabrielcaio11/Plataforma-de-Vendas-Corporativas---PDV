package br.com.gabrielcaio.pdv.controller.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record CreateProductRequest(
    @NotBlank(message = "O nome do produto é obrigatório") String name,
    @NotNull(message = "O preço do produto é obrigatório")
        @PositiveOrZero(message = "O preço deve ser maior ou igual a zero")
        BigDecimal price,
    @NotNull(message = "A quantidade em estoque é obrigatória")
        @Min(value = 0, message = "O estoque não pode ser negativo")
        Integer stock,
    @NotNull(message = "O ID da empresa é obrigatório") Long companyId) {}
