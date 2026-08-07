package br.com.zenom.report;

import br.com.zenom.ingestor.TransactionMap;

import java.math.BigDecimal;
import java.util.Optional;

public class ReportMain {

    static void main() {
        TransactionReport transactionReport = new TransactionReport("data/PS_20174392719_1491204439457_log.csv");

        long totalLines = transactionReport.loadTransactionsFile().skip(1).count();

        IO.println("Total de linhas: " + totalLines);

        long totalFrauds = transactionReport.loadTransactionsFile().skip(1).map(TransactionMap::getTransaction)
                .filter(t -> t.get().isFraud())
                .count();
        IO.println("Total de fraudes: " + totalFrauds);

        BigDecimal totalTransactions = transactionReport.loadTransactionsFile().skip(1)
                .map(TransactionMap::getTransaction)
                .filter(Optional::isPresent)
                .map(transaction -> transaction.get().amount().value())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        IO.println("Valor total transacionado: " + totalTransactions.toPlainString());

    }
}
