package com.review.ai.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.review.ai.app.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
	// Find reviews by rating
	List<Review> findByRating(Integer rating);

	// Find un-replied reviews
	List<Review> findByIsRepliedFalse();

	// Find reviews by sentiment
	List<Review> findBySentiment(String sentiment);

	// Custom query: find reviews with rating >= given value
	@Query("SELECT r FROM Review r WHERE r.rating >= :minRating")
	List<Review> findHighRatedReviews(@Param("minRating") Integer minRating);

	// Count reviews by rating
	@Query("SELECT COUNT(r) FROM Review r WHERE r.rating = :rating")
	Long countByRating(@Param("rating") Integer rating);

	// Search reviews containing text
	List<Review> findByReviewTextContainingIgnoreCase(String keyword);

	// Get latest 10 reviews
	List<Review> findTop10ByOrderByCreatedAtDesc();
}
