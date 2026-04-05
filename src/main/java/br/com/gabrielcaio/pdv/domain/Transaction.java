package br.com.gabrielcaio.pdv.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Quem comprou
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  // Produto comprado
  @ManyToOne(optional = false)
  @JoinColumn(name = "product_id")
  private Product product;

  // Snapshot do preço no momento da compra
  @Column(nullable = false)
  private BigDecimal priceAtPurchase;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private LocalDateTime createdAt;
}
