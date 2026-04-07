package br.com.gabrielcaio.pdv.service;

import br.com.gabrielcaio.pdv.controller.dto.request.TransactionRequest;
import br.com.gabrielcaio.pdv.controller.dto.response.TransactionResponse;
import br.com.gabrielcaio.pdv.controller.error.ResourceNotFoundException;
import br.com.gabrielcaio.pdv.domain.Product;
import br.com.gabrielcaio.pdv.domain.Transaction;
import br.com.gabrielcaio.pdv.domain.User;
import br.com.gabrielcaio.pdv.repository.ProductRepository;
import br.com.gabrielcaio.pdv.repository.TransactionRepository;
import br.com.gabrielcaio.pdv.repository.UserRepository;
import br.com.gabrielcaio.pdv.security.SecurityUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final TransactionRepository transactionRepository;

  public TransactionService(UserRepository userRepository, ProductRepository productRepository,
      TransactionRepository transactionRepository) {
    this.userRepository = userRepository;
    this.productRepository = productRepository;
    this.transactionRepository = transactionRepository;
  }

  public TransactionResponse create(TransactionRequest request) {

    // 1. Usuário autenticado
    String email = SecurityUtils.getLoggedUserEmail();
    User user = userRepository.findByEmail(email)
        .orElseThrow();

    // 2. Produto do banco
    Product product = productRepository.findById(request.productId())
        .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

    // 3. Criar transação
    Transaction transaction = new Transaction();
    transaction.setUser(user);
    transaction.setProduct(product);
    transaction.setQuantity(request.quantity());
    transaction.setPriceAtPurchase(product.getPrice());
    transaction.setCreatedAt(LocalDateTime.now());

    // 4. Salvar transação
    Transaction savedTransaction = transactionRepository.save(transaction);

    // 5. Retornar resposta
    BigDecimal totalPrice = savedTransaction.getPriceAtPurchase()
        .multiply(new BigDecimal(savedTransaction.getQuantity()));
    return new TransactionResponse(
        savedTransaction.getId(),
        savedTransaction.getProduct().getName(),
        savedTransaction.getQuantity(),
        totalPrice
    );
  }

  public TransactionResponse getById(Long id) {
    Transaction transaction = transactionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));
    return new TransactionResponse(
        transaction.getId(),
        transaction.getProduct().getName(),
        transaction.getQuantity(),
        transaction.getPriceAtPurchase().multiply(new BigDecimal(transaction.getQuantity()))
    );
  }

  public List<TransactionResponse> getAll() {

    List<Transaction> transactions = transactionRepository.findAll();
    return transactions.stream()
        .map(t -> new TransactionResponse(
            t.getId(),
            t.getProduct().getName(),
            t.getQuantity(),
            t.getPriceAtPurchase().multiply(new BigDecimal(t.getQuantity()))
        ))
        .toList();
  }
}
