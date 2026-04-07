package br.com.gabrielcaio.pdv.controller.dto.response;

import java.math.BigDecimal;

public record ProductDetailsResponse(
    Long id,
    String name,
    BigDecimal price
) {}
