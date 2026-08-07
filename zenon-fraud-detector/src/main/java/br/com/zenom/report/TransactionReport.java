package br.com.zenom.report;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class TransactionReport {

    private final Path filePath;
    private static final long HEADER_NUMBER = 1;

    public TransactionReport(String filePath) {
        Objects.requireNonNull(filePath);
        this.filePath = Path.of(filePath);
    }

    public record ReportTransaction(BigDecimal amount, boolean isFraud) {
    }

    public record Statistics (long totalTransactions, long totalFrauds, BigDecimal totalAmount) {
        private static Statistics STATISTICS_ZERO() {
            return new Statistics(0, 0, BigDecimal.ZERO);
        }

        private Statistics addTransaction(ReportTransaction reportTransaction) {
            return new Statistics(totalTransactions() + 1,
                    totalFrauds() + (reportTransaction.isFraud ? 1 : 0),
                    totalAmount().add(reportTransaction.amount)
            );
        }

        private Statistics add(Statistics other) {
            return new Statistics(
                    totalTransactions + other.totalTransactions,
                    totalFrauds + other.totalFrauds,
                    totalAmount.add(other.totalAmount));
        }
    }

    public Statistics generateReport() {
        try(Stream<String> lines = Files.lines(filePath)) {
            return lines.skip(HEADER_NUMBER)
                    .map(TransactionReport::getReportTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce(Statistics.STATISTICS_ZERO(), Statistics::addTransaction, Statistics::add);
        } catch (IOException ex) {
            throw new RuntimeException("Falha ao ler arquivo", ex);
        }
    }

    public static Optional<ReportTransaction> getReportTransaction(String line) {
        String[] values = line.split(",");

        try {
            BigDecimal amount = new BigDecimal(values[2]);
            boolean isFraud = values[9].equals("1");
            return Optional.of(new ReportTransaction(amount, isFraud));
        } catch (IllegalArgumentException ex) {
            System.err.printf("Erro: %s | %s: %s \r\n", line, ex.getClass(), ex.getMessage());
        }
        return Optional.empty();
    }

}
