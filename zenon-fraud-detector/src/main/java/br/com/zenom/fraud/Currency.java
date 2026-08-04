package br.com.zenom.fraud;

import java.math.BigDecimal;

public record Currency(BigDecimal amount) {
    public Currency(String amount) {
        this(new BigDecimal(amount));
        if (this.amount.signum() < 0)
            throw new IllegalArgumentException("amount should be positive: " + amount);
    }
}
