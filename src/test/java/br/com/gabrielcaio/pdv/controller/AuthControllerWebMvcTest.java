package br.com.gabrielcaio.pdv.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.gabrielcaio.pdv.controller.dto.request.LoginRequest;
import br.com.gabrielcaio.pdv.controller.dto.request.RegisterRequest;
import br.com.gabrielcaio.pdv.controller.dto.request.UserRoleRequest;
import br.com.gabrielcaio.pdv.security.AuthService;
import br.com.gabrielcaio.pdv.security.CustomAuthenticationEntryPoint;
import br.com.gabrielcaio.pdv.security.CustomUserDetailsService;
import br.com.gabrielcaio.pdv.security.JwtAuthenticationFilter;
import br.com.gabrielcaio.pdv.security.JwtService;
import br.com.gabrielcaio.pdv.security.SecurityConfig;
import br.com.gabrielcaio.pdv.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc
@Import({
  SecurityConfig.class,
  JwtAuthenticationFilter.class,
  CustomAuthenticationEntryPoint.class,
  JwtService.class,
  CustomUserDetailsService.class
})
class AuthControllerWebMvcTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private AuthService authService;

  @MockitoBean private UserRepository userRepository;

  @Test
  void login_returnsToken() throws Exception {
    when(authService.login(any(LoginRequest.class))).thenReturn("token-jwt");

    LoginRequest body = new LoginRequest("user@test.com", "senha1234");

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("token-jwt"));
  }

  @Test
  void register_returnsCreatedAndToken() throws Exception {
    when(authService.register(any(RegisterRequest.class))).thenReturn("token-novo");

    RegisterRequest body =
        new RegisterRequest(
            "Novo", "novo@test.com", "senha1234", new UserRoleRequest("CONSUMER"), null);

    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.token").value("token-novo"));
  }
}
