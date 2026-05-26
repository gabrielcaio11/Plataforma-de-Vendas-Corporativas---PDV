package br.com.gabrielcaio.pdv.controller.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Tag("unit")
class PageRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Deve aplicar valores padrão quando os campos forem nulos")
  void shouldApplyDefaultValues() {
    var request = new PageRequest(null, null, null, null);

    assertEquals(0, request.page());
    assertEquals(10, request.size());
    assertEquals("asc", request.direction());
    assertTrue(validator.validate(request).isEmpty());
  }

  @Test
  @DisplayName("Deve retornar os valores fornecidos quando os campos não forem nulos")
  void shouldReturnProvidedValues() {
    var request = new PageRequest(2, 20, "name", "desc");

    assertEquals(2, request.page());
    assertEquals(20, request.size());
    assertEquals("desc", request.direction());
    assertEquals("name", request.sort());
    assertTrue(validator.validate(request).isEmpty());
  }

  @Test
  @DisplayName("Deve falhar quando a página for negativa")
  void shouldFailWhenPageIsNegative() {
    var request = new PageRequest(-1, 10, null, "asc");
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream().anyMatch(v -> v.getMessage().equals("A página não pode ser negativa")));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1})
  @DisplayName("Deve falhar quando o tamanho da página for menor que 1")
  void shouldFailWhenSizeIsInvalid(int invalidSize) {
    var request = new PageRequest(0, invalidSize, null, "asc");
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream()
            .anyMatch(v -> v.getMessage().equals("O tamanho da página deve ser no mínimo 1")));
  }

  @Test
  @DisplayName("Deve falhar quando o tamanho da página exceder o máximo")
  void shouldFailWhenSizeExceedsMax() {
    var request = new PageRequest(0, 101, null, "asc");
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream()
            .anyMatch(v -> v.getMessage().equals("O tamanho da página deve ser no máximo 100")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"invalid", "up", ""})
  @DisplayName("Deve falhar quando a direção não for asc ou desc")
  void shouldFailWithInvalidDirection(String invalidDirection) {
    var request = new PageRequest(0, 10, null, invalidDirection);
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream().anyMatch(v -> v.getMessage().equals("A direção deve ser asc ou desc")));
  }
}
