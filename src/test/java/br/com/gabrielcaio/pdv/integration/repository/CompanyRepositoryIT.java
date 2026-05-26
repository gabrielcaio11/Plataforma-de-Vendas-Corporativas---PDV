package br.com.gabrielcaio.pdv.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.gabrielcaio.pdv.domain.Company;
import br.com.gabrielcaio.pdv.repository.CompanyRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class CompanyRepositoryIT {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @DynamicPropertySource
  static void registerDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired private CompanyRepository companyRepository;

  @PersistenceContext private EntityManager entityManager;

  @Test
  @DisplayName("findByName - should return company when name exists")
  @Transactional
  void findByName_whenExists_returnsCompany() {
    Company company = new Company();
    company.setName("Acme");
    entityManager.persist(company);
    entityManager.flush();

    var found = companyRepository.findByName("Acme");

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isNotNull();
    assertThat(found.get().getName()).isEqualTo("Acme");
  }

  @Test
  @DisplayName("findByName - should return empty when name does not exist")
  @Transactional
  void findByName_whenMissing_returnsEmpty() {
    assertThat(companyRepository.findByName("Missing")).isEmpty();
  }

  @Test
  @DisplayName("unique constraint - should not allow duplicate company names")
  @Transactional
  void save_withDuplicateName_throws() {
    Company c1 = new Company();
    c1.setName("Unique");
    entityManager.persist(c1);
    entityManager.flush();

    Company c2 = new Company();
    c2.setName("Unique");

    assertThatThrownBy(() -> {
          entityManager.persist(c2);
          entityManager.flush();
        })
        .isInstanceOfAny(DataIntegrityViolationException.class, ConstraintViolationException.class);
  }
}
