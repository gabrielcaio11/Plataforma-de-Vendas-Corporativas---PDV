package br.com.gabrielcaio.pdv.repository;

import br.com.gabrielcaio.pdv.domain.Company;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {

  Optional<Company> findByName(String name);
}
