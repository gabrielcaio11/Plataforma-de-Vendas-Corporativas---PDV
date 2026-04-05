package br.com.gabrielcaio.pdv.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "companies")
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  // Relacionamento reverso (opcional)
  @OneToMany(mappedBy = "company")
  private List<User> users;

  @OneToMany(mappedBy = "company")
  private List<Product> products;
}
