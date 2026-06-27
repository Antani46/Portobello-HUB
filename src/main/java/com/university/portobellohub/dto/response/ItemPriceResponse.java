package com.university.portobellohub.dto.response;

import java.math.BigDecimal;

public class ItemPriceResponse {

    private Long itemId;
    private String itemName;
    private BigDecimal priceInBaseCurrency;
    private String baseCurrency;
    private String targetCurrency;
    private BigDecimal convertedPrice;
    private BigDecimal exchangeRate;

    public ItemPriceResponse(
            Long itemId,
            String itemName,
            BigDecimal priceInBaseCurrency,
            String baseCurrency,
            String targetCurrency,
            BigDecimal convertedPrice,
            BigDecimal exchangeRate
    ) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.priceInBaseCurrency = priceInBaseCurrency;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.convertedPrice = convertedPrice;
        this.exchangeRate = exchangeRate;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getPriceInBaseCurrency() {
        return priceInBaseCurrency;
    }

    public void setPriceInBaseCurrency(BigDecimal priceInBaseCurrency) {
        this.priceInBaseCurrency = priceInBaseCurrency;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public BigDecimal getConvertedPrice() {
        return convertedPrice;
    }

    public void setConvertedPrice(BigDecimal convertedPrice) {
        this.convertedPrice = convertedPrice;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
