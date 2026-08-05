package br.com.zenom.repository;

import br.com.zenom.fraud.Transaction;

import java.util.Optional;

public interface TransactionListRepository {

    Optional<Transaction> findByOriginCustomerName(String name);
}
