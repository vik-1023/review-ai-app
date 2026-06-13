package com.review.ai.app.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
	private int statusCode;
	private String error;
	private String message;
	private LocalDateTime timestamp;
}
