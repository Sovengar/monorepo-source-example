package com.example.monorepo.shared;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Value Object compartido entre módulos.
 * Representa una cantidad monetaria con su divisa.
 */
public record Money(BigDecimal amount, Currency currency) {

    public static Money euros(double amount) {
        return new Money(BigDecimal.valueOf(amount), Currency.getInstance("EUR"));
    }

    public static Money dollars(double amount) {
        return new Money(BigDecimal.valueOf(amount), Currency.getInstance("USD"));
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
