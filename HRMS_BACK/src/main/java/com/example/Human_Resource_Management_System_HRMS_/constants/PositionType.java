package com.example.Human_Resource_Management_System_HRMS_.constants;

public enum PositionType {

	// 職等共分為以下三種:
	// 老闆(總經理)、員工主管、一般員工。
	// (資料庫欄位--grade依序為 11、6~10、1~5 )。
	BOSS("Boss"), // 老闆(總經理)。
	MANAGER("Manager"), // 員工主管。
	EMPLOYEE("Employee"); // 一般員工。

	private String positionName;

	private PositionType(String positionName) {
		this.positionName = positionName;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	// 比對輸入的職等名稱是否均一致、正確、非null的方法。
	public static boolean isValidPosition(String input) {
		if (input == null) {
			return false;
		}
		for (PositionType Position : values()) {
			// 比較輸入的名稱是否與 Enum 的 positionName 相符。
			if (Position.getPositionName().equalsIgnoreCase(input)) {
				return true;
			}
		}
		return false;
	}
}
