package br.com.gabrielcaio.pdv.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "Email não pode ser null") @Email String email,
    @NotBlank @Size(min = 8,message = "Senha com no mínimo 8 caracteres") String password
) {}
