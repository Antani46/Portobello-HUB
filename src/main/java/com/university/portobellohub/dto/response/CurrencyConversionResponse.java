package com.university.portobellohub.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CurrencyConversionResponse {

    private BigDecimal amount;
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal convertedAmount;
    private BigDecimal exchangeRate;
    private LocalDateTime retrievedAt;

    public CurrencyConversionResponse(
            BigDecimal amount,
            String fromCurrency,
            String toCurrency,
            BigDecimal convertedAmount,
            BigDecimal exchangeRate
    ) {
        this.amount = amount;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.convertedAmount = convertedAmount;
        this.exchangeRate = exchangeRate;
        this.retrievedAt = LocalDateTime.now();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public LocalDateTime getRetrievedAt() {
        return retrievedAt;
    }

    public void setRetrievedAt(LocalDateTime retrievedAt) {
        this.retrievedAt = retrievedAt;
    }
}
