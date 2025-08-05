package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.time.LocalDateTime;

import com.example.Human_Resource_Management_System_HRMS_.constants.ConstantsMessage;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateClockTimeReq extends UpdateEmployeeApplicationData {

	@Min(value = 1, message = ConstantsMessage.PARAMETER_ID_ERROR)
	private int attendanceId;

	@NotBlank(message = ConstantsMessage.CLOCK_TYPE_IS_NECESSARY)
	private String clockType;

	@NotBlank(message = ConstantsMessage.REASON_IS_NECESSARY)
	private String reason;

	@NotNull(message = ConstantsMessage.CLOCK_TIME_IS_NECESSARY)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime clockTime;

	public int getAttendanceId() {
		return attendanceId;
	}

	public void setAttendanceId(int attendanceId) {
		this.attendanceId = attendanceId;
	}

	public String getClockType() {
		return clockType;
	}

	public void setClockType(String clockType) {
		this.clockType = clockType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDateTime getClockTime() {
		return clockTime;
	}

	public void setClockTime(LocalDateTime clockTime) {
		this.clockTime = clockTime;
	}

}
