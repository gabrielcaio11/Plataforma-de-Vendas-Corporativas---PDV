package br.com.gabrielcaio.pdv.unit.controller.dto.request;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

@Tag("unit")
class UserRoleRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @ParameterizedTest
  @ValueSource(strings = {"CONSUMER", "COLLABORATOR", "consumer", "collaborator"})
  @DisplayName("Deve aceitar roles válidas")
  void shouldAcceptValidRoles(String roleName) {
    var request = new UserRoleRequest(roleName);
    assertTrue(validator.validate(request).isEmpty());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "ADMIN", "invalid"})
  @NullSource
  @DisplayName("Deve falhar com role inválida ou em branco")
  void shouldFailWithInvalidRole(String invalidRole) {
    var request = new UserRoleRequest(invalidRole);
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("Deve retornar mensagem específica para role inválida")
  void shouldFailWithInvalidRoleMessage() {
    var request = new UserRoleRequest("ADMIN");
    var violations = validator.validate(request);

    assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Role inválida")));
  }
}
