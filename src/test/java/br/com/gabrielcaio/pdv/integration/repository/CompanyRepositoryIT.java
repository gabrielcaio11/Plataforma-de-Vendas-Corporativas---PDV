package br.com.gabrielcaio.pdv.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.gabrielcaio.pdv.domain.Company;
import br.com.gabrielcaio.pdv.integration.base.BaseRepositoryTest;
import br.com.gabrielcaio.pdv.repository.CompanyRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Tag("integration")
class CompanyRepositoryIT extends BaseRepositoryTest {

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

    assertThatThrownBy(
            () -> {
              entityManager.persist(c2);
              entityManager.flush();
            })
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("duplicate key");
  }
}
