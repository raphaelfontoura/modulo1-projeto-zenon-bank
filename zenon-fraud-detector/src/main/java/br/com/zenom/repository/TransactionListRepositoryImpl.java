package br.com.zenom.repository;

import br.com.zenom.fraud.Transaction;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TransactionListRepositoryImpl implements TransactionRepository {

    private final List<Transaction> transactions;

    public TransactionListRepositoryImpl(List<Transaction> transactions) {
        Objects.requireNonNull(transactions);
        this.transactions = transactions;
    }

    @Override
    public Optional<Transaction> findByOriginCustomerName(String name) {
        return transactions.stream()
                .filter(transaction -> transaction.origin().name().equals(name))
                .findFirst();
    }
}
