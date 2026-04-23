package br.com.gabrielcaio.pdv.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.gabrielcaio.pdv.controller.dto.response.CompanyResponse;
import br.com.gabrielcaio.pdv.domain.User;
import br.com.gabrielcaio.pdv.domain.UserRole;
import br.com.gabrielcaio.pdv.repository.UserRepository;
import br.com.gabrielcaio.pdv.security.CustomAuthenticationEntryPoint;
import br.com.gabrielcaio.pdv.security.CustomUserDetailsService;
import br.com.gabrielcaio.pdv.security.JwtAuthenticationFilter;
import br.com.gabrielcaio.pdv.security.JwtService;
import br.com.gabrielcaio.pdv.security.SecurityConfig;
import br.com.gabrielcaio.pdv.service.CompanyService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
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

  @Test
  void getById_withoutBearer_returnsUnauthorized() throws Exception {
    mockMvc
        .perform(get("/companies/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
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
}
