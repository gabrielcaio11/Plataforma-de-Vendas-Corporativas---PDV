package br.com.gabrielcaio.pdv.controller.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class CreateProductRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   "})
  @NullSource
  @DisplayName("Deve falhar para diferentes tipos de nomes inválidos")
  void shouldFailWithInvalidNames(String invalidName) {
    var request = new CreateProductRequest(invalidName, new BigDecimal("5.50"), 10, 1L);
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getMessage().equals("O nome do produto é obrigatório")));
  }

  @Test
  @DisplayName("Deve falhar quando o preço for nulo")
  void shouldFailWhenPriceIsNull() {
    var request = new CreateProductRequest("Arroz", null, 10, 1L);
    var violations = validator.validate(request);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getMessage().contains("O preço do produto é obrigatório")));
  }

  @Test
  @DisplayName("Não deve aceitar preço negativo")
  void shouldNotAcceptNegativePrice() {
    var request = new CreateProductRequest("Arroz", new BigDecimal("-1.00"), 10, 1L);
    var violations = validator.validate(request);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("O preço deve ser maior ou igual a zero")));
  }

  @Test
  @DisplayName("Deve falhar quando o estoque for nulo")
  void shouldFailWhenStockIsNull() {
    var request = new CreateProductRequest("Arroz", new BigDecimal("5.50"), null, 1L);
    var violations = validator.validate(request);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getMessage().contains("A quantidade em estoque é obrigatória")));
  }

  @Test
  @DisplayName("Não deve aceitar estoque negativo")
  void shouldNotAcceptNegativeStock() {
    var request = new CreateProductRequest("Coca-Cola", new BigDecimal("5.50"), -1, 1L);
    var violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getMessage().contains("O estoque não pode ser negativo")));
  }


  @Test
  @DisplayName("Deve falhar quando o companyId for nulo")
  void shouldFailWhenCompanyIdIsNull() {
    var request = new CreateProductRequest("Arroz", new BigDecimal("5.50"), 10, null);
    var violations = validator.validate(request);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getMessage().contains("O ID da empresa é obrigatório")));
  }

  @Test
  @DisplayName("Deve falhar quando campos numéricos forem nulos")
  void shouldFailWhenNumbersAreNull() {
    var request = new CreateProductRequest(null, null, null, null);
    var violations = validator.validate(request);
    assertEquals(4, violations.size());
  }

  @Test
  void testCreateProductRequest() {
    String name = "Produto Teste";
    BigDecimal price = new BigDecimal("19.99");
    Integer stock = 10;
    Long companyId = 1L;

    CreateProductRequest request = new CreateProductRequest(name, price, stock, companyId);

    assertEquals(name, request.name());
    assertEquals(price, request.price());
    assertEquals(stock, request.stock());
    assertEquals(companyId, request.companyId());
  }

}