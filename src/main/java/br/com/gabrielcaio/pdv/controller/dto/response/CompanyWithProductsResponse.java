package br.com.gabrielcaio.pdv.controller.dto.response;

import java.util.List;

public record CompanyWithProductsResponse(
    Long id, String nameCompany, List<ProductResponse> products) {}
