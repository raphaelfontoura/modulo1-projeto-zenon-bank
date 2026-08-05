package br.com.zenom.fraud;

import br.com.zenom.ingestor.TransactionIngestor;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FraudAnalyzer {

    static void main() {
        TransactionIngestor ingestor = new TransactionIngestor();
        List<Transaction> transactions = ingestor.ingestorFileTransactions("data/PS_20174392719_1491204439457_log.csv");

        var frauds = transactions.stream().filter(Transaction::isFraud).toList();
        long totalFraud = frauds.size();
        System.out.println("1. total de fraudes: " + totalFraud);

        Comparator<Transaction> compAmount = Comparator.comparing(transaction -> transaction.amount().value());

        List<Transaction> orderedFrauds = frauds.stream()
                        .sorted(compAmount.reversed()).toList();

        System.out.println("2. Top 3 Fraudes de Maior Valor:");
        orderedFrauds.stream().limit(3).forEach(transaction -> IO.println(transaction.amount().value().toPlainString()));

        List<String> greaterFrauds = orderedFrauds.stream().map(t -> t.origin().name()).distinct().limit(5).toList();
        System.out.println("3. Clientes suspeitos:");
        greaterFrauds.forEach(IO::println);

        BigDecimal totalAmounts = frauds.stream()
                .map(transaction -> transaction.amount().value())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("4. Prejuízo total: " + totalAmounts);

        Map<TransactionType, List<Transaction>> groupTypesFraud = frauds.stream().collect(Collectors.groupingBy(Transaction::type));

        System.out.println("5. Fraudes por Tipo:");
        groupTypesFraud.forEach((k, v) -> IO.println("- " + k.name() + ": " + v.size()));

    }

}
