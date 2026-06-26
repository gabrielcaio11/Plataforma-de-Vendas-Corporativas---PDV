package br.com.gabrielcaio.pdv.unit.controller.dto.request;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.gabrielcaio.pdv.controller.dto.request.RegisterRequest;
import br.com.gabrielcaio.pdv.controller.dto.request.UserRoleRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ActiveProfiles;

@Tag("unit")
@ActiveProfiles("test")
class RegisterRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve passar com consumidor sem empresa")
  void shouldPassForConsumerWithoutCompany() {
    var request =
        new RegisterRequest(
            "12345678903",
            "Gabriel",
            "gabriel@test.com",
            "senha1234",
            new UserRoleRequest("CONSUMER"),
            null);
    assertTrue(validator.validate(request).isEmpty());
  }

  @Test
  @DisplayName("Deve passar com colaborador e empresa")
  void shouldPassForCollaboratorWithCompany() {
    var request =
        new RegisterRequest(
            "12345678904",
            "Gabriel",
            "colab@test.com",
            "senha1234",
            new UserRoleRequest("COLLABORATOR"),
            1L);
    assertTrue(validator.validate(request).isEmpty());
  }

  @Test
  @DisplayName("Deve falhar quando colaborador não tiver empresa")
  void shouldFailWhenCollaboratorHasNoCompany() {
    var request =
        new RegisterRequest(
            "12345678905",
            "Gabriel",
            "colab@test.com",
            "senha1234",
            new UserRoleRequest("COLLABORATOR"),
            null);
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream()
            .anyMatch(v -> v.getMessage().equals("Colaboradores devem pertencer a uma empresa")));
  }

  @Test
  @DisplayName("Deve falhar quando consumidor tiver empresa")
  void shouldFailWhenConsumerHasCompany() {
    var request =
        new RegisterRequest(
            "12345678906",
            "Gabriel",
            "consumer@test.com",
            "senha1234",
            new UserRoleRequest("CONSUMER"),
            1L);
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream()
            .anyMatch(
                v -> v.getMessage().equals("Consumidores não devem pertencer a uma empresa")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "   "})
  @NullSource
  @DisplayName("Deve falhar quando o nome for inválido")
  void shouldFailWithInvalidName(String invalidName) {
    var request =
        new RegisterRequest(
            "12345678907",
            invalidName,
            "user@test.com",
            "senha1234",
            new UserRoleRequest("CONSUMER"),
            null);
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Name não pode ser blank")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"email-invalido", "gabriel@", "   "})
  @DisplayName("Deve falhar com e-mail inválido")
  void shouldFailWithInvalidEmail(String invalidEmail) {
    var request =
        new RegisterRequest(
            "12345678908",
            "Gabriel",
            invalidEmail,
            "senha1234",
            new UserRoleRequest("CONSUMER"),
            null);
    assertFalse(validator.validate(request).isEmpty());
  }

  @Test
  @DisplayName("Deve falhar quando a senha tiver menos de 8 caracteres")
  void shouldFailWithShortPassword() {
    var request =
        new RegisterRequest(
            "12345678909",
            "Gabriel",
            "user@test.com",
            "1234567",
            new UserRoleRequest("CONSUMER"),
            null);
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream()
            .anyMatch(v -> v.getMessage().equals("Senha com no mínimo 8 caracteres")));
  }

  @Test
  @DisplayName("Deve falhar quando a role for nula")
  void shouldFailWhenRoleIsNull() {
    var request =
        new RegisterRequest("12345678901", "Gabriel", "user@test.com", "senha1234", null, null);
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Role não pode ser null")));
  }

  @Test
  @DisplayName("Deve falhar quando a role aninhada for inválida")
  void shouldFailWhenNestedRoleIsInvalid() {
    var request =
        new RegisterRequest(
            "123456778901",
            "Gabriel",
            "user@test.com",
            "senha1234",
            new UserRoleRequest("ADMIN"),
            null);
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Role inválida")));
  }
}
