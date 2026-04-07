package br.com.gabrielcaio.pdv.security;

import br.com.gabrielcaio.pdv.domain.User;
import br.com.gabrielcaio.pdv.repository.UserRepository;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AuthenticationManager authManager;
  private final JwtService jwtService;
  private final UserRepository userRepository;

  public AuthService(AuthenticationManager authManager,
      JwtService jwtService,
      UserRepository userRepository) {
    this.authManager = authManager;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  public String login(String email, String password) {

    authManager.authenticate(
        new UsernamePasswordAuthenticationToken(email, password)
    );

    User user = userRepository.findByEmail(email)
        .orElseThrow();

    return jwtService.generateToken(user);
  }
}
