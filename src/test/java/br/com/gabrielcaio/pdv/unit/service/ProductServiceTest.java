package br.com.gabrielcaio.pdv.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.gabrielcaio.pdv.controller.dto.request.CreateProductRequest;
import br.com.gabrielcaio.pdv.controller.dto.request.UpdateProductRequest;
import br.com.gabrielcaio.pdv.controller.exception.error.BusinessException;
import br.com.gabrielcaio.pdv.controller.exception.error.ForbiddenException;
import br.com.gabrielcaio.pdv.domain.Company;
import br.com.gabrielcaio.pdv.domain.Product;
import br.com.gabrielcaio.pdv.domain.User;
import br.com.gabrielcaio.pdv.repository.CompanyRepository;
import br.com.gabrielcaio.pdv.repository.ProductRepository;
import br.com.gabrielcaio.pdv.repository.UserRepository;
import br.com.gabrielcaio.pdv.security.AuthorizationService;
import br.com.gabrielcaio.pdv.security.SecurityUtils;
import br.com.gabrielcaio.pdv.service.ProductService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@Tag("unit")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock private ProductRepository productRepository;
  @Mock private UserRepository userRepository;
  @Mock private AuthorizationService authorizationService;
  @Mock private CompanyRepository companyRepository;
  @InjectMocks private ProductService productService;

  @Test
  @DisplayName("shouldCreateProductWhenUserBelongsToSameCompany")
  void shouldCreateProductWhenUserBelongsToSameCompany() {
    Company company = new Company();
    company.setId(1L);
    company.setProducts(new ArrayList<>());

    User user = new User();
    user.setEmail("colab@test.com");
    user.setCompany(company);

    CreateProductRequest request =
        new CreateProductRequest("Arroz", new BigDecimal("9.90"), 10, 1L);

    Product saved = new Product();
    saved.setName(request.name());
    saved.setPrice(request.price());
    saved.setStock(request.stock());
    saved.setCompany(company);

    when(userRepository.findByEmail("colab@test.com")).thenReturn(Optional.of(user));
    when(productRepository.save(any(Product.class))).thenReturn(saved);

    try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getLoggedUserEmail).thenReturn("colab@test.com");

      Product result = productService.create(request);

      assertEquals("Arroz", result.getName());
      assertEquals(new BigDecimal("9.90"), result.getPrice());
      assertEquals(10, result.getStock());
      verify(companyRepository).save(company);
      verify(productRepository).save(any(Product.class));
    }
  }

  @Test
  @DisplayName("shouldThrowForbiddenWhenUserHasNoCompany")
  void shouldThrowForbiddenWhenUserHasNoCompany() {
    User user = new User();
    user.setEmail("consumer@test.com");
    user.setCompany(null);

    CreateProductRequest request =
        new CreateProductRequest("Arroz", new BigDecimal("9.90"), 10, 1L);

    when(userRepository.findByEmail("consumer@test.com")).thenReturn(Optional.of(user));

    try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getLoggedUserEmail).thenReturn("consumer@test.com");

      ForbiddenException exception =
          assertThrows(ForbiddenException.class, () -> productService.create(request));

      assertEquals("Apenas colaboradores podem criar produtos", exception.getMessage());
      verify(productRepository, never()).save(any(Product.class));
    }
  }

  @Test
  @DisplayName("shouldThrowForbiddenWhenCompanyIdDoesNotMatch")
  void shouldThrowForbiddenWhenCompanyIdDoesNotMatch() {
    Company company = new Company();
    company.setId(1L);
    company.setProducts(new ArrayList<>());

    User user = new User();
    user.setEmail("colab@test.com");
    user.setCompany(company);

    CreateProductRequest request =
        new CreateProductRequest("Arroz", new BigDecimal("9.90"), 10, 2L);

    when(userRepository.findByEmail("colab@test.com")).thenReturn(Optional.of(user));

    try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getLoggedUserEmail).thenReturn("colab@test.com");

      ForbiddenException exception =
          assertThrows(ForbiddenException.class, () -> productService.create(request));

      assertEquals("Você não pode criar produtos para outra empresa", exception.getMessage());
      verify(companyRepository, never()).save(any(Company.class));
      verify(productRepository, never()).save(any(Product.class));
    }
  }

  @Test
  @DisplayName("shouldUpdateProductWhenUserIsAuthorized")
  void shouldUpdateProductWhenUserIsAuthorized() {
    User user = new User();
    user.setEmail("colab@test.com");

    Product existing = new Product();
    existing.setId(7L);
    existing.setName("Produto antigo");
    existing.setPrice(new BigDecimal("4.00"));

    UpdateProductRequest request =
        new UpdateProductRequest("Produto novo", new BigDecimal("8.50"), 5, 1L);

    when(userRepository.findByEmail("colab@test.com")).thenReturn(Optional.of(user));
    when(productRepository.findById(7L)).thenReturn(Optional.of(existing));
    when(productRepository.save(existing)).thenReturn(existing);

    try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getLoggedUserEmail).thenReturn("colab@test.com");

      Product result = productService.update(7L, request);

      assertEquals("Produto novo", result.getName());
      assertEquals(new BigDecimal("8.50"), result.getPrice());
      verify(authorizationService).checkProductOwnership(user, existing);
      verify(productRepository).save(existing);
    }
  }

  @Test
  @DisplayName("shouldThrowBusinessExceptionWhenProductDoesNotExist")
  void shouldThrowBusinessExceptionWhenProductDoesNotExist() {
    User user = new User();
    user.setEmail("colab@test.com");
    UpdateProductRequest request =
        new UpdateProductRequest("Nome", new BigDecimal("10.00"), null, null);

    when(userRepository.findByEmail("colab@test.com")).thenReturn(Optional.of(user));
    when(productRepository.findById(99L)).thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getLoggedUserEmail).thenReturn("colab@test.com");

      BusinessException exception =
          assertThrows(BusinessException.class, () -> productService.update(99L, request));

      assertEquals("Product not founded with id: 99", exception.getMessage());
      verify(authorizationService, never())
          .checkProductOwnership(any(User.class), any(Product.class));
    }
  }

  @Test
  @DisplayName("shouldPropagateExceptionWhenAuthorizationBlocksOwnership")
  void shouldPropagateExceptionWhenAuthorizationBlocksOwnership() {
    User user = new User();
    user.setEmail("colab@test.com");

    Product existing = new Product();
    existing.setId(7L);

    UpdateProductRequest request =
        new UpdateProductRequest("Nome", new BigDecimal("10.00"), null, null);

    when(userRepository.findByEmail("colab@test.com")).thenReturn(Optional.of(user));
    when(productRepository.findById(7L)).thenReturn(Optional.of(existing));
    ForbiddenException forbidden = new ForbiddenException("Sem acesso ao produto");
    org.mockito.Mockito.doThrow(forbidden)
        .when(authorizationService)
        .checkProductOwnership(eq(user), eq(existing));

    try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getLoggedUserEmail).thenReturn("colab@test.com");

      ForbiddenException exception =
          assertThrows(ForbiddenException.class, () -> productService.update(7L, request));

      assertEquals("Sem acesso ao produto", exception.getMessage());
      verify(productRepository, never()).save(any(Product.class));
    }
  }
}
