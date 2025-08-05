package com.example.Human_Resource_Management_System_HRMS_.vo;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class UpdateEmployeeBasicInfoReq extends UpdateEmployeeApplicationData {

	private int id;

	@NotBlank(message = ConstantsMessage.NAME_IS_NECESSARY)
	private String name;

	// 檢查 email 的格式。
	@Email(regexp = ConstantsMessage.EMAIL_PATTERN, message = ConstantsMessage.EMAIL_ADDRESS_IS_INVALID)
	@NotBlank(message = ConstantsMessage.EMAIL_IS_NECESSARY)
	private String email;

	// 檢查 phone 的格式。
	@Pattern(regexp = ConstantsMessage.PHONE_PATTERN, message = ConstantsMessage.PARAM_PHONE_ERROR)
	@NotBlank(message = ConstantsMessage.PHONE_IS_NECESSARY)
	private String phone;

	@NotNull(message = ConstantsMessage.GENDER_IS_NECESSARY)
	private Boolean gender;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Boolean getGender() {
		return gender;
	}

	public void setGender(Boolean gender) {
		this.gender = gender;
	}

}
