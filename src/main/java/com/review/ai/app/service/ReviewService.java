package com.review.ai.app.service;

import java.util.List;

import com.review.ai.app.dto.ReviewRequest;
import com.review.ai.app.dto.ReviewResponse;

public interface ReviewService {

	ReviewResponse createReview(ReviewRequest request);

	ReviewResponse getReviewById(Long id);

	List<ReviewResponse> getAllReviews();

	ReviewResponse updateReview(Long id, ReviewRequest request);

	void deleteReview(Long id);

	List<ReviewResponse> getReviewsByRating(Integer rating);

	List<ReviewResponse> getUnrepliedReviews();

	Long getTotalReviewCount();

}
