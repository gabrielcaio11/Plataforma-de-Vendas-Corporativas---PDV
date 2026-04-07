package br.com.gabrielcaio.pdv.controller.dto.response;

import java.math.BigDecimal;

public record ProductResponse(
        String name,
        BigDecimal price
) {}