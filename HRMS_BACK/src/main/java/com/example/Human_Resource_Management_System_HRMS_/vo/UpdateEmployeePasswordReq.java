package com.example.Human_Resource_Management_System_HRMS_.vo;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateEmployeePasswordReq {
	
	// 1. 依靠信箱更新密碼，故信箱必需輸入。2. 檢查 email 的格式。
	@NotBlank(message = ConstantsMessage.EMAIL_IS_NECESSARY)
	@Email(regexp = ConstantsMessage.EMAIL_PATTERN, message = ConstantsMessage.EMAIL_ADDRESS_IS_INVALID)
	private String email;
	
	// 1. 必須輸入原始密碼。2. 檢查輸入的原始密碼格式是否正確。
	@NotBlank(message = ConstantsMessage.PASSWORD_IS_NECESSARY)
	private String oldPassword;
	
	// 1. 必須輸入新密碼。2. 密碼長度介於8~30間。3. 新密碼格式檢查。
	@NotBlank(message = ConstantsMessage.NEW_PASSWORD_IS_NECESSARY)
	@Size(min = 8, max = 30, message = ConstantsMessage.PARAM_PASSWORD_LENGTH_ERROR)
	@Pattern(regexp = ConstantsMessage.PASSWORD_PATTERN, message = ConstantsMessage.PARAM_PASSWORD_COMPLEXITY_ERROR)
	private String newPassword;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
