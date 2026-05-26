package br.com.gabrielcaio.pdv.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.gabrielcaio.pdv.controller.dto.request.CreateCompanyRequest;
import br.com.gabrielcaio.pdv.controller.dto.request.PageRequest;
import br.com.gabrielcaio.pdv.controller.dto.response.CompanyResponse;
import br.com.gabrielcaio.pdv.controller.exception.error.BusinessException;
import br.com.gabrielcaio.pdv.controller.exception.error.ResourceNotFoundException;
import br.com.gabrielcaio.pdv.domain.Company;
import br.com.gabrielcaio.pdv.repository.CompanyRepository;
import br.com.gabrielcaio.pdv.service.CompanyService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

class CompanyServiceTest {

  private CompanyRepository companyRepository;
  private CompanyService companyService;

  @BeforeEach
  void setUp() {
    companyRepository = mock(CompanyRepository.class);
    companyService = new CompanyService(companyRepository);
  }

  @Test
  @DisplayName("shouldReturnCompanyWhenIdExists")
  void shouldReturnCompanyWhenIdExists() {
    Company company = new Company();
    company.setId(1L);
    company.setName("Acme");
    when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

    CompanyResponse response = companyService.getById(1L);

    assertEquals(1L, response.id());
    assertEquals("Acme", response.name());
  }

  @Test
  @DisplayName("shouldThrowNotFoundWhenCompanyDoesNotExist")
  void shouldThrowNotFoundWhenCompanyDoesNotExist() {
    when(companyRepository.findById(99L)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(ResourceNotFoundException.class, () -> companyService.getById(99L));

    assertEquals("Company not found with id: 99", exception.getMessage());
  }

  @Test
  @DisplayName("shouldReturnPagedCompaniesWhenSortIsValid")
  void shouldReturnPagedCompaniesWhenSortIsValid() {
    Company company = new Company();
    company.setId(1L);
    company.setName("Acme");
    Page<Company> page = new PageImpl<>(List.of(company));
    when(companyRepository.findAll(any(Pageable.class))).thenReturn(page);

    PageRequest request = new PageRequest(0, 10, "name", "asc");

    Page<CompanyResponse> response = companyService.getAll(request);

    assertEquals(1, response.getTotalElements());
    assertEquals("Acme", response.getContent().get(0).name());
    verify(companyRepository).findAll(any(Pageable.class));
  }

  @Test
  @DisplayName("shouldThrowIllegalArgumentWhenSortIsInvalid")
  void shouldThrowIllegalArgumentWhenSortIsInvalid() {
    PageRequest request = new PageRequest(0, 10, "invalid", "asc");

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> companyService.getAll(request));

    assertEquals("Invalid sort field: invalid", exception.getMessage());
  }

  @Test
  @DisplayName("shouldBusinessExceptionWhenNameExists")
  void shouldBusinessExceptionWhenNameExists() {
    when(companyRepository.findByName("Acme")).thenReturn(Optional.of(new Company()));

    CreateCompanyRequest request = new CreateCompanyRequest("Acme");

    BusinessException exception =
        assertThrows(BusinessException.class, () -> companyService.create(request));

    assertEquals("Company name already exists: Acme", exception.getMessage());
  }

  @Test
  @DisplayName("shouldCreateCompanyWhenNameIsValid")
  void shouldCreateCompanyWhenNameIsValid() {
    when(companyRepository.findByName("Acme")).thenReturn(Optional.empty());
    when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> {
      Company company = invocation.getArgument(0);
      company.setId(1L);
      return company;
    });

    CreateCompanyRequest request = new CreateCompanyRequest("Acme");

    CompanyResponse response = companyService.create(request);

    assertEquals(1L, response.id());
    assertEquals("Acme", response.name());
  }
}
