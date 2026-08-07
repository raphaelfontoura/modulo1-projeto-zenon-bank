package br.com.zenom.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class TransactionReport {

    private final Path filePath;

    public TransactionReport(String filePath) {
        this.filePath = Path.of(filePath);
    }

    public Stream<String> loadTransactionsFile() {
        try {
           return Files.lines(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Falha ao ler arquivo", ex);
        }
    }
}
