package com.oloh.oloh.model.entities;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;


/**
 * Created by micka on 10-Aug-17.
 */

public class Money {

    private static final Currency INR = Currency.getInstance(new Locale("fr",
            "fr"));
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

    private BigDecimal amount;
    private Currency currency;

    Money(BigDecimal amount, Currency currency) {
        this(amount, currency, DEFAULT_ROUNDING);
    }

    Money(BigDecimal amount, Currency currency, RoundingMode rounding) {
        this.amount = amount;
        this.currency = currency;

        this.amount = amount.setScale(currency.getDefaultFractionDigits(),
                rounding);
    }

    public static Money euro(BigDecimal amount) {
        return new Money(amount, INR);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return getCurrency().getSymbol() + " " + getAmount();
    }

    public String toStringForStripe() {
        return String.valueOf(getAmount().multiply(new BigDecimal(100)).intValueExact());
    }

    public String toString(Locale locale) {
        return getCurrency().getSymbol(locale) + " " + getAmount();
    }
}
