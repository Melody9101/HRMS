package com.example.Human_Resource_Management_System_HRMS_.constants;

public enum ApplicationType {

	basic_info("basic_info"), //
	job("job"), //
	resign("resign"), //
	clock("clock"), //
	overtime("overtime");

	private String type;

	private ApplicationType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static boolean checkAllType(String input) {
		for (ApplicationType type : values()) {
			if (input.equalsIgnoreCase(type.getType())) {
				return true;
			}
		}
		return false;
	}

	public ApplicationType getTypeEnum() {
		return ApplicationType.fromType(this.type); // 例如你原本存在的是 "basic_info"
	}

	public static ApplicationType fromType(String type) {
		for (ApplicationType value : values()) {
			if (value.getType().equalsIgnoreCase(type)) {
				return value;
			}
		}
		throw new IllegalArgumentException("未知的類型: " + type);
	}

}
