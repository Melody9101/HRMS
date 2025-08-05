package com.example.Human_Resource_Management_System_HRMS_.constants;

// enum列舉。
// HttpStatesCode >> 200代表成功。400代表badrequest。
// parameter 參數。
public enum ResMessage {
	SUCCESS(200, "Success!!"), //
	EMAIL_ALREADY_EXISTS(409, "Email already exists!!"), //
	INVALID_DEPARTMENT(400, "Invalid department!!"), //
	INVALID_POSITION(400, "Invalid position!!"), //
	PARAM_ID_ERROR(400, "Id error, please re-enter!!"), //
	NOT_FOUND(404, "Not Found!!"), //
	EMPLOYEE_NOT_FOUND(404, "Employee not found!!"), //
	FAILED(400, "Operation failed!!"), //
	END_DATE_BEFORE_START_DATE(400, "Unpaid leave end date must be after the start date!!"), //
	RESIGNATION_DATE_ERROR(400, "Resignation date error."), //
	POSITION_GRADE_MISMATCH(400, "Position and grade do not match, please check your input."), //
	ENCRYPTPASSWORD_ERROR(400, "Encrypt password error."), //
	PASSWORD_ERROR(400, "Password error."), //
	PASSWORD_MISMATCH(400, "Password mismatch."), //
	// 製作此表目的在於把console出來的東西回傳出去給使用者看
	// 常數名稱是自己定義的，數字是httpStatus上公認數值
	CLOCK_IN_SUCCESS(200, "Clock in success"), //
	CLOCK_OUT_SUCCESS(200, "Clock out success"), //
	CLOCK_TIME_ERROR(400, "Clock time error"), //
	CLOCK_TYPE_INVALID(400, "Clock type invalid."), //
	CLOCK_RECORD_NOT_FOUND(404, "Clock Record not found."), //
	CLOCK_TIME_MUST_BE_NOW(400, "Clock time must be now"), //
	ATTENDANCE_RECORD_NOT_SAME_EMPLOYEE(400, "Attendance record not same employee."), //
	CLOCK_SEQUENCE_ERROR(400, "Clock sequence error"), //
	PARAM_YEAR_ERROR(400, "Param year error"), //
	PARAM_MONTH_ERROR(400, "Param month error"), //
	PARAM_DAY_ERROR(400, "Param day error"), //
	DATE_FORMAT_ERROR(400, "Date format error"), //
	INVALID_TIME_RANGE(400, "Invalid time range."), //
	TIME_FORMAT_ERROR(400, "Time format error"), //
	CANNOT_QUERY_FUTURE_MONTH(400, "Cannot query future month"), //
	CANNOT_APPROVE_SELF_INFO(400, "Cannot approve self info"), //
	CANNOT_UPDATE_SELF_INFO(400, "Cannot update self info"), //
	CANNOT_UPDATE_DIFFERENT_DEPARTMENT_EMPLOYEE_INFO(400, "Cannot update different department employee info"), //
	CANNOT_APPROVE_SELF_APPLICATION(400, "Cannot approve self application"), //
	EMPLOYEE_ID_MISMATCH(400, "Employee id mismatch"), //
	DUPLICATE_CLOCK_ERROR(400, "Duplicate clock error"), //
	INSUFFICIENT_ANNUAL_LEAVE_BALANCE(400, "Insufficient annual leave balance"), //
	INSUFFICIENT_PAID_SICK_LEAVE_BALANCE(400, "Insufficient paid sick leave balance"), //
	APPLICATION_NOT_FOUND(404, "Application not found"), //
	GRADE_INSUFFICIENT(400, "Grade insufficient"), //
	PLEASE_LOGIN_FIRST(400, "Please login first"), //
	PLEASE_LOGOUT_FIRST(400, "Please logout first"), //
	REJECTION_REASON_REQUIRED(400, "Rejection reason required"), //
	STATUS_MISMATCH(400, "Status mismatch"), //
	REVIEW_STATUS_NOT_CHANGED(400, "Review status not changed"), //
	LEAVE_TYPE_ERROR(400, "Leave type error"), //
	INVALID_LEAVE_STATUS(400, "Invalid leave status"), //
	APPLICATION_TYPE_INVALID(400, "Application type invalid"), //
	RECORD_NOT_FOUND(404, "Record not found"), //
	MUST_BE_SAME_EMPLOYEE(400, "Must be same employee"), //
	MUST_BE_SAME_LEAVE(400, "Must be same leave"), //
	OVERTIME_TOO_SHORT(400, "Overtime too short"), //
	REJECTION_REASON_ERROR(400, "Rejection reason error"), //
	INVALID_PAY_DATE(400, "Payment date cannot be in the future."), //
	UNAUTHORIZED(400, "Unauthorized operator."), //
	INVALID_USER_ID(400, "Invalid user id."), //
	PAY_DATE_TOO_OLD(400, "Pay date too old."), //
	PAYER_ID_NOT_ALLOWED_OR_NOT_FOUND(400, "The payer id is invalid or does not exist."), //
	NO_PERMISSION(400, "The operator does not have permission to update salary."), //
	PAYROLL_ALREADY_EXISTS(400, "Payroll already exists."), //
	INVALID_QUERY_DATE(400, "The query date cannot be a future date."), //
	YEAR_ERROR(400, "Year error."), //
	MONTH_ERROR(400, "Month error."), //
	DATE_RANGE_IS_INCOMPLETE(400, "The query date range is incomplete."), //
	DATE_ERROR(400, "Date error."), //
	DUPLICATE_DATE_ERROR(400, "Duplicate date error."), //
	EMPLOYEE_ID_REQUIRED(400, "Employee id is required."), //
	DATE_RANGE_ERROR(400,
			"The start date cannot be later than the end date. The start date and end date cannot be in the future."), //
	ACCESS_DENIED(403, "Access denied"), //
	ACCOUNT_NOT_FOUND(404, "Account not found"), //
	VERIFICATION_FAILED(400, "Verification failed"), //
	VERIFICATION_CODE_VALID(400, "Verification code valid"), //
	EMPLOYEE_ID_NOT_FOUND(400, "Employee id not found."), //
	BONUS_ERROR(400, "Bonus error."), //
	PARAM_ID_LIST_ERROR(400, "Param id list error"), //

	INVALID_GENDER_FOR_LEAVE(400, "Invalid gender for menstrual leave"), //
	SALARY_ALREADY_EXISTS(400, "Salary already exists.");

	private int code;

	private String message;

	private ResMessage(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
