package br.com.gabrielcaio.pdv.service;

import br.com.gabrielcaio.pdv.controller.dto.request.PageRequestDTO;
import br.com.gabrielcaio.pdv.controller.dto.response.CompanyResponse;
import br.com.gabrielcaio.pdv.domain.Company;
import br.com.gabrielcaio.pdv.repository.CompanyRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    Company company = companyRepository.findById(id).orElseThrow();
    return new CompanyResponse(company.getId(), company.getName());
  }

  public Page<CompanyResponse> getAll(PageRequestDTO request) {

    Sort.Direction dir =
        request.direction().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

    Pageable pageable = PageRequest.of(request.page(), request.size(),
        Sort.by(dir, validateSort(request.sort())));

    Page<Company> companies = companyRepository.findAll(pageable);
    return companies.map(c -> new CompanyResponse(c.getId(), c.getName()));
  }

  private String validateSort(String sort) {

    List<String> ALLOWED_SORTS = List.of("name");
    if (!ALLOWED_SORTS.contains(sort)) {
      throw new IllegalArgumentException("Invalid sort field: " + sort);
    }
    return sort;
  }
}
