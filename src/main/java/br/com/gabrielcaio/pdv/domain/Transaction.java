package br.com.gabrielcaio.pdv.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

  public Transaction() {}

  public Transaction(
      Long id,
      User user,
      Product product,
      BigDecimal priceAtPurchase,
      Integer quantity,
      LocalDateTime createdAt) {
    this.id = id;
    this.user = user;
    this.product = product;
    this.priceAtPurchase = priceAtPurchase;
    this.quantity = quantity;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public BigDecimal getPriceAtPurchase() {
    return priceAtPurchase;
  }

  public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
    this.priceAtPurchase = priceAtPurchase;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
