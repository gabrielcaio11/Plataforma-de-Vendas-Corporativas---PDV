package br.com.gabrielcaio.pdv.controller.dto.response;

import java.math.BigDecimal;

public record TransactionResponse(
    Long id, String productName, Integer quantity, BigDecimal totalPrice) {}
