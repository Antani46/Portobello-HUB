package com.university.portobellohub.controller;

import com.university.portobellohub.dto.request.ClothingItemCreateRequest;
import com.university.portobellohub.dto.request.ElectronicItemCreateRequest;
import com.university.portobellohub.dto.request.ItemReviewActionRequest;
import com.university.portobellohub.dto.request.ItemUpdateRequest;
import com.university.portobellohub.dto.response.ItemPriceResponse;
import com.university.portobellohub.dto.response.ItemResponse;
import com.university.portobellohub.dto.response.PageResponse;
import com.university.portobellohub.entity.enums.ItemCondition;
import com.university.portobellohub.service.ExchangeRateService;
import com.university.portobellohub.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final ExchangeRateService exchangeRateService;

    public ItemController(ItemService itemService, ExchangeRateService exchangeRateService) {
        this.itemService = itemService;
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping
    public PageResponse<ItemResponse> searchItems(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) ItemCondition condition,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return itemService.searchPublishedItems(name, categoryId, condition, minPrice, maxPrice, pageable);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public PageResponse<ItemResponse> getPendingItems(@PageableDefault(size = 20) Pageable pageable) {
        return itemService.getPendingItems(pageable);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public PageResponse<ItemResponse> getMyItems(@PageableDefault(size = 20) Pageable pageable) {
        return itemService.getMyItems(pageable);
    }

    @GetMapping("/{id}")
    public ItemResponse getById(@PathVariable Long id) {
        return itemService.getById(id);
    }

    @GetMapping("/{id}/price")
    public ItemPriceResponse getItemPrice(
            @PathVariable Long id,
            @RequestParam(defaultValue = "USD") String currency
    ) {
        return exchangeRateService.getItemPriceInCurrency(id, currency);
    }

    @PostMapping("/electronics")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createElectronic(@Valid @RequestBody ElectronicItemCreateRequest request) {
        return itemService.createElectronicItem(request);
    }

    @PostMapping("/clothing")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createClothing(@Valid @RequestBody ClothingItemCreateRequest request) {
        return itemService.createClothingItem(request);
    }

    @PutMapping("/electronics/{id}")
    @PreAuthorize("isAuthenticated()")
    public ItemResponse updateElectronic(@PathVariable Long id, @Valid @RequestBody ItemUpdateRequest request) {
        return itemService.updateElectronicItem(id, request);
    }

    @PutMapping("/clothing/{id}")
    @PreAuthorize("isAuthenticated()")
    public ItemResponse updateClothing(@PathVariable Long id, @Valid @RequestBody ItemUpdateRequest request) {
        return itemService.updateClothingItem(id, request);
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ItemResponse uploadImage(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        return itemService.uploadImage(id, file);
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ItemResponse reviewItem(@PathVariable Long id, @Valid @RequestBody ItemReviewActionRequest request) {
        return itemService.reviewItem(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }
}
