package br.com.gabrielcaio.pdv.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.gabrielcaio.pdv.controller.dto.request.CreateCompanyRequest;
import br.com.gabrielcaio.pdv.controller.dto.request.PageRequestDTO;
import br.com.gabrielcaio.pdv.controller.dto.response.CompanyResponse;
import br.com.gabrielcaio.pdv.domain.Company;
import br.com.gabrielcaio.pdv.domain.User;
import br.com.gabrielcaio.pdv.domain.UserRole;
import br.com.gabrielcaio.pdv.repository.CompanyRepository;
import br.com.gabrielcaio.pdv.repository.UserRepository;
import br.com.gabrielcaio.pdv.security.CustomAuthenticationEntryPoint;
import br.com.gabrielcaio.pdv.security.CustomUserDetailsService;
import br.com.gabrielcaio.pdv.security.JwtAuthenticationFilter;
import br.com.gabrielcaio.pdv.security.JwtService;
import br.com.gabrielcaio.pdv.security.SecurityConfig;
import br.com.gabrielcaio.pdv.service.CompanyService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CompanyController.class)
@AutoConfigureMockMvc
@Import({
  SecurityConfig.class,
  JwtAuthenticationFilter.class,
  CustomAuthenticationEntryPoint.class,
  JwtService.class,
  CustomUserDetailsService.class
})
class CompanyControllerJwtWebMvcTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private JwtService jwtService;

  @MockitoBean private CompanyService companyService;

  @MockitoBean private UserRepository userRepository;

  @MockitoBean private CompanyRepository companyRepository;

  @Test
  @DisplayName("POST /companies - without Bearer token should return 401 Unauthorized")
  void create_withoutBearer_returnsUnauthorized() throws Exception {
    CreateCompanyRequest request = new CreateCompanyRequest("Acme");
    mockMvc
        .perform(
            post("/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Acme\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("POST /companies - with valid JWT should return 201 Created")
  void create_withValidJwt_returnsCreated() throws Exception {

    User user = new User();
    user.setId(10L);
    user.setName("Api");
    user.setEmail("jwt-mvc@test.com");
    user.setPassword("ignored");
    user.setRole(UserRole.CONSUMER);
    user.setCompany(null);

    Company company = new Company();
    company.setName("Acme");

    Company savedCompany = new Company();
    savedCompany.setId(1L);
    savedCompany.setName("Acme");

    when(companyRepository.save(eq(company))).thenReturn(savedCompany);

    when(companyService.create(eq(new CreateCompanyRequest("Acme"))))
        .thenReturn(new CompanyResponse(1L, "Acme"));

    when(userRepository.findByEmail("jwt-mvc@test.com")).thenReturn(Optional.of(user));
    when(companyService.getById(eq(1L))).thenReturn(new CompanyResponse(1L, "Acme"));

    String token = jwtService.generateToken(user);

    mockMvc
        .perform(
            post("/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Acme\"}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Acme"));

    when(userRepository.findByEmail("jwt-mvc@test.com")).thenReturn(Optional.of(user));
    when(companyService.getById(eq(1L))).thenReturn(new CompanyResponse(1L, "Acme"));

    mockMvc
        .perform(
            get("/companies/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Acme"));
  }

  @Test
  @DisplayName("GET /companies/{company_id} - without Bearer token should return 401 Unauthorized")
  void getById_withoutBearer_returnsUnauthorized() throws Exception {
    mockMvc
        .perform(get("/companies/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("GET /companies/{company_id} - with valid JWT should return company details")
  void getById_withValidJwt_returnsCompany() throws Exception {
    User user = new User();
    user.setId(10L);
    user.setName("Api");
    user.setEmail("jwt-mvc@test.com");
    user.setPassword("ignored");
    user.setRole(UserRole.CONSUMER);
    user.setCompany(null);

    when(userRepository.findByEmail("jwt-mvc@test.com")).thenReturn(Optional.of(user));
    when(companyService.getById(eq(1L))).thenReturn(new CompanyResponse(1L, "Acme"));

    String token = jwtService.generateToken(user);

    mockMvc
        .perform(
            get("/companies/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Acme"));
  }

  @Test
  @DisplayName("GET /companies - whitout Bearer token should return 401 Unauthorized")
  void getAll_withoutBearer_returnsUnauthorized() throws Exception {
    PageRequestDTO pageRequest = new PageRequestDTO(0, 10, "name", "asc");

    mockMvc
        .perform(
            get("/companies")
                .accept(MediaType.APPLICATION_JSON)
                .param("page", String.valueOf(pageRequest.page()))
                .param("size", String.valueOf(pageRequest.size()))
                .param("sort", pageRequest.sort())
                .param("direction", pageRequest.direction()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("GET /companies - with valid JWT should return paginated list of companies")
  void getAll_withValidJwt_returnsAllCompanies() throws Exception {
    User user = new User();
    user.setId(10L);
    user.setName("Api");
    user.setEmail("jwt-mvc@test.com");
    user.setPassword("ignored");
    user.setRole(UserRole.CONSUMER);
    user.setCompany(null);

    PageRequestDTO pageRequest = new PageRequestDTO(0, 10, "name", "asc");

    when(userRepository.findByEmail("jwt-mvc@test.com")).thenReturn(Optional.of(user));
    when(companyService.getById(eq(1L))).thenReturn(new CompanyResponse(1L, "Acme"));

    String token = jwtService.generateToken(user);

    mockMvc
        .perform(
            get("/companies")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .param("page", String.valueOf(pageRequest.page()))
                .param("size", String.valueOf(pageRequest.size()))
                .param("sort", pageRequest.sort())
                .param("direction", pageRequest.direction()))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   "})
  @NullSource
  @DisplayName("POST /companies - should return 422 when name is null or blank")
  void create_withInvalidName_returnsUnprocessableEntity(String invalidName) throws Exception {

    User user = new User();
    user.setId(10L);
    user.setName("Api");
    user.setEmail("jwt-mvc@test.com");
    user.setPassword("ignored");
    user.setRole(UserRole.CONSUMER);
    user.setCompany(null);

    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    String token = jwtService.generateToken(user);

    String jsonContent =
        invalidName == null ? "{\"name\": null}" : "{\"name\": \"" + invalidName + "\"}";

    mockMvc
        .perform(
            post("/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(jsonContent))
        .andExpect(status().isUnprocessableEntity());
  }
}
