package br.com.gabrielcaio.pdv.controller.dto.request;

import java.math.BigDecimal;

public record ProductRequest(String name, BigDecimal price, Integer stock, Long company_id) {}
