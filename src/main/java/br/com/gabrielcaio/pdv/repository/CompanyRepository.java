package br.com.gabrielcaio.pdv.repository;

import br.com.gabrielcaio.pdv.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {}
