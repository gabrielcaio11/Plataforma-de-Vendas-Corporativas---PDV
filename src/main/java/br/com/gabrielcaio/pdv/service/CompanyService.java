package br.com.gabrielcaio.pdv.service;

import br.com.gabrielcaio.pdv.controller.dto.request.CreateCompanyRequest;
import br.com.gabrielcaio.pdv.controller.dto.request.PageRequest;
import br.com.gabrielcaio.pdv.controller.dto.response.CompanyResponse;
import br.com.gabrielcaio.pdv.controller.dto.response.CompanyWithEmployeeResponse;
import br.com.gabrielcaio.pdv.controller.dto.response.CompanyWithProductsResponse;
import br.com.gabrielcaio.pdv.controller.dto.response.EmployeeResponse;
import br.com.gabrielcaio.pdv.controller.dto.response.ProductResponse;
import br.com.gabrielcaio.pdv.controller.exception.error.ResourceNotFoundException;
import br.com.gabrielcaio.pdv.domain.Company;
import br.com.gabrielcaio.pdv.repository.CompanyRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

  private final CompanyRepository companyRepository;

  public CompanyService(CompanyRepository companyRepository) {
    this.companyRepository = companyRepository;
  }

  public CompanyResponse create(CreateCompanyRequest request) {
    Company company = new Company();
    company.setName(request.name());
    Company saved = companyRepository.save(company);
    return new CompanyResponse(saved.getId(), saved.getName());
  }

  public CompanyResponse getById(Long id) {
    Company company =
        companyRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
    return new CompanyResponse(company.getId(), company.getName());
  }

  public Page<CompanyResponse> getAll(PageRequest request) {

    String direction = request.direction() == null ? "asc" : request.direction();
    Sort.Direction dir =
        direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

    String sort = request.sort() == null ? "name" : request.sort();

    Pageable pageable =
        org.springframework.data.domain.PageRequest.of(request.page(), request.size(), Sort.by(dir, validateSort(sort)));

    Page<Company> companies = companyRepository.findAll(pageable);
    return companies.map(c -> new CompanyResponse(c.getId(), c.getName()));
  }

  private String validateSort(String sort) {

    List<String> allowedSorts = List.of("name");
    if (!allowedSorts.contains(sort)) {
      throw new IllegalArgumentException("Invalid sort field: " + sort);
    }
    return sort;
  }

  public List<CompanyWithEmployeeResponse> getEmployeesByCompanyId(Long companyId) {
    Company company =
        companyRepository
            .findById(companyId)
            .orElseThrow(
                () -> new IllegalArgumentException("Company not found with id: " + companyId));
    List<EmployeeResponse> employeeResponses =
        company.getUsers().stream().map(u -> new EmployeeResponse(u.getId(), u.getName())).toList();
    return List.of(
        new CompanyWithEmployeeResponse(company.getId(), company.getName(), employeeResponses));
  }

  public List<CompanyWithProductsResponse> getProductsByCompanyId(Long companyId) {
    Company company =
        companyRepository
            .findById(companyId)
            .orElseThrow(
                () -> new IllegalArgumentException("Company not found with id: " + companyId));

    List<ProductResponse> productResponses =
        company.getProducts().stream()
            .map(p -> new ProductResponse(p.getName(), p.getPrice()))
            .toList();
    return List.of(
        new CompanyWithProductsResponse(company.getId(), company.getName(), productResponses));
  }
}
