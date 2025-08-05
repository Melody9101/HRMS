package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.time.LocalDateTime;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OvertimeApplicationReq extends UpdateEmployeeApplicationData {
	
	// 加班緣由。
	@NotBlank(message = ConstantsMessage.REASON_IS_NECESSARY)
	private String reason;
	
	// 加班開始時間。
	@NotNull(message = ConstantsMessage.PARAMETER_START_TIME_ERROR)
	private LocalDateTime startTime;
	
	// 加班結束時間。
	@NotNull(message = ConstantsMessage.PARAMETER_END_TIME_ERROR)
	private LocalDateTime endTime;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

}
