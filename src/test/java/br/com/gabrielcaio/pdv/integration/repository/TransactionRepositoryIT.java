package br.com.gabrielcaio.pdv.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.gabrielcaio.pdv.domain.Company;
import br.com.gabrielcaio.pdv.domain.Product;
import br.com.gabrielcaio.pdv.domain.Transaction;
import br.com.gabrielcaio.pdv.domain.User;
import br.com.gabrielcaio.pdv.domain.UserRole;
import br.com.gabrielcaio.pdv.repository.TransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class TransactionRepositoryIT {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @DynamicPropertySource
  static void registerDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private TransactionRepository transactionRepository;

  @PersistenceContext
  private EntityManager entityManager;

  @Test
  void findByUserId_whenExists_returnsPage() {
    var user = TestDataFactory.createUserConsumer();
    var company = TestDataFactory.createCompany();
    var product = TestDataFactory.createProduct();
    var transaction = TestDataFactory.createTransaction(user, product, 1);

    entityManager.persist(user);
    entityManager.flush();

    transaction = TestDataFactory.createTransaction(user, product, 1);
    entityManager.persist(transaction);
    entityManager.flush();

    var page = transactionRepository.findByUserId(user.getId(), TestDataFactory.createPageable());

    assertThat(page).isNotEmpty();
    assertThat(page.getContent()).hasSize(1);
    assertThat(page.getContent().get(0).getId()).isEqualTo(transaction.getId());
  }

  private static class TestDataFactory {

    static Company createCompany() {
      var company = new Company();
      company.setName("Acme");
      return company;
    }

    static User createUserConsumer() {
      var user = new User();
      user.setName("John Doe");
      user.setEmail("john@gmail.com");
      user.setPassword("password");
      user.setRole(UserRole.CONSUMER);
      return user;
    }

    static Transaction createTransaction(User user, Product product, Integer quantity) {
      var transaction = new Transaction();
      transaction.setUser(user);
      transaction.setProduct(product);
      transaction.setPriceAtPurchase(product.getPrice());
      transaction.setQuantity(quantity);
      transaction.setCreatedAt(LocalDateTime.now());
      return transaction;

    }

    static Product createProduct() {
      Product product = new Product();
      product.setName("Widget");
      product.setPrice(BigDecimal.valueOf(9.99));
      return product;
    }

    static Pageable createPageable() {
      return PageRequest.of(0, 10);
    }
  }
}
