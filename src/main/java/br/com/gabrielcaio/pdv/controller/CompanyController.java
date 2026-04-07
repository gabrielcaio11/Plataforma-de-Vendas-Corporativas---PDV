package br.com.gabrielcaio.pdv.controller;

import br.com.gabrielcaio.pdv.controller.dto.request.CreateCompanyRequest;
import br.com.gabrielcaio.pdv.controller.dto.response.CompanyResponse;
import br.com.gabrielcaio.pdv.service.CompanyService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<List<CompanyResponse>> getAll() {
    List<CompanyResponse> responses = companyService.getAll();
    return ResponseEntity.ok(responses);
  }
}
