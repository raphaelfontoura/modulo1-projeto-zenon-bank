package br.com.zenom.report;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class ReportMain {

    static void main() {
        TransactionReport transactionReport = new TransactionReport("data/PS_20174392719_1491204439457_log.csv");
        TransactionReport.Statistics statistics = transactionReport.generateReport();

        Locale locale = Locale.of("pt", "BR");
//        Locale locale = Locale.US;
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("reportmessages", locale);

        IO.println("""
                %s: %s
                %s: %s
                %s: %s
                """.formatted(
                        resourceBundle.getString("total.lines"), numberFormat.format(statistics.totalTransactions()),
                        resourceBundle.getString("total.frauds"),numberFormat.format(statistics.totalFrauds()),
                        resourceBundle.getString("total.amount"), numberFormat.format(statistics.totalAmount())
                )
        );

    }
}
