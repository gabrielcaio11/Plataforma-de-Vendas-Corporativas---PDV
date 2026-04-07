package br.com.gabrielcaio.pdv.controller.dto.request;

import br.com.gabrielcaio.pdv.domain.UserRole;

public record RegisterRequest(String name, String email, String password, UserRole role,
                              Long company_id) {

}
