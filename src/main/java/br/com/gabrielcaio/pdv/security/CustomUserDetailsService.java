package br.com.gabrielcaio.pdv.security;

import br.com.gabrielcaio.pdv.domain.User;
import br.com.gabrielcaio.pdv.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

    return org.springframework.security.core.userdetails.User
        .withUsername(user.getEmail())
        .password("{noop}" + user.getPassword())
        .roles(user.getRole().name())
        .build();
  }
}
