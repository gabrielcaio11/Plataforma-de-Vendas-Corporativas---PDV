package br.com.gabrielcaio.pdv.controller.dto.request;

import br.com.gabrielcaio.pdv.domain.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "CPF não pode ser blank") String cpf,
    @NotBlank(message = "Name não pode ser blank") String name,
    @NotBlank(message = "Email não pode ser blank") @Email String email,
    @NotBlank(message = "Password não pode ser blank")
        @Size(min = 8, message = "Senha com no mínimo 8 caracteres")
        String password,
    @NotNull(message = "Role não pode ser null") @Valid UserRoleRequest role,
    Long company_id) {

  @AssertTrue(message = "Colaboradores devem pertencer a uma empresa")
  private boolean isCollaboratorWithCompany() {
    return !isRole(UserRole.COLLABORATOR) || company_id != null;
  }

  @AssertTrue(message = "Consumidores não devem pertencer a uma empresa")
  private boolean isConsumerWithoutCompany() {
    return !isRole(UserRole.CONSUMER) || company_id == null;
  }

  private boolean isRole(UserRole expected) {
    if (role == null || role.name() == null || role.name().isBlank()) {
      return false;
    }
    try {
      return UserRole.valueOf(role.name().toUpperCase()) == expected;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
