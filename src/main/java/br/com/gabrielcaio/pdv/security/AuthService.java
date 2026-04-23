package br.com.gabrielcaio.pdv.security;

import br.com.gabrielcaio.pdv.controller.dto.request.LoginRequest;
import br.com.gabrielcaio.pdv.controller.dto.request.RegisterRequest;
import br.com.gabrielcaio.pdv.controller.exception.error.BusinessException;
import br.com.gabrielcaio.pdv.controller.exception.error.EntityExistsException;
import br.com.gabrielcaio.pdv.domain.Company;
import br.com.gabrielcaio.pdv.domain.User;
import br.com.gabrielcaio.pdv.domain.UserRole;
import br.com.gabrielcaio.pdv.repository.CompanyRepository;
import br.com.gabrielcaio.pdv.repository.UserRepository;
import java.util.ArrayList;
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

  public String login(LoginRequest request) {
    String email = request.email();
    String password = request.password();
    authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

    return jwtService.generateToken(user);
  }

  public String register(RegisterRequest request) {

    // Validar os campos do request
    validateRegisterRequest(request);

    // Criar o usuário a partir do request
    User user = createUserFromRequest(request);

    // Consistencia bidecional entre User e Company
    Company company = user.getCompany();
    if (company != null) {
      if (company.getUsers() == null) {
        company.setUsers(new ArrayList<>());
      }
      company.getUsers().add(user);
    }

    userRepository.save(user);

    return jwtService.generateToken(user);
  }

  private User createUserFromRequest(RegisterRequest request) {
    User user = new User();
    user.setName(request.name());
    user.setEmail(request.email());
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setRole(getUserRole(request));
    user.setCompany(getCompanyFromRequest(request));
    return user;
  }

  private Company getCompanyFromRequest(RegisterRequest request) {
    if (request.company_id() == null) {
      return null;
    }
    return companyRepository
        .findById(request.company_id())
        .orElseThrow(() -> new BusinessException("Empresa não encontrada"));
  }

  private void validateRegisterRequest(RegisterRequest request) {
    validateNameUser(request);
    validateEmailUser(request);
    validateCompanyOfLogin(request);
    validatePasswordUser(request);
  }

  private static void validateNameUser(RegisterRequest request) {
    // TODO: Melhorar validação de nome (ex: não permitir números, caracteres especiais, etc)
    if (request.name() == null || request.name().isBlank()) {
      throw new BusinessException("Name não pode ser null ou blank");
    }
  }

  private static void validatePasswordUser(RegisterRequest request) {
    // TODO: Melhorar validação de senha (ex: exigir caracteres especiais, números, etc)
    if (request.password() == null || request.password().isBlank()) {
      throw new BusinessException("Password não pode ser null ou blank");
    }
  }

  private void validateEmailUser(RegisterRequest request) {
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new EntityExistsException("Email indisponível");
    }
    // TODO: Melhorar validação de email (ex: regex para validar formato, etc)
  }

  private void validateCompanyOfLogin(RegisterRequest request) {
    UserRole role = getUserRole(request);

    // Verificar se o papel é COLLABORATOR e se a empresa_id é nula
    if (role == UserRole.COLLABORATOR && request.company_id() == null) {
      throw new BusinessException("Colaboradores devem pertencer a uma empresa");
    }
    // Verificar se o papel é CONSUMER e se a empresa_id não é nula
    if (role == UserRole.CONSUMER && request.company_id() != null) {
      throw new BusinessException("Consumidores não devem pertencer a uma empresa");
    }
  }

  private UserRole getUserRole(RegisterRequest request) {

    if (request.role() == null) {
      return UserRole.CONSUMER;
    }

    String roleName = request.role().name().toUpperCase();
    UserRole role;

    try {
      role = UserRole.valueOf(roleName);
    } catch (IllegalArgumentException e) {
      throw new BusinessException("Role inválida");
    }
    return role;
  }
}
