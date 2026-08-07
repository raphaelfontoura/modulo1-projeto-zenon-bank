package br.com.zenom.ingestor;

import br.com.zenom.fraud.Currency;
import br.com.zenom.fraud.Customer;
import br.com.zenom.fraud.Transaction;
import br.com.zenom.fraud.TransactionType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionIngestor {

    private static final Logger log = Logger.getLogger(TransactionIngestor.class.getName());
    private static final long LIMIT = 100_000;

    public List<Transaction> ingestorFileTransactions(String fileName) {
        Path path = Path.of(fileName);
        try {
        List<String> transactionLines = Files.readAllLines(path);
        return transactionLines.stream()
                .skip(1)
                .limit(LIMIT)
                .map(TransactionMap::getTransaction)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Erro no ingestor.", ex);
            throw new RuntimeException("Falha ao ler arquivo", ex);
        }

    }

}
