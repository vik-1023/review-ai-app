package com.review.ai.app.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 500)
	private String reviewText;
	@Column(nullable = false)
	private Integer rating;
	@Column(length = 50)
	private String customerName;
	@Column(length = 50)
	private String customerEmail;
	@Column(length = 2000)
	private String aiGeneratedReply;
	@Column(length = 50)
	private String sentiment;
	@Column(nullable = false)
	private Boolean isReplied = false;
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	 // Custom constructor for creating new reviews
    public Review(String reviewText, Integer rating, String customerName, String customerEmail) {
        this.reviewText = reviewText;
        this.rating = rating;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
    }

}
