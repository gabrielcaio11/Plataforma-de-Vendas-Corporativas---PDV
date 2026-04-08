package br.com.gabrielcaio.pdv.controller;

import br.com.gabrielcaio.pdv.controller.dto.request.CreateCompanyRequest;
import br.com.gabrielcaio.pdv.controller.dto.request.PageRequestDTO;
import br.com.gabrielcaio.pdv.controller.dto.response.CompanyResponse;
import br.com.gabrielcaio.pdv.controller.dto.response.CompanyWithEmployeeResponse;
import br.com.gabrielcaio.pdv.controller.dto.response.CompanyWithProductsResponse;
import br.com.gabrielcaio.pdv.service.CompanyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/companies")
public class CompanyController {

  private final CompanyService companyService;

  public CompanyController(CompanyService companyService) {
    this.companyService = companyService;
  }

  @PostMapping
  public ResponseEntity<CompanyResponse> create(@RequestBody CreateCompanyRequest request) {
    CompanyResponse response = companyService.create(request.name());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CompanyResponse> getById(@PathVariable Long id) {
    CompanyResponse response = companyService.getById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<Page<CompanyResponse>> getAll(
      PageRequestDTO request
  ) {
    Page<CompanyResponse> responses = companyService.getAll(request);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/{companyId}/employees")
  public ResponseEntity<List<CompanyWithEmployeeResponse>> getEmployeesByCompanyId(
      @PathVariable Long companyId) {
    List<CompanyWithEmployeeResponse> responses = companyService.getEmployeesByCompanyId(companyId);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/{companyId}/products")
  public ResponseEntity<List<CompanyWithProductsResponse>> getProductsByCompanyId(
      @PathVariable Long companyId) {
    List<CompanyWithProductsResponse> responses = companyService.getProductsByCompanyId(companyId);
    return ResponseEntity.ok(responses);
  }
}