package br.com.gabrielcaio.pdv.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_role")
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private UserRole name;

  public Role() {
  }

  public Role(String name) {
    try {
      this.name = UserRole.valueOf(name.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Role inválida: " + name);
    }
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name.toString();
  }

  public void setName(String name) {
    try {
      this.name = UserRole.valueOf(name.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Role inválida: " + name);
    }
  }
}
