package br.com.gabrielcaio.pdv.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "E-mail é obrigatório") @Email String email,
    @NotBlank(message = "Password é obrigatório") String password) {}
