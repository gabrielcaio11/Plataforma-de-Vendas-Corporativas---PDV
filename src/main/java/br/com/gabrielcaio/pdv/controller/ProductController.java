package br.com.gabrielcaio.pdv.controller;

import br.com.gabrielcaio.pdv.controller.dto.request.ProductRequest;
import br.com.gabrielcaio.pdv.controller.dto.response.ProductDetailsResponse;
import br.com.gabrielcaio.pdv.controller.dto.response.ProductResponse;
import br.com.gabrielcaio.pdv.domain.Product;
import br.com.gabrielcaio.pdv.service.ProductService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  @PreAuthorize("hasRole('COLLABORATOR')")
  public ProductResponse create(@RequestBody ProductRequest request) {
    Product entity = productService.create(request);
    return new ProductResponse(entity.getName(), entity.getPrice());
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('COLLABORATOR')")
  public ProductResponse update(
      @PathVariable Long id,
      @RequestBody ProductRequest request
  ) {
    Product entity = productService.update(id, request);
    return new ProductResponse(entity.getName(), entity.getPrice());
  }

  @GetMapping
  public List<ProductDetailsResponse> getAll() {
    return productService.getAll();
  }

  @GetMapping("/{id}")
  public ProductDetailsResponse getById(@PathVariable Long id) {
    return productService.getById(id);
  }
}


