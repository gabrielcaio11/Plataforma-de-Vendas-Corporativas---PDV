package br.com.gabrielcaio.pdv.controller.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CreateCompanyRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve passar quando o nome for válido")
  void shouldAcceptValidName() {
    var request = new CreateCompanyRequest("Minha Empresa");
    var violations = validator.validate(request);
    assertTrue(violations.isEmpty(), "Não deveria haver violações");
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "    "})
  @DisplayName("Deve falhar quando o nome estiver vazio ou apenas com espaços")
  void shouldFailWhenNameIsBlank(String invalidName) {
    var request = new CreateCompanyRequest(invalidName);
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream().anyMatch(v -> v.getMessage().equals("Nome da empresa é obrigatório")));
  }

  @Test
  @DisplayName("Deve falhar quando o nome for null")
  void shouldFailWhenNameIsNull() {
    var request = new CreateCompanyRequest(null);
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream().anyMatch(v -> v.getMessage().equals("Nome da empresa é obrigatório")));
    assertEquals("Nome da empresa é obrigatório", violations.iterator().next().getMessage());
  }
}
