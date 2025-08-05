package com.example.Human_Resource_Management_System_HRMS_.constants;

public enum RejectionReason {

	MISSING_CERTIFICATE("Missing certificate"), // 缺少證明文件
	INVALID_CERTIFICATE("Invalid certificate"), // 無效的請假證明
	APPLICATION_DATE_ERROR("Application date error"), // 申請日期有問題
	LEAVE_TYPE_ERROR("Leave type error"), // 假別有問題
	REASON_IS_NOT_MATCH_TYPE("Reason is not match type"), // 請假理由有問題
	INSUFFICIENT_DAYS_REMAINING("Insufficient days remaining"), // 剩餘天數不足
	ADVANCE_APPLICATION_REQUIRED("Advance application required"), // 需要提前申請
	DUPLICATE_LEAVE_REQUEST("Duplicate leave request"), // 假單重複申請
	LEAVE_PERIOD_CONFLICTS_WITH_SCHEDULE("Leave period conflicts with schedule"), // 請假時間與排程有衝突
	OTHER("Other"); // 其他

	private String reason;

	public String getReason() {
		return reason;
	}

	private RejectionReason(String reason) {
		this.reason = reason;
	}

	public static boolean checkAllReason(String input) {
		if (input == null) {
			return false;
		}
		for (RejectionReason reason : values()) {
			if (input.equalsIgnoreCase(reason.getReason())) {
				return true;
			}
		}
		return false;
	}
}
