package br.com.gabrielcaio.pdv.repository;

import br.com.gabrielcaio.pdv.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

 Optional<User> findByEmail(String email);
}