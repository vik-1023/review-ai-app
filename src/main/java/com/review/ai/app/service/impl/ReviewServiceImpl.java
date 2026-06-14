package com.review.ai.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.review.ai.app.dto.ReviewRequest;
import com.review.ai.app.dto.ReviewResponse;
import com.review.ai.app.entity.Review;
import com.review.ai.app.repository.ReviewRepository;
import com.review.ai.app.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;

	@Override
	public ReviewResponse createReview(ReviewRequest request) {
		// Convert DTO to Entity
		Review review = new Review();
		review.setReviewText(request.getReviewText());
		review.setRating(request.getRating());
		review.setCustomerName(request.getCustomerName());
		review.setCustomerEmail(request.getCustomerEmail());
		// Auto-determine sentiment based on rating
		String sentiment = determineSentiment(request.getRating());
		review.setSentiment(sentiment);
		// Save to database
		Review savedReview = reviewRepository.save(review);
		return convertToResponse(savedReview);
	}

	@Override
	public ReviewResponse getReviewById(Long id) {
		Review review = reviewRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
		return convertToResponse(review);
	}

	@Override
	public List<ReviewResponse> getAllReviews() {
		return reviewRepository.findAll().stream().map(this::convertToResponse).collect(Collectors.toList());
	}

	@Override
	public ReviewResponse updateReview(Long id, ReviewRequest request) {
		Review review = reviewRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

		review.setReviewText(request.getReviewText());
		review.setRating(request.getRating());
		review.setCustomerName(request.getCustomerName());
		review.setCustomerEmail(request.getCustomerEmail());
		review.setSentiment(determineSentiment(request.getRating()));

		Review updatedReview = reviewRepository.save(review);
		return convertToResponse(updatedReview);
	}

	@Override
	public void deleteReview(Long id) {
		if (!reviewRepository.existsById(id)) {
			throw new RuntimeException("Review not found with id: " + id);
		}
		reviewRepository.deleteById(id);

	}

	@Override
	public List<ReviewResponse> getReviewsByRating(Integer rating) {
		return reviewRepository.findByRating(rating).stream().map(this::convertToResponse).collect(Collectors.toList());
	}

	@Override
	public List<ReviewResponse> getUnrepliedReviews() {
		return reviewRepository.findByIsRepliedFalse().stream().map(this::convertToResponse)
				.collect(Collectors.toList());
	}

	@Override
	public Long getTotalReviewCount() {
		return reviewRepository.count();
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
