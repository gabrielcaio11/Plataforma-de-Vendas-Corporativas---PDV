package br.com.gabrielcaio.pdv.service;

import br.com.gabrielcaio.pdv.controller.dto.response.CompanyResponse;
import br.com.gabrielcaio.pdv.domain.Company;
import br.com.gabrielcaio.pdv.repository.CompanyRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

  private final CompanyRepository companyRepository;

  public CompanyService(CompanyRepository companyRepository) {
    this.companyRepository = companyRepository;
  }

  public CompanyResponse create(String name) {
    Company company = new Company();
    company.setName(name);
    Company saved = companyRepository.save(company);
    return new CompanyResponse(saved.getId(), saved.getName());
  }

  public CompanyResponse getById(Long id) {
    Company company = companyRepository.findById(id)
        .orElseThrow();
    return new CompanyResponse(company.getId(), company.getName());
  }

  public List<CompanyResponse> getAll() {
    List<Company> companies = companyRepository.findAll();
    return companies.stream()
        .map(c -> new CompanyResponse(c.getId(), c.getName()))
        .toList();
  }
}
