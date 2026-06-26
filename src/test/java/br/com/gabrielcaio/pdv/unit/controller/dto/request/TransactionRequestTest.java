package br.com.gabrielcaio.pdv.unit.controller.dto.request;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.gabrielcaio.pdv.controller.dto.request.TransactionRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ActiveProfiles;

@Tag("unit")
@ActiveProfiles("test")
class TransactionRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve passar com dados válidos")
  void shouldPassWithValidData() {
    var request = new TransactionRequest(1L, 2);
    assertTrue(validator.validate(request).isEmpty());
  }

  @Test
  @DisplayName("Deve falhar quando o productId for nulo")
  void shouldFailWhenProductIdIsNull() {
    var request = new TransactionRequest(null, 1);
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream().anyMatch(v -> v.getMessage().equals("O ID do produto é obrigatório")));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -10})
  @DisplayName("Deve falhar quando a quantidade não for positiva")
  void shouldFailWhenQuantityIsNotPositive(int invalidQuantity) {
    var request = new TransactionRequest(1L, invalidQuantity);
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream()
            .anyMatch(v -> v.getMessage().equals("A quantidade deve ser maior que zero")));
  }
}
