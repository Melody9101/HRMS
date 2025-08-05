package com.example.Human_Resource_Management_System_HRMS_.constants;

public enum LeaveType {

	ANNUAL("Annual"), // 年假
	PAID_SICK_LEAVE("Paid Sick Leave"), // 有薪病假
	SICK("Sick"), // 病假
	PERSONAL("Personal"), // 事假
	MARRIAGE("Marriage"), // 婚假
	BEREAVEMENT("Bereavement"), // 喪假
	MATERNITY("Maternity"), // 產假
	PATERNITY("Paternity"), // 陪產假
	OFFICIAL("Official"), // 公假
	MENSTRUAL("Menstrual"), // 生理假;
	OTHER("Other"); // 其他

	private String type;

	public String getType() {
		return type;
	}

	private LeaveType(String type) {
		this.type = type;
	}

	public static boolean checkAllType(String input) {
		for (LeaveType status : values()) {
			if (input.equalsIgnoreCase(status.getType())) {
				return true;
			}
		}
		return false;
	}
}
