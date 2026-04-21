package br.com.gabrielcaio.pdv.controller;

import br.com.gabrielcaio.pdv.controller.dto.request.PageRequestDTO;
import br.com.gabrielcaio.pdv.controller.dto.request.TransactionRequest;
import br.com.gabrielcaio.pdv.controller.dto.response.TransactionResponse;
import br.com.gabrielcaio.pdv.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/transactions")
public class TransactionController {

  private final TransactionService transactionService;

  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @PostMapping
  public ResponseEntity<TransactionResponse> create(@RequestBody TransactionRequest request) {
    TransactionResponse response = transactionService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TransactionResponse> getById(@PathVariable Long id) {
    TransactionResponse response = transactionService.getById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<Page<TransactionResponse>> getAll(PageRequestDTO pageRequestDTO) {
    Page<TransactionResponse> responses = transactionService.getAll(pageRequestDTO);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/me")
  public ResponseEntity<Page<TransactionResponse>> getAllMe(PageRequestDTO pageRequestDTO) {
    Page<TransactionResponse> responses = transactionService.getAllMe(pageRequestDTO);
    return ResponseEntity.ok(responses);
  }
}
