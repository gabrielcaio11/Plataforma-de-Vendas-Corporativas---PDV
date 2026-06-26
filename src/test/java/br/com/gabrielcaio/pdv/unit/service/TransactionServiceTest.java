package br.com.gabrielcaio.pdv.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.gabrielcaio.pdv.controller.dto.request.TransactionRequest;
import br.com.gabrielcaio.pdv.controller.dto.response.TransactionResponse;
import br.com.gabrielcaio.pdv.controller.exception.error.ResourceNotFoundException;
import br.com.gabrielcaio.pdv.domain.Product;
import br.com.gabrielcaio.pdv.domain.Transaction;
import br.com.gabrielcaio.pdv.domain.User;
import br.com.gabrielcaio.pdv.repository.ProductRepository;
import br.com.gabrielcaio.pdv.repository.TransactionRepository;
import br.com.gabrielcaio.pdv.repository.UserRepository;
import br.com.gabrielcaio.pdv.security.SecurityUtils;
import br.com.gabrielcaio.pdv.service.TransactionService;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.context.ActiveProfiles;

@Tag("unit")
@ActiveProfiles("test")
class TransactionServiceTest {

  private UserRepository userRepository;
  private ProductRepository productRepository;
  private TransactionRepository transactionRepository;
  private TransactionService transactionService;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    productRepository = mock(ProductRepository.class);
    transactionRepository = mock(TransactionRepository.class);
    transactionService =
        new TransactionService(userRepository, productRepository, transactionRepository);
  }

  @Test
  @DisplayName("shouldCreateTransactionAndCalculateTotalPrice")
  void shouldCreateTransactionAndCalculateTotalPrice() {
    User user = new User();
    user.setId(5L);
    user.setEmail("buyer@test.com");

    Product product = new Product();
    product.setId(10L);
    product.setName("Notebook");
    product.setPrice(new BigDecimal("100.00"));

    TransactionRequest request = new TransactionRequest(10L, 2);

    Transaction saved = new Transaction();
    saved.setId(20L);
    saved.setUser(user);
    saved.setProduct(product);
    saved.setQuantity(2);
    saved.setPriceAtPurchase(new BigDecimal("100.00"));

    when(userRepository.findByEmail("buyer@test.com")).thenReturn(Optional.of(user));
    when(productRepository.findById(10L)).thenReturn(Optional.of(product));
    when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

    try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getLoggedUserEmail).thenReturn("buyer@test.com");

      TransactionResponse response = transactionService.create(request);

      assertEquals(20L, response.id());
      assertEquals("Notebook", response.productName());
      assertEquals(2, response.quantity());
      assertEquals(new BigDecimal("200.00"), response.totalPrice());
      verify(transactionRepository).save(any(Transaction.class));
    }
  }

  @Test
  @DisplayName("shouldThrowWhenProductDoesNotExist")
  void shouldThrowWhenProductDoesNotExist() {
    User user = new User();
    user.setEmail("buyer@test.com");
    TransactionRequest request = new TransactionRequest(99L, 2);

    when(userRepository.findByEmail("buyer@test.com")).thenReturn(Optional.of(user));
    when(productRepository.findById(99L)).thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getLoggedUserEmail).thenReturn("buyer@test.com");

      ResourceNotFoundException exception =
          assertThrows(ResourceNotFoundException.class, () -> transactionService.create(request));

      assertEquals("Produto não encontrado", exception.getMessage());
      verify(transactionRepository, never()).save(any(Transaction.class));
    }
  }

  @Test
  @DisplayName("shouldThrowWhenAuthenticatedUserIsMissing")
  void shouldThrowWhenAuthenticatedUserIsMissing() {
    TransactionRequest request = new TransactionRequest(10L, 2);
    when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getLoggedUserEmail).thenReturn("ghost@test.com");

      assertThrows(
          java.util.NoSuchElementException.class, () -> transactionService.create(request));

      verify(productRepository, never()).findById(any(Long.class));
      verify(transactionRepository, never()).save(any(Transaction.class));
    }
  }
}
