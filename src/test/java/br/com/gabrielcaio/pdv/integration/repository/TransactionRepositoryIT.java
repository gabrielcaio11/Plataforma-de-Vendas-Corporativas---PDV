package br.com.gabrielcaio.pdv.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.gabrielcaio.pdv.domain.CPF;
import br.com.gabrielcaio.pdv.domain.Company;
import br.com.gabrielcaio.pdv.domain.Product;
import br.com.gabrielcaio.pdv.domain.Transaction;
import br.com.gabrielcaio.pdv.domain.User;
import br.com.gabrielcaio.pdv.domain.UserRole;
import br.com.gabrielcaio.pdv.integration.base.BaseRepositoryTest;
import br.com.gabrielcaio.pdv.repository.TransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Tag("integration")
class TransactionRepositoryIT extends BaseRepositoryTest {

  @Autowired private TransactionRepository transactionRepository;

  @PersistenceContext private EntityManager entityManager;

  @Test
  @DisplayName("findByUserId - should return page of transactions when user has transactions")
  @Transactional
  void findByUserId_whenExists_returnsPage() {
    var user = TestDataFactory.createUserConsumer();
    var company = TestDataFactory.createCompany();

    entityManager.persist(user);
    entityManager.persist(company);

    var product = TestDataFactory.createProduct();
    product.setCompany(company);
    entityManager.persist(product);

    var transaction = TestDataFactory.createTransaction(user, product, 1);
    entityManager.persist(transaction);
    entityManager.flush();

    var page = transactionRepository.findByUserId(user.getId(), TestDataFactory.createPageable());

    assertThat(page).isNotEmpty();
    assertThat(page.getContent()).hasSize(1);
    assertThat(page.getContent().get(0).getId()).isEqualTo(transaction.getId());
  }

  @Test
  @DisplayName("findByUserId - should return empty page when user has no transactions")
  @Transactional
  void findByUserId_whenNoTransactions_returnsEmptyPage() {
    // sem empresa, apenas para ter um ID
    var user = TestDataFactory.defaultUser(null);
    entityManager.persist(user);
    entityManager.flush();

    var page = transactionRepository.findByUserId(user.getId(), PageRequest.of(0, 10));
    assertThat(page).isEmpty();
  }

  private static class TestDataFactory {

    static Company createCompany() {
      var company = new Company();
      company.setName("Acme");
      return company;
    }

    static User createUserConsumer() {
      var user = new User();
      user.setCpf("12345678909");
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
      product.setDescription("A useful widget");
      product.setName("Widget");
      product.setPrice(BigDecimal.valueOf(9.99));
      product.setStock(100);
      return product;
    }

    public static User defaultUser(Company company) {
      return createUser(
          getCpf(),
          "John Doe",
          "user-" + UUID.randomUUID() + "@test.com",
          UserRole.CONSUMER,
          company);
    }

    private static String getCpf() {
      return CPF.random().value();
    }

    public static User createUser(
        String cpf, String name, String email, UserRole role, Company company) {
      User u = new User();
      u.setCpf(cpf);
      u.setName(name);
      u.setEmail(email);
      u.setPassword("password");
      u.setRole(role);
      u.setCompany(company);
      return u;
    }

    static Pageable createPageable() {
      return PageRequest.of(0, 10);
    }
  }
}
