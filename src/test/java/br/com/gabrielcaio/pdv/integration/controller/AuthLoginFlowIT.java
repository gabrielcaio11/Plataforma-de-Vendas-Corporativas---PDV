package br.com.gabrielcaio.pdv.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.gabrielcaio.pdv.controller.dto.request.LoginRequest;
import br.com.gabrielcaio.pdv.controller.dto.request.RegisterRequest;
import br.com.gabrielcaio.pdv.controller.dto.request.UserRoleRequest;
import br.com.gabrielcaio.pdv.controller.dto.response.AuthResponse;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class AuthLoginFlowIT {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @DynamicPropertySource
  static void registerDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @LocalServerPort private int port;

  private RestTemplate restTemplate;

  @BeforeEach
  void configureRestTemplate() {
    restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(
        new ResponseErrorHandler() {
          @Override
          public boolean hasError(ClientHttpResponse response) {
            return false;
          }

          @Override
          public void handleError(URI url, HttpMethod method, ClientHttpResponse response)
              throws IOException {}
        });
  }

  private String url(String pathAndQuery) {
    return "http://127.0.0.1:" + port + pathAndQuery;
  }

  @Test
  @DisplayName("Accessing protected route without token should return 401 Unauthorized")
  void companies_withoutToken_returnsUnauthorized() {
    ResponseEntity<String> response =
        restTemplate.getForEntity(url("/companies?page=0&size=10&sort=name"), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("Registering and logging in should return a token that can access protected routes")
  void login_thenBearerAccessesProtectedRoute() {

    String email = "flow-" + UUID.randomUUID() + "@integration.test";
    String password = "senha1234";

    RegisterRequest registerRequest =
        new RegisterRequest("09300499009","Fluxo Login", email, password, new UserRoleRequest("CONSUMER"), null);

    HttpHeaders json = new HttpHeaders();
    json.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<AuthResponse> registered =
        restTemplate.postForEntity(
            url("/auth/register"), new HttpEntity<>(registerRequest, json), AuthResponse.class);

    assertThat(registered.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    LoginRequest loginRequest = new LoginRequest(email, password);
    ResponseEntity<AuthResponse> loggedIn =
        restTemplate.postForEntity(
            url("/auth/login"), new HttpEntity<>(loginRequest, json), AuthResponse.class);

    assertThat(loggedIn.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(loggedIn.getBody()).isNotNull();
    String token = loggedIn.getBody().token();
    assertThat(token).isNotBlank();

    HttpHeaders auth = new HttpHeaders();
    auth.setBearerAuth(token);

    ResponseEntity<String> companies =
        restTemplate.exchange(
            url("/companies?page=0&size=10&sort=name"),
            HttpMethod.GET,
            new HttpEntity<>(auth),
            String.class);

    assertThat(companies.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @DisplayName("Logging in with invalid credentials should return 401 Unauthorized")
  void login_withInvalidCredentials_returnsUnauthorized() {
    LoginRequest loginRequest =
        new LoginRequest("nonexistent-" + UUID.randomUUID() + "@integration.test", "wrongpassword");

    HttpHeaders json = new HttpHeaders();
    json.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<AuthResponse> response =
        restTemplate.postForEntity(
            url("/auth/login"), new HttpEntity<>(loginRequest, json), AuthResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "invalid.token.here",
        "abc",
        "Bearer",
        "",
        "eyJhbGciOiJIUzI1NiJ9..",
      })
  void protectedRoute_withInvalidTokens_returnsUnauthorized(String token) {

    ResponseEntity<String> response =
        restTemplate.exchange(
            url("/companies?page=0&size=10&sort=name"),
            HttpMethod.GET,
            bearer(token),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  @DisplayName("Should return 401 when no token is provided")
  void protectedRoute_withoutToken_returnsUnauthorized() {

    ResponseEntity<String> response =
        restTemplate.exchange(
            url("/companies?page=0&size=10&sort=name"), HttpMethod.GET, null, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  private HttpEntity<Void> bearer(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    return new HttpEntity<>(headers);
  }
}
