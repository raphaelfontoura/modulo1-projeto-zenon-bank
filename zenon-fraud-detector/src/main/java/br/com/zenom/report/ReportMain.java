package br.com.zenom.report;

public class ReportMain {

    static void main() {
        TransactionReport transactionReport = new TransactionReport("data/PS_20174392719_1491204439457_log.csv");
        TransactionReport.Statistics statistics = transactionReport.generateReport();

        IO.println("""
                Total de linhas: %d
                Total de fraudes: %d
                Valor total das transações: %.2f
                """.formatted(statistics.totalTransactions(), statistics.totalFrauds(), statistics.totalAmount()));

    }
}
