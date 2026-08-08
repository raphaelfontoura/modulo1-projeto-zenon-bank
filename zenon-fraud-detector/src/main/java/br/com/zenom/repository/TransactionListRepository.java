package br.com.zenom.repository;

import br.com.zenom.fraud.Transaction;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TransactionListRepository implements TransactionRepository {

    private final List<Transaction> transactions;

    public TransactionListRepository(List<Transaction> transactions) {
        Objects.requireNonNull(transactions);
        this.transactions = transactions;
    }

    @Override
    public Optional<Transaction> findByOriginCustomerName(String name) {
        return transactions.stream()
                .filter(transaction -> transaction.origin().name().equals(name))
                .findFirst();
    }

    @Override
    public void save(Transaction transaction) {
        transactions.add(transaction);
    }
}
