package br.com.gabrielcaio.pdv.security;

import br.com.gabrielcaio.pdv.controller.error.EntityExistsException;
import br.com.gabrielcaio.pdv.domain.User;
import br.com.gabrielcaio.pdv.domain.UserRole;
import br.com.gabrielcaio.pdv.repository.UserRepository;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AuthenticationManager authManager;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthService(AuthenticationManager authManager,
      JwtService jwtService,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    this.authManager = authManager;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public String login(String email, String password) {

    authManager.authenticate(
        new UsernamePasswordAuthenticationToken(email, password)
    );

    User user = userRepository.findByEmail(email)
        .orElseThrow();

    return jwtService.generateToken(user);
  }

  public String register(String name, String email, String password) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new EntityExistsException("Email já registrado");
    }

    User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    user.setRole(UserRole.CONSUMER);

    userRepository.save(user);

    return jwtService.generateToken(user);
  }
}
