package br.com.gabrielcaio.pdv.domain;

public enum UserRole {
  CONSUMER("CONSUMER"),
  COLLABORATOR("COLLABORATOR");

  private final String role;

  UserRole(String role) {
    this.role = role;
  }

  public String getRole() {
    return role;
  }
}
