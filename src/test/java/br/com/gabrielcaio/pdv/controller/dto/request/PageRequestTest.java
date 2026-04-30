package br.com.gabrielcaio.pdv.controller.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PageRequestTest {

  @Test
  @DisplayName("Deve aplicar valores padrão quando os campos forem nulos")
  void shouldApplyDefaultValues() {

    var request = new PageRequest(null, null, null, null);

    assertEquals(0, request.page());
    assertEquals(10, request.size());
    assertEquals("asc", request.direction());
  }
}
