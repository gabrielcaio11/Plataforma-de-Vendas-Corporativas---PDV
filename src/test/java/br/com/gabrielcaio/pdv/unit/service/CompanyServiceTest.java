package br.com.gabrielcaio.pdv.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.gabrielcaio.pdv.controller.dto.request.CreateCompanyRequest;
import br.com.gabrielcaio.pdv.controller.dto.request.PageRequest;
import br.com.gabrielcaio.pdv.controller.dto.response.CompanyResponse;
import br.com.gabrielcaio.pdv.controller.dto.response.CompanyWithEmployeeResponse;
import br.com.gabrielcaio.pdv.controller.dto.response.CompanyWithProductsResponse;
import br.com.gabrielcaio.pdv.controller.exception.error.BusinessException;
import br.com.gabrielcaio.pdv.controller.exception.error.ResourceNotFoundException;
import br.com.gabrielcaio.pdv.domain.Company;
import br.com.gabrielcaio.pdv.domain.Product;
import br.com.gabrielcaio.pdv.domain.User;
import br.com.gabrielcaio.pdv.domain.UserRole;
import br.com.gabrielcaio.pdv.repository.CompanyRepository;
import br.com.gabrielcaio.pdv.service.CompanyService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@Tag("unit")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

  @Mock private CompanyRepository companyRepository;

  @InjectMocks private CompanyService companyService;

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
  @DisplayName("shouldBusinessExceptionWhenCompanyNameExists")
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
    when(companyRepository.save(any(Company.class)))
        .thenAnswer(
            invocation -> {
              Company company = invocation.getArgument(0);
              company.setId(1L);
              return company;
            });

    CreateCompanyRequest request = new CreateCompanyRequest("Acme");

    CompanyResponse response = companyService.create(request);

    assertEquals(1L, response.id());
    assertEquals("Acme", response.name());
  }

  @Test
  @DisplayName(
      "should Throw ResourceNotFoundException When Company Not Exist In GetProductsByCompanyId")
  void shouldThrowResourceNotFoundExceptionWhenCompanyNotExistInGetProductsByCompanyId() {
    when(companyRepository.findById(99L)).thenReturn(Optional.empty());
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class, () -> companyService.getProductsByCompanyId(99L));

    assertEquals("Company not found with id: 99", exception.getMessage());
  }

  @Test
  @DisplayName("shouldReturnEmptyEmployeeListWhenCompanyHasNoEmployees")
  void shouldReturnEmptyEmployeeListWhenCompanyHasNoEmployees() {
    Company company = new Company();
    company.setId(1L);
    company.setName("Acme");
    company.setUsers(List.of());

    when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

    List<CompanyWithEmployeeResponse> response = companyService.getEmployeesByCompanyId(1L);

    assertEquals(1, response.size());
    assertEquals(1L, response.get(0).id());
    assertEquals("Acme", response.get(0).nameCompany());
    assertTrue(response.get(0).employees().isEmpty());
  }

  @Test
  @DisplayName("shouldReturnEmployeeListWhenCompanyHasEmployees")
  void shouldReturnEmployeeListWhenCompanyHasEmployees() {

    User user1 = new User();
    user1.setId(1L);
    user1.setName("John Doe");
    user1.setEmail("gabriel@gmail.com");
    user1.setPassword("password");
    user1.setRole(UserRole.COLLABORATOR);

    User user2 = new User();
    user2.setId(2L);
    user2.setName("Jane Smith");
    user2.setEmail("jane@gmail.com");
    user2.setPassword("password");
    user2.setRole(UserRole.COLLABORATOR);

    Company company = new Company();
    company.setId(1L);
    company.setName("Acme");
    company.setUsers(List.of(user1, user2));

    user1.setCompany(company);
    user2.setCompany(company);

    company.setUsers(List.of(user1, user2));

    when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

    List<CompanyWithEmployeeResponse> response = companyService.getEmployeesByCompanyId(1L);
    assertEquals(1, response.size());
    assertEquals(1L, response.get(0).id());
    assertEquals("Acme", response.get(0).nameCompany());
    assertEquals(2, response.get(0).employees().size());
    assertEquals(1L, response.get(0).employees().get(0).id());
    assertEquals("John Doe", response.get(0).employees().get(0).name());
    assertEquals(2L, response.get(0).employees().get(1).id());
    assertEquals("Jane Smith", response.get(0).employees().get(1).name());
  }

  @Test
  @DisplayName("shouldThrowResourceNotFoundExceptionWhenGetEmployeesByCompanyIdNotExist")
  void shouldThrowResourceNotFoundExceptionWhenCompanyNotExistInGetEmployeesByCompanyId() {
    when(companyRepository.findById(99L)).thenReturn(Optional.empty());
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class, () -> companyService.getEmployeesByCompanyId(99L));

    assertEquals("Company not found with id: 99", exception.getMessage());
  }

  @Test
  @DisplayName("shouldReturnEmptyProductListWhenCompanyHasNoProducts")
  void shouldReturnEmptyProductListWhenCompanyHasNoProducts() {
    Company company = new Company();
    company.setId(1L);
    company.setName("Acme");
    company.setProducts(List.of());

    when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

    List<CompanyWithProductsResponse> response = companyService.getProductsByCompanyId(1L);

    assertEquals(1, response.size());
    assertEquals(1L, response.get(0).id());
    assertEquals("Acme", response.get(0).nameCompany());
    assertTrue(response.get(0).products().isEmpty());
  }

  @Test
  @DisplayName("shouldReturnProductListWhenCompanyHasProducts")
  void shouldReturnProductListWhenCompanyHasProducts() {
    Company company = new Company();
    company.setId(1L);
    company.setName("Acme");

    Product product1 = new Product();
    product1.setName("Product 1");
    product1.setPrice(BigDecimal.valueOf(10.0));
    product1.setCompany(company);

    Product product2 = new Product();
    product2.setName("Product 2");
    product2.setPrice(BigDecimal.valueOf(20.0));
    product2.setCompany(company);

    company.setProducts(List.of(product1, product2));

    when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

    List<CompanyWithProductsResponse> response = companyService.getProductsByCompanyId(1L);

    assertEquals(1, response.size());
    assertEquals(1L, response.get(0).id());
    assertEquals("Acme", response.get(0).nameCompany());
    assertEquals(2, response.get(0).products().size());
    assertEquals("Product 1", response.get(0).products().get(0).name());
    assertEquals(BigDecimal.valueOf(10.0), response.get(0).products().get(0).price());
    assertEquals("Product 2", response.get(0).products().get(1).name());
    assertEquals(BigDecimal.valueOf(20.0), response.get(0).products().get(1).price());
  }

  @Nested
  class getById {

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
  }

  @Nested
  class create {

    @Test
    @DisplayName("shouldCreateCompanyWhenValidDataIsProvided")
    void shouldCreateCompanyWhenValidDataIsProvided() {
      CreateCompanyRequest createCompanyRequest = new CreateCompanyRequest("Acme");
      when(companyRepository.findByName("Acme")).thenReturn(Optional.empty());

      Company savedCompany = new Company();
      savedCompany.setId(1L);
      savedCompany.setName("Acme");
      when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);

      CompanyResponse response = companyService.create(createCompanyRequest);

      assertEquals("Acme", response.name());
    }

    @Test
    @DisplayName("shouldThrowBusinessExceptionWhenCompanyNameAlreadyExists")
    void shouldThrowBusinessExceptionWhenCompanyNameAlreadyExists() {
      CreateCompanyRequest createCompanyRequest = new CreateCompanyRequest("Acme");
      Company savedCompany = new Company();
      savedCompany.setId(1L);
      savedCompany.setName("Acme");
      when(companyRepository.findByName("Acme")).thenReturn(Optional.of(savedCompany));

      BusinessException exception =
          assertThrows(BusinessException.class, () -> companyService.create(createCompanyRequest));

      assertEquals("Company name already exists: Acme", exception.getMessage());
    }
  }
}
