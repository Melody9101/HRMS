package com.example.Human_Resource_Management_System_HRMS_.constants;

public enum DepartmentAndPosition {
	BOSS("Boss"), //
	HR_MANAGER("HR Manager"), //
	HR_EMPLOYEE("HR Employee"), //
	ACCOUNTING_MANAGER("AC Manager"), //
	ACCOUNTING_EMPLOYEE("AC Employee"), //
	GENERAL_MANAGER("GA Manager"), //
	GENERAL_EMPLOYEE("GA Employee");

	private String Position;

	public String getPosition() {
		return Position;
	}

	private DepartmentAndPosition(String position) {
		Position = position;
	}

}
