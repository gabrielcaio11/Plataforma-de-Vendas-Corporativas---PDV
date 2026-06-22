package br.com.gabrielcaio.pdv.domain;

import java.util.Objects;
import java.util.UUID;

/** Value Object que representa um CPF limpo e validado. */
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
   * Método de fábrica que gera uma instância de CPF com um valor aleatório e válido baseado em um
   * UUID.
   */
  public static CPF random() {
    // 1. Gera um UUID aleatório e extrai apenas os números
    String apenasNumeros = UUID.randomUUID().toString().replaceAll("\\D", "");

    // 2. Garante que temos pelo menos 9 dígitos (preenche com zero se necessário)
    if (apenasNumeros.length() < 9) {
      apenasNumeros = String.format("%-9s", apenasNumeros).replace(' ', '0');
    }

    // 3. Pega os primeiros 9 dígitos para a base do CPF
    String baseCpf = apenasNumeros.substring(0, 9);

    // 4. Calcula os dois dígitos verificadores
    int d1 = calcularDigito(baseCpf, 10);
    int d2 = calcularDigito(baseCpf + d1, 11);

    // 5. Instancia e retorna o Record CPF
    return new CPF(baseCpf + d1 + d2);
  }

  /** Retorna o CPF formatado no padrão (###.###.###-##). */
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

  // Método auxiliar privado para o cálculo dos dígitos verificadores
  private static int calcularDigito(String base, int pesoInicial) {
    int soma = 0;
    int peso = pesoInicial;
    for (int i = 0; i < base.length(); i++) {
      soma += (base.charAt(i) - '0') * peso;
      peso--;
    }
    int resto = soma % 11;
    return (resto < 2) ? 0 : (11 - resto);
  }
}
