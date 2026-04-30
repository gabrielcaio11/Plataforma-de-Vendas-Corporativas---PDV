package br.com.gabrielcaio.pdv.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCompanyRequest(
    @NotBlank(message = "Nome da empresa é obrigatório") String name) {}
