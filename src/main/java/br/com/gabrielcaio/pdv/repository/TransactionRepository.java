package br.com.gabrielcaio.pdv.repository;

import br.com.gabrielcaio.pdv.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}