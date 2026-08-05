package br.com.zenom.repository;

import br.com.zenom.fraud.Transaction;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TransactionListRepositoryOptimizedImpl implements TransactionListRepository {

    private final Map<String, Transaction> dbTransactions;

    public TransactionListRepositoryOptimizedImpl(List<Transaction> transactions) {
        Objects.requireNonNull(transactions);

//        dbTransactions = new TreeMap<>();
//        transactions.forEach(transaction -> {
//            dbTransactions.putIfAbsent(transaction.origin().name(), transaction);
//        });

//        dbTransactions = transactions.stream().collect(Collectors.toMap(transaction ->
//                transaction.origin().name(),
//                transaction -> transaction
//        ));

        dbTransactions = transactions.stream().collect(Collectors.toMap(
                transaction -> transaction.origin().name(),
                Function.identity(),
                (first, _) -> first,
                TreeMap::new
        ));
    }

    @Override
    public Optional<Transaction> findByOriginCustomerName(String name) {
        return Optional.ofNullable(dbTransactions.get(name));
    }
}
