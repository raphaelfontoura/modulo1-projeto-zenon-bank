package br.com.zenom.fraud;

public record Customer(
        String name,
        Currency oldBalance,
        Currency newBalance
) {
    public Customer {
        if (name.isBlank()) throw new IllegalArgumentException("name should not be empty");
    }
}
