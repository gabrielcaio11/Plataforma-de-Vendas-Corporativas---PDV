package br.com.gabrielcaio.pdv.security;

import java.util.Objects;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

  public static String getLoggedUserEmail() {
    Object principal =
        Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication())
            .getPrincipal();

    if (principal instanceof UserDetails userDetails) {
      return userDetails.getUsername();
    }

    throw new RuntimeException("Usuário não autenticado");
  }
}
