package br.com.gabrielcaio.pdv.controller;

import br.com.gabrielcaio.pdv.controller.request.AuthResponse;
import br.com.gabrielcaio.pdv.controller.request.LoginRequest;
import br.com.gabrielcaio.pdv.controller.request.RegisterRequest;
import br.com.gabrielcaio.pdv.security.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public AuthResponse login(@RequestBody LoginRequest request) {
    String token = authService.login(request.email(), request.password());
    return new AuthResponse(token);
  }
  @PostMapping("/register")
  public AuthResponse register(@RequestBody RegisterRequest request) {
    String token = authService.register(request.name(), request.email(), request.password(), request.role());
    return new AuthResponse(token);
  }

  @GetMapping("/test")
  public String test() {
    return "Hello, World!";
  }
}
