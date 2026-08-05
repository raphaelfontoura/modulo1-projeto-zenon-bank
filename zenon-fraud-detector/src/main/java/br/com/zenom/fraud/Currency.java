package br.com.zenom.fraud;

import java.math.BigDecimal;
import java.util.Objects;

public record Currency(BigDecimal value) implements Comparable<Currency>  {
    public Currency {
        if (Objects.isNull(value)) throw new IllegalArgumentException("value cannot be null");
        if (value.signum() < 0)
            throw new IllegalArgumentException("amount should be positive: " + value);
    }
    public Currency(String amount) {
        this(new BigDecimal(amount));
    }
    public BigDecimal toBigDecimal() {
        return value;
    }

    @Override
    public int compareTo(Currency o) {
        return this.value.compareTo(o.value);
    }
}
