package com.example.Human_Resource_Management_System_HRMS_.dto;

import java.time.LocalDateTime;

public class EmailVerificationDto {
	
	private String email;

	private String code;

	private LocalDateTime expireAt;

	private boolean verified;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public LocalDateTime getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(LocalDateTime expireAt) {
		this.expireAt = expireAt;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

}
