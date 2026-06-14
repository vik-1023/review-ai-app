package com.review.ai.app;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.review.ai.app.dto.ReviewRequest;
import com.review.ai.app.dto.ReviewResponse;
import com.review.ai.app.entity.Review;
import com.review.ai.app.repository.ReviewRepository;
import com.review.ai.app.service.ReviewService;

@SpringBootTest
class ReviewAiAppApplicationTests {
	 @Autowired
	    private ReviewService reviewService;
	    
	    @Test
	    public void testCreateReview() {
	        ReviewRequest request = new ReviewRequest();
	        request.setReviewText("Excellent product! Very fast delivery.");
	        request.setRating(5);
	        request.setCustomerName("Amit Sharma");
	        request.setCustomerEmail("amit@example.com");
	        
	        ReviewResponse response = reviewService.createReview(request);
	        
	        System.out.println("Created Review ID: " + response.getId());
	        System.out.println("Sentiment: " + response.getSentiment());
	        System.out.println("Review Text: " + response.getReviewText());
	        
	        assert response.getId() != null;
	        assert response.getSentiment().equals("POSITIVE");
	    }
	    
	    @Test
	    public void testGetAllReviews() {
	        List<ReviewResponse> reviews = reviewService.getAllReviews();
	        System.out.println("Total Reviews: " + reviews.size());
	        reviews.forEach(review -> {
	            System.out.println("ID: " + review.getId() + 
	                             ", Rating: " + review.getRating() + 
	                             ", Sentiment: " + review.getSentiment());
	        });
	    }
	    
	    @Test
	    public void testGetReviewsByRating() {
	        List<ReviewResponse> reviews = reviewService.getReviewsByRating(5);
	        System.out.println("5-star Reviews: " + reviews.size());
	    }
	    
	    @Test
	    public void testGetUnrepliedReviews() {
	        List<ReviewResponse> unreplied = reviewService.getUnrepliedReviews();
	        System.out.println("Unreplied Reviews: " + unreplied.size());
	    }

}
