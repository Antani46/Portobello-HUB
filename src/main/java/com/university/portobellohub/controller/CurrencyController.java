package com.university.portobellohub.controller;

import com.university.portobellohub.dto.response.CurrencyConversionResponse;
import com.university.portobellohub.service.ExchangeRateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    private final ExchangeRateService exchangeRateService;

    public CurrencyController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/convert")
    public CurrencyConversionResponse convert(
            @RequestParam BigDecimal amount,
            @RequestParam(defaultValue = "EUR") String from,
            @RequestParam String to
    ) {
        return exchangeRateService.convert(amount, from, to);
    }
}
