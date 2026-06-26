package br.com.gabrielcaio.pdv.unit.controller.dto.request;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.gabrielcaio.pdv.controller.dto.request.LoginRequest;
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
class LoginRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @ParameterizedTest
  @ValueSource(strings = {"email-invalido", "gabriel@", "@teste.com", "   "})
  @DisplayName("Deve falhar com e-mails em formato inválido ou em branco")
  void shouldFailWithInvalidEmail(String invalidEmail) {
    var request = new LoginRequest(invalidEmail, "senha1234");
    var violations = validator.validate(request);
    assertFalse(violations.isEmpty());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", " ", "   "})
  @DisplayName("Deve falhar quando a senha estiver vazia, nula ou em branco")
  void shouldFailWithBlankPassword(String invalidPassword) {
    var request = new LoginRequest("dev@gabrielcaio.com", invalidPassword);
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password é obrigatório")));
  }

  @Test
  @DisplayName("Deve aceitar senha com menos de 8 caracteres no login")
  void shouldAcceptShortPasswordOnLogin() {
    var request = new LoginRequest("dev@gabrielcaio.com", "1234567");
    var violations = validator.validate(request);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("Deve passar com dados totalmente válidos")
  void shouldPassWithValidData() {
    var request = new LoginRequest("dev@gabrielcaio.com", "password123");
    var violations = validator.validate(request);
    assertTrue(violations.isEmpty());
  }
}
