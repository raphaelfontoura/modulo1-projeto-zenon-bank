package br.com.zenom.ingestor;

import br.com.zenom.fraud.Currency;
import br.com.zenom.fraud.Customer;
import br.com.zenom.fraud.Transaction;
import br.com.zenom.fraud.TransactionType;

import java.util.Optional;

public class TransactionMap {
    public static Optional<Transaction> getTransaction(String line) {
        String[] values = line.split(",");

        try {
            int step = Integer.parseInt(values[0]);
            TransactionType type = TransactionType.valueOf(values[1]);
            Currency amount = new Currency(values[2]);
            Customer orig = new Customer(values[3], new Currency(values[4]), new Currency(values[5]));
            Customer recipient = new Customer(values[6], new Currency(values[7]), new Currency(values[8]));
            boolean isFraud = values[9].equals("1");
            boolean isFlagged = values[10].equals("1");
            return Optional.of(new Transaction(step, type, amount, orig, recipient, isFraud, isFlagged));
        } catch (IllegalArgumentException ex) {
            System.err.printf("Erro: %s | %s: %s \r\n", line, ex.getClass(), ex.getMessage());
        }
        return Optional.empty();
    }
}
