package br.com.gabrielcaio.pdv.controller;

import br.com.gabrielcaio.pdv.controller.dto.request.TransactionRequest;
import br.com.gabrielcaio.pdv.controller.dto.response.TransactionResponse;
import br.com.gabrielcaio.pdv.service.TransactionService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

  private final TransactionService transactionService;

  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @PostMapping
  public TransactionResponse create(@RequestBody TransactionRequest request) {
    return transactionService.create(request);
  }

  @GetMapping("/{id}")
  public TransactionResponse getById(@PathVariable Long id) {
    return transactionService.getById(id);
  }

  @GetMapping
  public List<TransactionResponse> getAll() {
    return transactionService.getAll();
  }
}