package com.review.ai.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.review.ai.app.dto.ReviewRequest;
import com.review.ai.app.dto.ReviewResponse;
import com.review.ai.app.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
	private final ReviewService reviewService;

	// Create a new review
	@PostMapping
	public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
		ReviewResponse response = reviewService.createReview(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Get review by ID
	@GetMapping("/{id}")
	public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
		ReviewResponse response = reviewService.getReviewById(id);
		return ResponseEntity.ok(response);
	}

	// Get all reviews
	@GetMapping
	public ResponseEntity<List<ReviewResponse>> getAllReviews() {
		List<ReviewResponse> reviews = reviewService.getAllReviews();
		return ResponseEntity.ok(reviews);
	}

	// Update review
	@PutMapping("/{id}")
	public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long id,
			@Valid @RequestBody ReviewRequest request) {
		ReviewResponse response = reviewService.updateReview(id, request);
		return ResponseEntity.ok(response);
	}

	// Delete review
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
		reviewService.deleteReview(id);
		return ResponseEntity.noContent().build();
	}

	// Get reviews by rating
	@GetMapping("/rating/{rating}")
	public ResponseEntity<List<ReviewResponse>> getReviewsByRating(@PathVariable Integer rating) {
		List<ReviewResponse> reviews = reviewService.getReviewsByRating(rating);
		return ResponseEntity.ok(reviews);
	}

	// Get unreplied reviews
	@GetMapping("/unreplied")
	public ResponseEntity<List<ReviewResponse>> getUnrepliedReviews() {
		List<ReviewResponse> reviews = reviewService.getUnrepliedReviews();
		return ResponseEntity.ok(reviews);
	}

	// Get total review count
	@GetMapping("/count")
	public ResponseEntity<Long> getTotalReviewCount() {
		Long count = reviewService.getTotalReviewCount();
		return ResponseEntity.ok(count);
	}

}
