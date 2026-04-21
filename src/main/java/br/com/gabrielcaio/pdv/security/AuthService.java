package br.com.gabrielcaio.pdv.security;

import br.com.gabrielcaio.pdv.controller.dto.request.RegisterRequest;
import br.com.gabrielcaio.pdv.controller.error.BusinessException;
import br.com.gabrielcaio.pdv.controller.error.EntityExistsException;
import br.com.gabrielcaio.pdv.domain.Company;
import br.com.gabrielcaio.pdv.domain.User;
import br.com.gabrielcaio.pdv.domain.UserRole;
import br.com.gabrielcaio.pdv.repository.CompanyRepository;
import br.com.gabrielcaio.pdv.repository.UserRepository;
import java.util.Optional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AuthenticationManager authManager;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CompanyRepository companyRepository;

  public AuthService(
      AuthenticationManager authManager,
      JwtService jwtService,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      CompanyRepository companyRepository) {
    this.authManager = authManager;
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.companyRepository = companyRepository;
  }

  public String login(String email, String password) {

    authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

    User user = userRepository.findByEmail(email).orElseThrow();

    return jwtService.generateToken(user);
  }

  public String register(RegisterRequest request) {
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new EntityExistsException("Email já registrado");
    }

    User user = new User();
    user.setName(request.name());
    user.setEmail(request.email());
    UserRole role = request.role() != null ? request.role() : UserRole.CONSUMER;
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setRole(role);

    if (role == UserRole.COLLABORATOR) {
      if (request.company_id() == null) {
        throw new BusinessException("Colaboradores devem pertencer a uma empresa");
      } else {
        Optional<Company> company = companyRepository.findById(request.company_id());
        if (company.isEmpty()) {
          throw new BusinessException("Empresa não encontrada");
        } else {
          user.setCompany(company.get());
        }
      }
    }

    userRepository.save(user);

    return jwtService.generateToken(user);
  }
}
