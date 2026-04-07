package br.com.gabrielcaio.pdv.security;

import br.com.gabrielcaio.pdv.controller.error.ForbiddenException;
import br.com.gabrielcaio.pdv.domain.Product;
import br.com.gabrielcaio.pdv.domain.User;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

  public void checkProductOwnership(User user, Product product) {

    if (user.getCompany() == null) {
      throw new ForbiddenException("Usuário não pertence a nenhuma empresa");
    }

    if (!product.getCompany().getId().equals(user.getCompany().getId())) {
      throw new ForbiddenException("Produto não pertence à empresa do usuário");
    }
  }
}
