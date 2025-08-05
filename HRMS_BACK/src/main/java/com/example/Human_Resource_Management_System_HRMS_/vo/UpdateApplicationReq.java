package com.example.Human_Resource_Management_System_HRMS_.vo;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateApplicationReq {

	@Min(value = 1, message = ConstantsMessage.PARAMETER_lEAVE_ID_ERROR)
	private int leaveId;

	@NotNull(message = ConstantsMessage.PARAMETER_CERTIFICATE_ERROR)
	private byte[] certificate;

	@NotNull(message = ConstantsMessage.PARAMETER_CERTIFICATEFILETYPE_ERROR)
	private String certificateFileType;

	public String getCertificateFileType() {
		return certificateFileType;
	}

	public void setCertificateFileType(String certificateFileType) {
		this.certificateFileType = certificateFileType;
	}

	public int getLeaveId() {
		return leaveId;
	}

	public void setLeaveId(int leaveId) {
		this.leaveId = leaveId;
	}

	public byte[] getCertificate() {
		return certificate;
	}

	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}

}
