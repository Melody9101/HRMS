package com.example.Human_Resource_Management_System_HRMS_.vo;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SearchEmployeeByEmailAndPhoneReq {

	@NotBlank(message = ConstantsMessage.EMAIL_IS_NECESSARY)
	@Email(regexp = ConstantsMessage.EMAIL_PATTERN, message = ConstantsMessage.EMAIL_ADDRESS_IS_INVALID)
	private String email;

	@NotBlank(message = ConstantsMessage.PHONE_IS_NECESSARY)
	@Pattern(regexp = ConstantsMessage.PHONE_PATTERN, message = ConstantsMessage.PARAM_PHONE_ERROR)
	private String phone;

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

}
