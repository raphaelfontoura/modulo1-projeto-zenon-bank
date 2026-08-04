package br.com.zenom.ingestor;

import br.com.zenom.fraud.Customer;
import br.com.zenom.fraud.Transaction;
import br.com.zenom.fraud.TransactionType;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TransactionIngestor {

    public List<Transaction> ingestorFileTransactions(String fileName) throws IOException {
        Path path = Path.of(fileName);
        List<String> transactionLines = Files.readAllLines(path);
        return transactionLines.stream()
                .skip(1)
                .limit(1000)
                .map(TransactionIngestor::getTransaction)
                .toList();
    }

    private static Transaction getTransaction(String line) {
        String[] values = line.split(",");
        int step = Integer.parseInt(values[0]);
        TransactionType type = TransactionType.valueOf(values[1]);
        BigDecimal amount = new BigDecimal(values[2]);
        Customer orig= new Customer(values[3], new BigDecimal(values[4]), new BigDecimal(values[5]));
        Customer recipient = new Customer(values[6], new BigDecimal(values[7]), new BigDecimal(values[8]));
        boolean isFraud = values[9].equals("1");
        boolean isFlagged = values[10].equals("1");
        return new Transaction(step, type, amount, orig, recipient, isFraud, isFlagged);
    }
}
