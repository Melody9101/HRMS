package com.example.Human_Resource_Management_System_HRMS_.dto;

import java.time.LocalDateTime;

public class LeaveApplicationDto {

	private int leaveId;

	private String employeeName;

	private int employerId;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private String leaveType;

	private byte[] certificate;

	private LocalDateTime applyDateTime;

	private String reason;

	private String status;

	private String approvalPendingRole;

	private Boolean submitUp;

	private String certificateFileType;

	// Getters and Setters

	public String getCertificateFileType() {
		return certificateFileType;
	}

	public void setCertificateFileType(String certificateFileType) {
		this.certificateFileType = certificateFileType;
	}

	public int getLeaveId() {
		return leaveId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public void setLeaveId(int leaveId) {
		this.leaveId = leaveId;
	}

	public int getEmployerId() {
		return employerId;
	}

	public void setEmployerId(int employerId) {
		this.employerId = employerId;
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

	public LocalDateTime getApplyDateTime() {
		return applyDateTime;
	}

	public void setApplyDateTime(LocalDateTime applyDateTime) {
		this.applyDateTime = applyDateTime;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getApprovalPendingRole() {
		return approvalPendingRole;
	}

	public void setApprovalPendingRole(String approvalPendingRole) {
		this.approvalPendingRole = approvalPendingRole;
	}

	public Boolean getSubmitUp() {
		return submitUp;
	}

	public void setSubmitUp(Boolean submitUp) {
		this.submitUp = submitUp;
	}
}