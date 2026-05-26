package br.com.gabrielcaio.pdv.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRoleRequest(
    @NotBlank(message = "Name não pode ser blank")
        @Pattern(
            regexp = "CONSUMER|COLLABORATOR",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Role inválida")
        String name) {}
