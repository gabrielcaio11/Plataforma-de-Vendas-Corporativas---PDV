package br.com.gabrielcaio.pdv.domain;

import java.util.Objects;

/**
 * Value Object que representa um CPF limpo e validado.
 */
public record CPF(String value) {

  // O construtor compacto do Record serve para validação e normalização
  public CPF {
    Objects.requireNonNull(value, "O CPF não pode ser nulo.");

    // Remove pontos, traços ou espaços
    value = value.replaceAll("\\D", "");

    if (!isValidCPF(value)) {
      throw new IllegalArgumentException("CPF inválido: " + value);
    }
  }

  /**
   * Retorna o CPF formatado no padrão (###.###.###-##).
   */
  public String getFormatted() {
    return value.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
  }

  // Lógica interna de validação do algoritmo do CPF
  private static boolean isValidCPF(String cpf) {
    if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
      return false;
    }

    try {
      // Cálculo do primeiro dígito verificador
      int sum = 0;
      for (int i = 0; i < 9; i++) {
        sum += (cpf.charAt(i) - '0') * (10 - i);
      }
      int checkDigit1 = 11 - (sum % 11);
      if (checkDigit1 >= 10) checkDigit1 = 0;

      // Cálculo do segundo dígito verificador
      sum = 0;
      for (int i = 0; i < 10; i++) {
        sum += (cpf.charAt(i) - '0') * (11 - i);
      }
      int checkDigit2 = 11 - (sum % 11);
      if (checkDigit2 >= 10) checkDigit2 = 0;

      return (cpf.charAt(9) - '0' == checkDigit1) && (cpf.charAt(10) - '0' == checkDigit2);
    } catch (Exception e) {
      return false;
    }
  }

  // O Java Record já sobrescreve automaticamente os métodos toString(), equals() e hashCode() baseado no atributo 'value'
}
