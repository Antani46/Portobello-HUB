package com.university.portobellohub.controller;

import com.university.portobellohub.dto.request.ReviewCreateRequest;
import com.university.portobellohub.dto.request.ReviewUpdateRequest;
import com.university.portobellohub.dto.response.PageResponse;
import com.university.portobellohub.dto.response.ReviewResponse;
import com.university.portobellohub.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/api/items/{itemId}/reviews")
    public PageResponse<ReviewResponse> getItemReviews(
            @PathVariable Long itemId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return reviewService.getReviewsByItem(itemId, pageable);
    }

    @PostMapping("/api/items/{itemId}/reviews")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse createReview(
            @PathVariable Long itemId,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        return reviewService.createReview(itemId, request);
    }

    @PutMapping("/api/reviews/{id}")
    @PreAuthorize("isAuthenticated()")
    public ReviewResponse updateReview(@PathVariable Long id, @Valid @RequestBody ReviewUpdateRequest request) {
        return reviewService.updateReview(id, request);
    }

    @DeleteMapping("/api/reviews/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }
}
