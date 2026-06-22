package br.com.gabrielcaio.pdv.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.gabrielcaio.pdv.domain.Company;
import br.com.gabrielcaio.pdv.domain.User;
import br.com.gabrielcaio.pdv.domain.UserRole;
import br.com.gabrielcaio.pdv.repository.CompanyRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class UserRepositoryIT {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @DynamicPropertySource
  static void registerDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private CompanyRepository companyRepository;

  @PersistenceContext
  private EntityManager entityManager;

  @Test
  @DisplayName("findByEmail - should return user when email exists")
  @Transactional
  void findByEmail() {

    // criar empresa
    var company = TestDataFactory.createCompany();
    entityManager.persist(company);
    entityManager.flush();

    // criar usuário
    var user = TestDataFactory.createUserAdmin(company);
    entityManager.persist(user);
    entityManager.flush();

    var found = companyRepository.findByName(company.getName());

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(user.getId());

  }

  @Test
  @DisplayName("findByEmail - should return empty when email does not exist")
  @Transactional
  void findByEmail_whenNotExists_returnsEmpty() {
    assertThat(userRepository.findByEmail("missing@test.com")).isEmpty();
  }

  private static class TestDataFactory {

    static Company createCompany() {
      var company = new Company();
      company.setName("Acme");
      return company;
    }

    static User createUserAdmin(Company company) {
      var user = new User();
      user.setCpf("12345678909");
      user.setCompany(company);
      user.setName("John Doe");
      user.setEmail("john@gmail.com");
      user.setPassword("password");
      user.setRole(UserRole.CONSUMER);
      return user;
    }
  }
}