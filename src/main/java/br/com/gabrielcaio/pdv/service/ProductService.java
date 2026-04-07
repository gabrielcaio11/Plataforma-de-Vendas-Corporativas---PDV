package br.com.gabrielcaio.pdv.service;

import br.com.gabrielcaio.pdv.controller.dto.response.ProductDetailsResponse;
import br.com.gabrielcaio.pdv.controller.dto.response.ProductResponse;
import br.com.gabrielcaio.pdv.controller.error.ForbiddenException;
import br.com.gabrielcaio.pdv.controller.dto.request.ProductRequest;
import br.com.gabrielcaio.pdv.domain.Product;
import br.com.gabrielcaio.pdv.domain.User;
import br.com.gabrielcaio.pdv.repository.CompanyRepository;
import br.com.gabrielcaio.pdv.repository.ProductRepository;
import br.com.gabrielcaio.pdv.repository.UserRepository;
import br.com.gabrielcaio.pdv.security.AuthorizationService;
import br.com.gabrielcaio.pdv.security.SecurityUtils;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final AuthorizationService authorizationService;
  private final CompanyRepository companyRepository;

  public ProductService(ProductRepository productRepository,
      UserRepository userRepository,
      AuthorizationService authorizationService,
      CompanyRepository companyRepository) {
    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.authorizationService = authorizationService;
    this.companyRepository = companyRepository;
  }

  public Product update(Long productId, ProductRequest request) {

    // 1. Usuário autenticado
    String email = SecurityUtils.getLoggedUserEmail();
    User user = userRepository.findByEmail(email)
        .orElseThrow();

    // 2. Produto do banco
    Product product = productRepository.findById(productId)
        .orElseThrow();

    // 3. AUTORIZAÇÃO (ponto chave)
    authorizationService.checkProductOwnership(user, product);

    // 4. Atualização
    product.setName(request.name());
    product.setPrice(request.price());

    return productRepository.save(product);
  }

  public Product create(ProductRequest request) {

    String email = SecurityUtils.getLoggedUserEmail();
    User user = userRepository.findByEmail(email)
        .orElseThrow();

    if (user.getCompany() == null) {
      throw new ForbiddenException("Apenas colaboradores podem criar produtos");
    }

    if(!Objects.equals(user.getCompany().getId(), request.company_id())) {
      throw new ForbiddenException("Você não pode criar produtos para outra empresa");
    }

    Product product = new Product();
    product.setName(request.name());
    product.setPrice(request.price());
    product.setStock(request.stock());
    product.setCompany(user.getCompany());
    user.getCompany().getProducts().add(product);
    companyRepository.save(user.getCompany());

    return productRepository.save(product);
  }

  public List<ProductDetailsResponse> getAll() {
    return productRepository.findAll().stream()
        .map(
            p -> new ProductDetailsResponse(
              p.getId(),
              p.getName(),
              p.getPrice()
            )
        )
        .toList();
  }

  public ProductDetailsResponse getById(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow();
    return new ProductDetailsResponse(
        product.getId(),
        product.getName(),
        product.getPrice()
    );
  }
}
