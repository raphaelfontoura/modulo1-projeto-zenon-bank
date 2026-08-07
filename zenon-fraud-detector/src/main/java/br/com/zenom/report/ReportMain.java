package br.com.zenom.report;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;

public class ReportMain {

    static void main(String[] args) {
        TransactionReport transactionReport = new TransactionReport("data/PS_20174392719_1491204439457_log.csv");
        TransactionReport.Statistics statistics = transactionReport.generateReport();

        String language = (args.length > 0 ? args[0]: "pt");
        Locale locale = Locale.of(language);
//        Locale locale = Locale.US;
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        currencyFormat.setCurrency(Currency.getInstance("USD"));
        NumberFormat integerFormat = NumberFormat.getIntegerInstance(locale);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("reportmessages", locale);

        IO.println("""
                %s: %s
                %s: %s
                %s: %s
                """.formatted(
                        resourceBundle.getString("label.total.transactions"), integerFormat.format(statistics.totalTransactions()),
                        resourceBundle.getString("label.total.frauds"),integerFormat.format(statistics.totalFrauds()),
                        resourceBundle.getString("label.total.amount"), currencyFormat.format(statistics.totalAmount())
                )
        );

    }
}
