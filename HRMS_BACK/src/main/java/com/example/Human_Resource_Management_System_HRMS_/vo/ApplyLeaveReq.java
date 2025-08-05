package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.time.LocalDateTime;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ApplyLeaveReq {

	@NotNull(message = ConstantsMessage.PARAMETER_START_TIME_ERROR)
	private LocalDateTime startTime;

	@NotNull(message = ConstantsMessage.PARAMETER_END_TIME_ERROR)
	private LocalDateTime endTime;

	@NotBlank(message = ConstantsMessage.PARAMETER_LEAVE_TYPE_ERROR)
	private String leaveType;

	private byte[] certificate;

	private String reason;

	private String certificateFileType;

	public String getCertificateFileType() {
		return certificateFileType;
	}

	public void setCertificateFileType(String certificateFileType) {
		this.certificateFileType = certificateFileType;
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

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public byte[] getCertificate() {
		return certificate;
	}

	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
