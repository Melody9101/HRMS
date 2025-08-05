package com.example.Human_Resource_Management_System_HRMS_.constants;

public enum LeaveStatus {
	APPROVED("Approved"), // 通過
	REJECTED("Rejected"), // 未通過
	PENDING_REVIEW("Pending review"), // 審查中
	PENDING_SUPPLEMENT("Pending supplement"),// 待補文件
	CANCEL_APPLICATION("Cancel application");// 取消申請

	private String status;

	public String getStatus() {
		return this.status;
	}

	private LeaveStatus(String status) {
		this.status = status;
	}

	public static boolean checkAllStatus(String input) {
		for (LeaveStatus status : values()) {
			if (input.equalsIgnoreCase(status.getStatus())) {
				return true;
			}
		}
		return false;
	}

}
