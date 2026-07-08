package br.com.gabrielcaio.pdv.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.gabrielcaio.pdv.controller.dto.request.CreateCompanyRequest;
import br.com.gabrielcaio.pdv.controller.dto.request.PageRequest;
import br.com.gabrielcaio.pdv.controller.dto.response.CompanyResponse;
import br.com.gabrielcaio.pdv.controller.dto.response.CompanyWithEmployeeResponse;
import br.com.gabrielcaio.pdv.controller.dto.response.CompanyWithProductsResponse;
import br.com.gabrielcaio.pdv.controller.dto.response.EmployeeResponse;
import br.com.gabrielcaio.pdv.controller.dto.response.ProductResponse;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@Tag("unit")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

  @Mock private CompanyRepository companyRepository;

  @InjectMocks private CompanyService companyService;

  @Nested
  class getAll {

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

      assertThat(response.getTotalElements()).isEqualTo(1);
      assertThat(response.getContent())
          .extracting(CompanyResponse::id, CompanyResponse::name)
          .containsExactly(tuple(1L, "Acme"));
      ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
      verify(companyRepository).findAll(captor.capture());
      Pageable captured = captor.getValue();
      assertThat(captured.getPageNumber()).isEqualTo(0);
      assertThat(captured.getPageSize()).isEqualTo(10);
      Sort sort = captured.getSort();
      assertThat(sort).hasSize(1);
      assertThat(sort.iterator().next().getProperty()).isEqualTo("name");
      assertThat(sort.iterator().next().isAscending()).isTrue();
    }

    @Test
    @DisplayName("shouldUseDefaultSortAndDirectionWhenNull")
    void shouldUseDefaultSortAndDirectionWhenNull() {
      PageRequest request = new PageRequest(0, 10, null, null);
      when(companyRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

      companyService.getAll(request);

      ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
      verify(companyRepository).findAll(captor.capture());
      Pageable captured = captor.getValue();
      Sort sort = captured.getSort();
      assertThat(sort).hasSize(1);
      assertThat(sort.iterator().next().getProperty()).isEqualTo("name");
      assertThat(sort.iterator().next().isAscending()).isTrue();
    }

    @Test
    @DisplayName("deve usar direção 'desc' quando informada")
    void shouldUseDescDirectionWhenProvided() {
      PageRequest request = new PageRequest(0, 10, "name", "desc");
      when(companyRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

      companyService.getAll(request);

      ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
      verify(companyRepository).findAll(captor.capture());
      Sort sort = captor.getValue().getSort();
      assertThat(sort.iterator().next().isDescending()).isTrue();
    }

    @Test
    @DisplayName("shouldThrowIllegalArgumentWhenSortIsInvalid")
    void shouldThrowIllegalArgumentWhenSortIsInvalid() {
      PageRequest request = new PageRequest(0, 10, "invalid", "asc");

      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> companyService.getAll(request));

      assertEquals("Invalid sort field: invalid", exception.getMessage());
    }
  }

  @Nested
  class getEmployeesByCompanyId {

    @Test
    @DisplayName("shouldReturnEmptyEmployeeListWhenCompanyHasNoEmployees")
    void shouldReturnEmptyEmployeeListWhenCompanyHasNoEmployees() {
      Company company = createCompany(1L, "Acme", List.of(), List.of());
      when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

      List<CompanyWithEmployeeResponse> responses = companyService.getEmployeesByCompanyId(1L);

      assertThat(responses)
          .hasSize(1)
          .first()
          .satisfies(
              response -> {
                assertThat(response.id()).isEqualTo(1L);
                assertThat(response.nameCompany()).isEqualTo("Acme");
                assertThat(response.employees()).isEmpty();
              });

      verify(companyRepository).findById(1L);
    }

    @Test
    @DisplayName("shouldReturnEmployeeListWhenCompanyHasEmployees")
    void shouldReturnEmployeeListWhenCompanyHasEmployees() {

      Company company = createCompany(1L, "Acme");
      User user1 = createUser(1L, "John Doe", company);
      User user2 = createUser(2L, "Jane Smith", company);
      company.setUsers(List.of(user1, user2));

      when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

      List<CompanyWithEmployeeResponse> responses = companyService.getEmployeesByCompanyId(1L);

      assertThat(responses)
          .hasSize(1)
          .first()
          .satisfies(
              response -> {
                assertThat(response.id()).isEqualTo(1L);
                assertThat(response.nameCompany()).isEqualTo("Acme");
                assertThat(response.employees())
                    .hasSize(2)
                    .extracting(EmployeeResponse::id, EmployeeResponse::name)
                    .containsExactly(tuple(1L, "John Doe"), tuple(2L, "Jane Smith"));
              });

      verify(companyRepository).findById(1L);
    }

    @Test
    @DisplayName("shouldThrowResourceNotFoundExceptionWhenGetEmployeesByCompanyIdNotExist")
    void shouldThrowResourceNotFoundExceptionWhenCompanyNotExistInGetEmployeesByCompanyId() {
      when(companyRepository.findById(99L)).thenReturn(Optional.empty());
      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class, () -> companyService.getEmployeesByCompanyId(99L));

      assertThat(exception).hasMessage("Company not found with id: " + 99L);
      verify(companyRepository).findById(99L);
    }
  }

  @Nested
  class getProductsByCompanyId {

    @Test
    @DisplayName(
        "should Throw ResourceNotFoundException When Company Not Exist In GetProductsByCompanyId")
    void shouldThrowResourceNotFoundExceptionWhenCompanyNotExistInGetProductsByCompanyId() {
      when(companyRepository.findById(99L)).thenReturn(Optional.empty());
      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class, () -> companyService.getProductsByCompanyId(99L));

      assertThat(exception).hasMessage("Company not found with id: " + 99L);
      verify(companyRepository).findById(99L);
    }

    @Test
    @DisplayName("shouldReturnEmptyProductListWhenCompanyHasNoProducts")
    void shouldReturnEmptyProductListWhenCompanyHasNoProducts() {
      Company company = createCompany(1L, "Acme", List.of(), List.of());
      when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

      // when
      List<CompanyWithProductsResponse> responses = companyService.getProductsByCompanyId(1L);

      // then
      assertThat(responses)
          .hasSize(1)
          .first()
          .satisfies(
              response -> {
                assertThat(response.id()).isEqualTo(1L);
                assertThat(response.nameCompany()).isEqualTo("Acme");
                assertThat(response.products()).isEmpty();
              });

      verify(companyRepository).findById(1L);
    }

    @Test
    @DisplayName("shouldReturnProductListWhenCompanyHasProducts")
    void shouldReturnProductListWhenCompanyHasProducts() {
      Company company = createCompany(1L, "Acme");
      Product product1 = createProduct("Product 1", BigDecimal.valueOf(10.0), company);
      Product product2 = createProduct("Product 2", BigDecimal.valueOf(20.0), company);
      company.setProducts(List.of(product1, product2));

      when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

      // when
      List<CompanyWithProductsResponse> responses = companyService.getProductsByCompanyId(1L);

      // then
      assertThat(responses)
          .hasSize(1)
          .first()
          .satisfies(
              response -> {
                assertThat(response.id()).isEqualTo(1L);
                assertThat(response.nameCompany()).isEqualTo("Acme");
                assertThat(response.products())
                    .hasSize(2)
                    .extracting(ProductResponse::name, ProductResponse::price)
                    .containsExactly(
                        tuple("Product 1", BigDecimal.valueOf(10.0)),
                        tuple("Product 2", BigDecimal.valueOf(20.0)));
              });

      verify(companyRepository).findById(1L);
    }
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

      assertThat(response)
          .isNotNull()
          .extracting(CompanyResponse::id, CompanyResponse::name)
          .containsExactly(1L, "Acme");
      verify(companyRepository).findById(1L);
      verify(companyRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("shouldThrowNotFoundWhenCompanyDoesNotExist")
    void shouldThrowNotFoundWhenCompanyDoesNotExist() {
      when(companyRepository.findById(99L)).thenReturn(Optional.empty());

      ResourceNotFoundException exception =
          assertThrows(ResourceNotFoundException.class, () -> companyService.getById(99L));

      assertThat(exception).hasMessage("Company not found with id: " + 99L);
      verify(companyRepository).findById(99L);
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

      assertThat(response)
          .extracting(CompanyResponse::id, CompanyResponse::name)
          .containsExactly(1L, "Acme");

      verify(companyRepository).findByName("Acme");

      ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
      verify(companyRepository).save(captor.capture());
      Company captured = captor.getValue();
      assertThat(captured.getId()).isNull();
      assertThat(captured.getName()).isEqualTo("Acme");
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

      assertThat(exception).hasMessage("Company name already exists: " + "Acme");

      verify(companyRepository).findByName("Acme");
      verify(companyRepository, never()).save(any(Company.class));
    }
  }

  private Company createCompany(Long id, String name) {
    Company company = new Company();
    company.setId(id);
    company.setName(name);
    return company;
  }

  private Company createCompany(Long id, String name, List<User> users, List<Product> products) {
    Company company = createCompany(id, name);
    company.setUsers(users != null ? users : List.of());
    company.setProducts(products != null ? products : List.of());
    return company;
  }

  private User createUser(Long id, String name, Company company) {
    User user = new User();
    user.setId(id);
    user.setName(name);
    user.setEmail(name.toLowerCase().replace(" ", "") + "@example.com");
    user.setPassword("password");
    user.setRole(UserRole.COLLABORATOR);
    user.setCompany(company);
    return user;
  }

  private Product createProduct(String name, BigDecimal price, Company company) {
    Product product = new Product();
    product.setName(name);
    product.setPrice(price);
    product.setCompany(company);
    return product;
  }
}
