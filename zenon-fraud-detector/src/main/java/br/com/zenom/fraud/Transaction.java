package br.com.zenom.fraud;

public record Transaction(
        int step,
        TransactionType type,
        Currency amount,
        Customer origin,
        Customer recipient,
        boolean isFraud,
        boolean isFlaggedFraud
        ) {
        public Transaction {
                if (step <= 0) throw new IllegalArgumentException("step should be positive: " + step);
        }
}
