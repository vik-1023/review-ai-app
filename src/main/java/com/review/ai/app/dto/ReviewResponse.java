package com.review.ai.app.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
	private Long id;
	private String reviewText;
	private Integer rating;
	private String customerName;
	private String customerEmail;
	private String aiGeneratedReply;
	private String sentiment;
	private Boolean isReplied;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
