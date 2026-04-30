package br.com.gabrielcaio.pdv.controller;

import br.com.gabrielcaio.pdv.controller.dto.request.CreateProductRequest;
import br.com.gabrielcaio.pdv.controller.dto.request.PageRequestDTO;
import br.com.gabrielcaio.pdv.controller.dto.request.ProductRequest;
import br.com.gabrielcaio.pdv.controller.dto.response.ProductDetailsResponse;
import br.com.gabrielcaio.pdv.controller.dto.response.ProductResponse;
import br.com.gabrielcaio.pdv.domain.Product;
import br.com.gabrielcaio.pdv.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  @PreAuthorize("hasRole('COLLABORATOR')")
  public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
    Product entity = productService.create(request);
    ProductResponse response = new ProductResponse(entity.getName(), entity.getPrice());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('COLLABORATOR')")
  public ResponseEntity<ProductResponse> update(
      @PathVariable Long id, @Valid @RequestBody ProductRequest request) {
    Product entity = productService.update(id, request);
    ProductResponse response = new ProductResponse(entity.getName(), entity.getPrice());
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<Page<ProductDetailsResponse>> getAll(@Valid PageRequestDTO request) {
    Page<ProductDetailsResponse> responses = productService.getAll(request);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductDetailsResponse> getById(@PathVariable Long id) {
    ProductDetailsResponse response = productService.getById(id);
    return ResponseEntity.ok(response);
  }
}
