package com.university.portobellohub.service;

import com.university.portobellohub.dto.request.ReviewCreateRequest;
import com.university.portobellohub.dto.request.ReviewUpdateRequest;
import com.university.portobellohub.dto.response.PageResponse;
import com.university.portobellohub.dto.response.ReviewResponse;
import com.university.portobellohub.entity.Item;
import com.university.portobellohub.entity.Review;
import com.university.portobellohub.entity.User;
import com.university.portobellohub.entity.enums.RoleName;
import com.university.portobellohub.exception.BadRequestException;
import com.university.portobellohub.exception.ForbiddenException;
import com.university.portobellohub.exception.ResourceNotFoundException;
import com.university.portobellohub.repository.OrderRepository;
import com.university.portobellohub.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ItemService itemService;
    private final OrderRepository orderRepository;
    private final SecurityUtils securityUtils;

    public ReviewService(
            ReviewRepository reviewRepository,
            ItemService itemService,
            OrderRepository orderRepository,
            SecurityUtils securityUtils
    ) {
        this.reviewRepository = reviewRepository;
        this.itemService = itemService;
        this.orderRepository = orderRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getReviewsByItem(Long itemId, Pageable pageable) {
        itemService.findItem(itemId);
        Page<ReviewResponse> page = reviewRepository.findByItemId(itemId, pageable)
                .map(ReviewResponse::fromEntity);
        return PageResponse.from(page);
    }

    @Transactional
    public ReviewResponse createReview(Long itemId, ReviewCreateRequest request) {
        User user = securityUtils.getCurrentUser();
        Item item = itemService.findItem(itemId);

        if (reviewRepository.existsByUserIdAndItemId(user.getId(), itemId)) {
            throw new BadRequestException("You have already reviewed this item");
        }
        if (!hasPurchasedItem(user.getId(), itemId)) {
            throw new BadRequestException("You can only review items you have purchased");
        }

        Review review = new Review();
        review.setUser(user);
        review.setItem(item);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return ReviewResponse.fromEntity(reviewRepository.save(review));
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest request) {
        Review review = findReview(reviewId);
        assertOwnerOrAdmin(review);

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }

        return ReviewResponse.fromEntity(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = findReview(reviewId);
        assertOwnerOrAdmin(review);
        reviewRepository.delete(review);
    }

    private Review findReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
    }

    private void assertOwnerOrAdmin(Review review) {
        User currentUser = securityUtils.getCurrentUser();
        boolean isOwner = review.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("You are not allowed to modify this review");
        }
    }

    private boolean hasPurchasedItem(Long userId, Long itemId) {
        return orderRepository.hasUserPurchasedItem(userId, itemId);
    }
}
