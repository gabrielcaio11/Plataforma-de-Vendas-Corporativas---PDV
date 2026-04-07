package br.com.gabrielcaio.pdv.controller.dto.request;

import br.com.gabrielcaio.pdv.domain.UserRole;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank(message = "Name não pode ser blank") String name,
    @NotBlank(message = "Email não pode ser blank") String email,
    @NotBlank(message = "Password não pode ser blank") String password,
    @NotBlank(message = "Role não pode ser blank") UserRole role,
    Long company_id
) {}
