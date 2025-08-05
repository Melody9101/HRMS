package com.example.Human_Resource_Management_System_HRMS_.constants;

public enum DepartmentType {

	// 部門(department)共分為以下四種:
	BOSS("Boss"), // 老闆。
	HR("HR"), // 人資部門。
	ACCOUNTANT("Acct"), // 會計部門。
	GENERAL_AFFAIRS("GA"); // 一般事務(總務)部門。

	private String departmentName;

	private DepartmentType(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	// 比對輸入的部門名稱是否均一致、正確、非null的方法。
	public static boolean isValidDepartment(String input) {
		if (input == null) {
			return false;
		}
		for (DepartmentType department : values()) {
			// 比較輸入的名稱是否與 Enum 的 departmentName 相符。
			if (department.getDepartmentName().equalsIgnoreCase(input)) {
				return true;
			}
		}
		return false;
	}

	public DepartmentType getTypeEnum() {
		return DepartmentType.fromType(this.departmentName); // 例如你原本存在的是 "basic_info"
	}

	public static DepartmentType fromType(String departmentName) {
		for (DepartmentType value : values()) {
			if (value.getDepartmentName().equalsIgnoreCase(departmentName)) {
				return value;
			}
		}
		throw new IllegalArgumentException("未知的類型: " + departmentName);
	}
}
