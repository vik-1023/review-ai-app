package com.review.ai.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.review.ai.app.dto.ReviewRequest;
import com.review.ai.app.dto.ReviewResponse;
import com.review.ai.app.entity.Review;
import com.review.ai.app.repository.ReviewRepository;
import com.review.ai.app.service.ReviewService;
import com.review.ai.app.service.PollinationsAiReplyService;  // NEW IMPORT

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // NEW IMPORT for logging

@Slf4j  // NEW - Add logging
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	
	// NEW - Inject AI service
	private final PollinationsAiReplyService aiReplyService;  // NEW

	@Override
	@Transactional  // NEW - Add transaction management
	public ReviewResponse createReview(ReviewRequest request) {
		log.info("Creating new review with rating: {}", request.getRating());  // NEW
		
		// Convert DTO to Entity
		Review review = new Review();
		review.setReviewText(request.getReviewText());
		review.setRating(request.getRating());
		review.setCustomerName(request.getCustomerName());
		review.setCustomerEmail(request.getCustomerEmail());
		
		// Auto-determine sentiment based on rating
		String sentiment = determineSentiment(request.getRating());
		review.setSentiment(sentiment);
		
		// Save to database first (to get ID)
		Review savedReview = reviewRepository.save(review);
		log.info("Review saved with ID: {}", savedReview.getId());  // NEW
		
		// NEW - Generate AI reply
		try {
			String aiReply = aiReplyService.generateReply(savedReview);
			savedReview.setAiGeneratedReply(aiReply);
			savedReview.setIsReplied(true);
			Review updatedReview = reviewRepository.save(savedReview);
			log.info("AI reply generated for review: {}", updatedReview.getId());  // NEW
			return convertToResponse(updatedReview);
		} catch (Exception e) {
			log.error("Failed to generate AI reply: {}", e.getMessage());  // NEW
			return convertToResponse(savedReview);
		}
	}

	@Override
	public ReviewResponse getReviewById(Long id) {
		Review review = reviewRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
		return convertToResponse(review);
	}

	@Override
	public List<ReviewResponse> getAllReviews() {
		return reviewRepository.findAll().stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional  // NEW
	public ReviewResponse updateReview(Long id, ReviewRequest request) {
		Review review = reviewRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

		review.setReviewText(request.getReviewText());
		review.setRating(request.getRating());
		review.setCustomerName(request.getCustomerName());
		review.setCustomerEmail(request.getCustomerEmail());
		review.setSentiment(determineSentiment(request.getRating()));

		Review updatedReview = reviewRepository.save(review);
		log.info("Review updated: {}", updatedReview.getId());  // NEW
		return convertToResponse(updatedReview);
	}

	@Override
	@Transactional  // NEW
	public void deleteReview(Long id) {
		if (!reviewRepository.existsById(id)) {
			throw new RuntimeException("Review not found with id: " + id);
		}
		reviewRepository.deleteById(id);
		log.info("Review deleted: {}", id);  // NEW
	}

	@Override
	public List<ReviewResponse> getReviewsByRating(Integer rating) {
		return reviewRepository.findByRating(rating).stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public List<ReviewResponse> getUnrepliedReviews() {
		return reviewRepository.findByIsRepliedFalse().stream()
				.map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public Long getTotalReviewCount() {
		return reviewRepository.count();
	}

	// NEW - Generate AI reply for existing review
	@Override
	@Transactional
	public ReviewResponse generateAiReplyForReview(Long id) {
		log.info("Generating AI reply for existing review: {}", id);  // NEW
		
		Review review = reviewRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
		
		String aiReply = aiReplyService.generateReply(review);
		review.setAiGeneratedReply(aiReply);
		review.setIsReplied(true);
		Review updatedReview = reviewRepository.save(review);
		
		log.info("AI reply generated successfully for review: {}", id);  // NEW
		return convertToResponse(updatedReview);
	}

	// Helper method to determine sentiment
	private String determineSentiment(Integer rating) {
		if (rating >= 4) {
			return "POSITIVE";
		} else if (rating == 3) {
			return "NEUTRAL";
		} else {
			return "NEGATIVE";
		}
	}

	// Helper method to convert Entity to Response DTO
	private ReviewResponse convertToResponse(Review review) {
		ReviewResponse response = new ReviewResponse();
		response.setId(review.getId());
		response.setReviewText(review.getReviewText());
		response.setRating(review.getRating());
		response.setCustomerName(review.getCustomerName());
		response.setCustomerEmail(review.getCustomerEmail());
		response.setAiGeneratedReply(review.getAiGeneratedReply());
		response.setSentiment(review.getSentiment());
		response.setIsReplied(review.getIsReplied());
		response.setCreatedAt(review.getCreatedAt());
		response.setUpdatedAt(review.getUpdatedAt());
		return response;
	}
}