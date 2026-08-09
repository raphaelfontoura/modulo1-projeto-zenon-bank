package br.com.zenom.ingestor;

import br.com.zenom.fraud.Transaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class EfficientTransactionIngestor {

    private static final Logger log = Logger.getLogger(EfficientTransactionIngestor.class.getName());
    private static final long LIMIT = 10_000;
    private static final long LINE_BATCH_SIZE = 2_500;

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
        List<Transaction> transactions = new CopyOnWriteArrayList<>();

        // ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()
        try (ExecutorService executor = Executors.newFixedThreadPool(10);
                Stream<String> lines = Files.lines(path)) {

            Iterator<String> iterator = lines
                    .skip(1)
//                    .limit(LIMIT)
                    .parallel()
                    .iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                TransactionMap.getTransaction(line).ifPresentOrElse(transactions::add, () -> {
                });
                if (transactions.size() >= LINE_BATCH_SIZE) {
                    var list = List.copyOf(transactions);
                    executor.submit(() -> consumer.accept(list));
                    transactions.clear();
                }
            }
            if (!transactions.isEmpty()) {
                var list = List.copyOf(transactions);
                executor.submit(() -> consumer.accept(list));
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Erro no ingestor.", ex);
            throw new RuntimeException("Falha ao ler arquivo", ex);
        }
    }

}
