package br.com.zenom.ingestor;

import br.com.zenom.fraud.Transaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EfficientTransactionIngestor {

    private static final Logger log = Logger.getLogger(EfficientTransactionIngestor.class.getName());
    private static final long LIMIT = 10_000;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public void readAsStream(String fileName, Consumer<Transaction> consumer) {
        Path path = Path.of(fileName);
        try (Stream<String> lines = Files.lines(path)) {

            lines
                    .skip(1)
                    .limit(LIMIT)
                    .map(TransactionMap::getTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(consumer);


        } catch (IOException ex) {
            log.log(Level.SEVERE, "Erro no ingestor.", ex);
            throw new RuntimeException("Falha ao ler arquivo", ex);
        }

    }

    public void readBatch(String fileName, Consumer<List<Transaction>> consumer) {
        Path path = Path.of(fileName);
        int batchSize = 1000;
        List<Transaction> transactions = new CopyOnWriteArrayList<>();
        try (Stream<String> lines = Files.lines(path)) {

            lines
                    .skip(1)
                    .limit(LIMIT)
                    .parallel()
                    .map(TransactionMap::getTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(transaction -> {
                        transactions.add(transaction);
                        if (transactions.size() == batchSize) {
                            consumer.accept(transactions);
                            transactions.clear();
                        }
                    });
            if (!transactions.isEmpty()) {
                consumer.accept(transactions);
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Erro no ingestor.", ex);
            throw new RuntimeException("Falha ao ler arquivo", ex);
        }
    }

}
