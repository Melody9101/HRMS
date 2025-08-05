package com.example.Human_Resource_Management_System_HRMS_.vo;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ApproveLeaveReq {

	@Min(value = 1, message = ConstantsMessage.PARAMETER_lEAVE_ID_ERROR)
	private int leaveId;

	@NotBlank(message = ConstantsMessage.PARAMETER_STATUS_ERROR)
	private String status;

	private String rejectionReason;

	public int getLeaveId() {
		return leaveId;
	}

	public void setLeaveId(int leaveId) {
		this.leaveId = leaveId;
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

}
