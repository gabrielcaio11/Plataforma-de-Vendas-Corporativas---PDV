package br.com.gabrielcaio.pdv.repository;

import br.com.gabrielcaio.pdv.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {}
