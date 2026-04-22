package br.com.gabrielcaio.pdv.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

    @NotBlank(message = "Name não pode ser blank")
    String name,

    @NotBlank(message = "Email não pode ser blank")
    @Email String email,

    @NotBlank(message = "Password não pode ser blank")
    @Size(min = 8, message = "Senha com no mínimo 8 caracteres")
    String password,

    @NotNull(message = "Role não pode ser null")
    UserRoleRequest role,

    Long company_id

) {}
