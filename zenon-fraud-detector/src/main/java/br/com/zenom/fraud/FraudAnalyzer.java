package br.com.zenom.fraud;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FraudAnalyzer {

    private final List<Transaction> transactions;

    public FraudAnalyzer(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    private Stream<Transaction> getFrauds() {
        return this.transactions.stream().filter(Transaction::isFraud);
    }

    public long countFrauds() {
        return getFrauds().filter(Transaction::isFraud).count();
    }

    public List<Transaction> findHighestValueFrauds(int limit) {
        return getFrauds()
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .limit(limit)
                .toList();
    }

    public List<String> findTopSuspiciousClients(int limit) {
        return getFrauds()
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .map(t -> t.origin().name()).distinct().limit(limit).toList();
    }

    public BigDecimal totalAmountFrauds() {
        return getFrauds().map(transaction -> transaction.amount().value())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<TransactionType, List<Transaction>> getFraudsByType() {
        return getFrauds().collect(Collectors.groupingBy(Transaction::type));
    }

}
