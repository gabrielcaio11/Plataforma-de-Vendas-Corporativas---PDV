package br.com.gabrielcaio.pdv.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserRoleRequest(@NotBlank(message = "Name não pode ser blank") String name) {}
