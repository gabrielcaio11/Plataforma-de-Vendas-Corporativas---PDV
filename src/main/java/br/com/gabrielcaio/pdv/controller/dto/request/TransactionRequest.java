package br.com.gabrielcaio.pdv.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransactionRequest(
    @NotNull(message = "O ID do produto é obrigatório") Long productId,
    @Positive(message = "A quantidade deve ser maior que zero") int quantity) {}
