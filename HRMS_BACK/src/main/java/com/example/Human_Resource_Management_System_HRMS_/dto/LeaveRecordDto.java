package com.example.Human_Resource_Management_System_HRMS_.dto;

import java.time.LocalDateTime;

public class LeaveRecordDto {

	private int leaveApplicationId;

	private int employeeId;

	private String employeeName;

	private String leaveType;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private String reason;

	private byte[] certificate;

	private LocalDateTime applyDateTime;

	private String status;

	private String rejectionReason;

	private Boolean approved;

	private LocalDateTime approvedDateTime;

	private String approvalPendingRole;

	private int approverId;

	private Boolean submitUp;

	private String certificateFileType;

	public String getCertificateFileType() {
		return certificateFileType;
	}

	public void setCertificateFileType(String certificateFileType) {
		this.certificateFileType = certificateFileType;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public int getLeaveApplicationId() {
		return leaveApplicationId;
	}

	public void setLeaveApplicationId(int leaveApplicationId) {
		this.leaveApplicationId = leaveApplicationId;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public LocalDateTime getApprovedDateTime() {
		return approvedDateTime;
	}

	public void setApprovedDateTime(LocalDateTime approvedDateTime) {
		this.approvedDateTime = approvedDateTime;
	}

	public String getApprovalPendingRole() {
		return approvalPendingRole;
	}

	public void setApprovalPendingRole(String approvalPendingRole) {
		this.approvalPendingRole = approvalPendingRole;
	}

	public int getApproverId() {
		return approverId;
	}

	public void setApproverId(int approverId) {
		this.approverId = approverId;
	}

	public Boolean getSubmitUp() {
		return submitUp;
	}

	public void setSubmitUp(Boolean submitUp) {
		this.submitUp = submitUp;
	}

}
