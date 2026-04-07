package br.com.gabrielcaio.pdv.controller;

import br.com.gabrielcaio.pdv.controller.dto.response.CompanyResponse;
import br.com.gabrielcaio.pdv.controller.dto.request.CreateCompanyRequest;
import br.com.gabrielcaio.pdv.service.CompanyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/companies")
public class CompanyController {

  private final CompanyService companyService;

  public CompanyController(CompanyService companyService) {
    this.companyService = companyService;
  }

  @PostMapping
  public CompanyResponse createCompany(@RequestBody CreateCompanyRequest request) {
    return companyService.createCompany(request.name());
  }
}
