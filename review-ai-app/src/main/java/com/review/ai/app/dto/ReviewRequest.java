package com.review.ai.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
	@NotBlank(message = "Review text cannot be empty")
	@Size(min = 5, max = 500, message = "Review must be between 5 and 500 characters")
	private String reviewText;

	@NotNull(message = "Rating is required")
	@Min(value = 1, message = "Rating must be at least 1")
	@Max(value = 5, message = "Rating cannot exceed 5")
	private Integer rating;

	private String customerName;

	@Email(message = "Invalid email format")
	private String customerEmail;
}
