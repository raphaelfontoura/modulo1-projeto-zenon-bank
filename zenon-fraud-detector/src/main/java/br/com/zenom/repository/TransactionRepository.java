package br.com.zenom.repository;

import br.com.zenom.fraud.Transaction;

import java.util.Optional;

public interface TransactionRepository {

    Optional<Transaction> findByOriginCustomerName(String name);

    void save(Transaction transaction);
}
