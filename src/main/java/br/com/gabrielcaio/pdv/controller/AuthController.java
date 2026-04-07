package br.com.gabrielcaio.pdv.controller;

import br.com.gabrielcaio.pdv.controller.request.LoginRequest;
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
  public String login(@RequestBody LoginRequest request) {
    return authService.login(request.email(), request.password());
  }
}
