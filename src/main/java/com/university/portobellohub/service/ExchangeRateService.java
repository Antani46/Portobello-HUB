package com.university.portobellohub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.portobellohub.dto.response.CurrencyConversionResponse;
import com.university.portobellohub.dto.response.ItemPriceResponse;
import com.university.portobellohub.entity.Item;
import com.university.portobellohub.exception.BadRequestException;
import com.university.portobellohub.exception.ResourceNotFoundException;
import com.university.portobellohub.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ExchangeRateService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ItemRepository itemRepository;
    private final String baseUrl;
    private final String baseCurrency;

    public ExchangeRateService(
            ItemRepository itemRepository,
            @Value("${exchange-rate.base-url}") String baseUrl,
            @Value("${app.base-currency}") String baseCurrency
    ) {
        this.itemRepository = itemRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.baseCurrency = baseCurrency.toUpperCase();
    }

    public CurrencyConversionResponse convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }

        String from = fromCurrency.toUpperCase();
        String to = toCurrency.toUpperCase();
        BigDecimal rate = fetchExchangeRate(from, to);
        BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

        return new CurrencyConversionResponse(amount, from, to, convertedAmount, rate);
    }

    public ItemPriceResponse getItemPriceInCurrency(Long itemId, String targetCurrency) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));

        String target = targetCurrency.toUpperCase();
        BigDecimal rate = fetchExchangeRate(baseCurrency, target);
        BigDecimal convertedPrice = item.getPrice().multiply(rate).setScale(2, RoundingMode.HALF_UP);

        return new ItemPriceResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                baseCurrency,
                target,
                convertedPrice,
                rate
        );
    }

    private BigDecimal fetchExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }

        try {
            String responseBody = restTemplate.getForObject(baseUrl + fromCurrency, String.class);
            if (responseBody == null || responseBody.isBlank()) {
                throw new BadRequestException("Empty response from exchange rate API");
            }

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode rateNode = root.path("rates").path(toCurrency);
            if (rateNode.isMissingNode() || !rateNode.isNumber()) {
                throw new BadRequestException("Unsupported currency: " + toCurrency);
            }
            return rateNode.decimalValue();
        } catch (BadRequestException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new BadRequestException("Exchange rate API is not reachable: " + ex.getMessage());
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("Invalid response from exchange rate API");
        }
    }
}
