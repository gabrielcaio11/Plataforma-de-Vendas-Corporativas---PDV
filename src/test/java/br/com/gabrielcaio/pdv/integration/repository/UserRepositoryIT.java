package br.com.gabrielcaio.pdv.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.gabrielcaio.pdv.domain.Company;
import br.com.gabrielcaio.pdv.domain.User;
import br.com.gabrielcaio.pdv.domain.UserRole;
import br.com.gabrielcaio.pdv.integration.base.BaseRepositoryTest;
import br.com.gabrielcaio.pdv.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Tag("integration")
class UserRepositoryIT extends BaseRepositoryTest {

  @Autowired private UserRepository userRepository;

  @PersistenceContext private EntityManager entityManager;

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

    var found = userRepository.findByEmail(user.getEmail());

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
