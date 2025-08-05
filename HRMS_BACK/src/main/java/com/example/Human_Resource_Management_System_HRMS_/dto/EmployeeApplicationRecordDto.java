package com.example.Human_Resource_Management_System_HRMS_.dto;

import java.time.LocalDateTime;

public class EmployeeApplicationRecordDto {

	private int applicationId;

	private int employeeId;

	private String comment;

	private String type;

	private String data;

	private LocalDateTime applyDateTime;

	private Boolean submitUp;

	private Boolean approved;

	private int approverId;

	private String approverRole;

	private String rejectionReason;

	private int applicationGroup;

	private LocalDateTime approvedDateTime;

	private int applyerId;

	public int getApplyerId() {
		return applyerId;
	}

	public void setApplyerId(int applyerId) {
		this.applyerId = applyerId;
	}

	public int getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(int applicationId) {
		this.applicationId = applicationId;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public LocalDateTime getApplyDateTime() {
		return applyDateTime;
	}

	public void setApplyDateTime(LocalDateTime applyDateTime) {
		this.applyDateTime = applyDateTime;
	}

	public Boolean getSubmitUp() {
		return submitUp;
	}

	public void setSubmitUp(Boolean submitUp) {
		this.submitUp = submitUp;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public int getApproverId() {
		return approverId;
	}

	public void setApproverId(int approverId) {
		this.approverId = approverId;
	}

	public String getApproverRole() {
		return approverRole;
	}

	public void setApproverRole(String approverRole) {
		this.approverRole = approverRole;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public int getApplicationGroup() {
		return applicationGroup;
	}

	public void setApplicationGroup(int applicationGroup) {
		this.applicationGroup = applicationGroup;
	}

	public LocalDateTime getApprovedDateTime() {
		return approvedDateTime;
	}

	public void setApprovedDateTime(LocalDateTime approvedDateTime) {
		this.approvedDateTime = approvedDateTime;
	}

}
