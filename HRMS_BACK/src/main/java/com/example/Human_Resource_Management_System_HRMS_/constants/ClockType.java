package com.example.Human_Resource_Management_System_HRMS_.constants;

public enum ClockType {

	CLOCK_IN("Clock in"), // 上班卡
	CLOCK_OUT("Clock out"); // 下班卡

	private String type;

	public String getType() {
		return type;
	}

	private ClockType(String type) {
		this.type = type;
	}

	public static boolean checkAllType(String input) {
		for (ClockType type : values()) {
			if (input.equalsIgnoreCase(type.getType())) {
				return true;
			}
		}
		return false;
	}
}
