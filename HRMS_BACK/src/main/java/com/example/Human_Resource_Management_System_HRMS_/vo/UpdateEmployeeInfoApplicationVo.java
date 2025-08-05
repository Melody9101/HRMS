package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.time.LocalDateTime;

public class UpdateEmployeeInfoApplicationVo {
	private int id;

	private int employeeId;

	private String type;

	private String comment;

	private Object data;

	private LocalDateTime applyDateTime;

	private Boolean submitUp;

	private String approvalPendingRole;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
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

	public String getApprovalPendingRole() {
		return approvalPendingRole;
	}

	public void setApprovalPendingRole(String approvalPendingRole) {
		this.approvalPendingRole = approvalPendingRole;
	}

}
