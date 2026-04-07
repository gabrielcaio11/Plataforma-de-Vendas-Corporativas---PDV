package br.com.gabrielcaio.pdv.service;

import br.com.gabrielcaio.pdv.controller.dto.response.CompanyResponse;
import br.com.gabrielcaio.pdv.domain.Company;
import br.com.gabrielcaio.pdv.repository.CompanyRepository;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

  private final CompanyRepository companyRepository;

  public CompanyService(CompanyRepository companyRepository) {
    this.companyRepository = companyRepository;
  }

  public CompanyResponse createCompany(String name) {
    Company company = new Company();
    company.setName(name);
    Company saved = companyRepository.save(company);
    return new CompanyResponse(saved.getId(), saved.getName());
  }
}
