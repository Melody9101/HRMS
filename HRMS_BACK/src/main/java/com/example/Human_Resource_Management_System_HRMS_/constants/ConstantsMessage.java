package com.example.Human_Resource_Management_System_HRMS_.constants;

public class ConstantsMessage {

	// Employee用:
	public static final String ID_IS_NECESSARY = "Id is necessary.";

	public static final String DEPARTMENT_IS_NECESSARY = "Department is necessary.";

	public static final String NAME_IS_NECESSARY = "Name is necessary.";

	public static final String EMAIL_IS_NECESSARY = "Email is necessary.";

	public static final String PASSWORD_FORMAT_ERROR = "Password format error.";

	public static final String PHONE_FORMAT_CHECK = "^[0-9]{8,15}$";

	public static final String PASSWORD_IS_NECESSARY = "Password is necessary.";

	public static final String PARAM_PASSWORD_LENGTH_ERROR = "Password length error.";

	public static final String PARAM_PASSWORD_COMPLEXITY_ERROR = "Password complexity error.";

	public static final String PHONE_IS_NECESSARY = "Phone is necessary.";

	public static final String EMAIL_ADDRESS_IS_INVALID = "Please enter a valid email address.";

	public static final String GRADE_OUT_OF_RANGE_ERROR = "Grade must be between 1 and 11.";

	public static final String PARAM_ENTRYDATE_ERROR = "The entry date cannot be empty and cannot be a future date!!";

	public static final String PARAM_REINSTATEMENT_ERROR = "Reinstatement date cannot be null or be empty.";

	public static final String PARAM_PHONE_ERROR = "The phone number format is incorrect. Please enter pure numbers with a length between 8 and 15 digits!!";

	public static final String PARAM_SALARIES_ERROR = "Param salaries error!!";

	public static final String PARAM_POSITION_ERROR = "Param position error!!";

	public static final String PARAM_REMAININGPREVIOUSANNUALLEAVE_ERROR = "The remaining previous annual leave cannot be a negative number!!";

	public static final String PARAM_REMAININGCURRENTANNUALLEAVE_ERROR = "The remaining current annual leave cannot be a negative number!!";

	public static final String PARAM_REMAININGPAIDSICKLEAVE_ERROR = "The remaining paid sick leave cannot be a negative number!!";

	public static final String PARAM_RESIGNATIONDATE_ERROR = "The resignation date cannot be in the future!!";

	public static final String GENDER_IS_NECESSARY = "Gender is necessary.";

	public static final String DATA_IS_NECESSARY = "Data is necessary";

	public static final String RESIGNATION_DATE_IS_NECESSARY = "Rresignation date is necessary.";

	public static final String RESIGNATION_REASON_IS_NECESSARY = "Resignation reason is necessary.";

	public static final String NEW_PASSWORD_IS_NECESSARY = "New password is necessary.";

	public static final String REASON_IS_NECESSARY = "Reason is necessary.";

	public static final String CLOCK_TIME_IS_NECESSARY = "Clock time is necessary.";

	public static final String CLOCK_TYPE_IS_NECESSARY = "Clock type is necessary.";

	public static final String UNPAID_LEAVE_START_DATE_IS_NECESSARY = "Unpaid leave start date is necessary.";

	public static final String UNPAID_LEAVE_END_DATE_IS_NECESSARY = "Unpaid leave end date is necessary.";

	public static final String UNPAID_LEAVE_REASON_IS_NECESSARY = "Unpaid leave reason is necessary.";

	public static final String UNPAID_LEAVE_REASON_TOO_LONG = "Unpaid leave reason is too long, please keep it under 200 characters.";

	public static final String PARAM_APPLICATION_ID_ERROR = "Param application id error";

	public static final String APPLICATION_TYPE_IS_REQUIRED = "Application type is required";

	public static final String APPLICATION_TYPE_INVALID = "application type invalid";
	// Attendance用:
	public static final String PARAMETER_EMPLOYEE_ID_ERROR = "Parameter employee id error";

	public static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

	// 密碼長度必須在 8 到 30 個字元間、至少一個數字 (0-9)、至少一個小寫英文字母 (a-z)、至少一個大寫英文字母
	// (A-Z)、至少一個特殊字元(包括：! @ # $ % ^ & * ( ) _ + - = [ ] { } ; ' : " | , . < > / ?)。
	public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"|,.<>/?]).{8,30}$";

	public static final String PHONE_PATTERN = "^[0-9]{8,15}$";

	public static final String PARAM_EMPLOYED_ERROR = "Param employed error";

	public static final String GRADE_CANNOT_BE_NULL = "Grade cannot be null";

	// Attendance用:
	public static final String PARAMETER_YEAR_ERROR = "Parameter year error";

	public static final String PARAMETER_MONTH_ERROR = "Parameter month error";

	public static final String PARAMETER_START_TIME_ERROR = "Parameter start time error";

	public static final String PARAMETER_END_TIME_ERROR = "Parameter end time error";

	public static final String PARAMETER_DATE_ERROR = "Parameter date error";

	public static final String PARAMETER_DATE_TIME_ERROR = "Parameter date time error";

	public static final String PARAMETER_lEAVE_ID_ERROR = "Parameter leave_id error";

	public static final String PARAMETER_CERTIFICATE_ERROR = "Parameter certificate error";

	public static final String PARAMETER_CERTIFICATEFILETYPE_ERROR = "Parameter certificateFileType error";

	public static final String PARAMETER_STATUS_ERROR = "Parameter status error";

	public static final String PARAMETER_LEAVE_TYPE_ERROR = "Parameter leave type error";

	// Employee用:的req中也有使用到格式檢查的部分。
	// login登入系統用(我們使用的是:帳號-email信箱、密碼-(1)預設密碼(2)自設密碼。
	public static final String EMAIL_FORMAT_CHECK = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"; // 信箱格式檢查。

	public static final String PARAM_EMAIL_ERROR = "Param email error."; // 信箱錯誤訊息。

	// 密碼長度必須在 8 到 30 個字元間、至少一個數字 (0-9)、至少一個小寫英文字母 (a-z)、至少一個大寫英文字母
	// (A-Z)、至少一個特殊字元(包括：! @ # $ % ^ & * ( ) _ + - = [ ] { } ; ' : " | , . < > / ?)。
	public static final String PASSWORD_FORMAT_CHECK = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"|,.<>/?]).{8,30}$"; // 密碼格式檢查。

	public static final String PARAM_PASSWORD_ERROR = "Param password error."; // 密碼錯誤。

	// Salaries用:
	public static final String EMPLOYEE_ID_IS_NECESSARY = "Employee id is necessary.";

	public static final String SALARY_IS_NECESSARY = "Salary is necessary.";

	public static final String SALARY_CANNOT_BE_NEGATIVE = "Salary cannot be negative.";

	public static final String BONUS_IS_NECESSARY = "Bonus is necessary.";

	public static final String BONUS_CANNOT_BE_NEGATIVE = "Bonus cannot be negative.";

	public static final String PAYDATE_IS_NECESSARY = "Paydate is necessary.";

	public static final String PAYERID_IS_NECESSARY = "PayerId is necessary.";

	public static final String YEAR_IS_NECESSARY = "Year is necessary.";

	public static final String MONTH_IS_NECESSARY = "Month is necessary.";

	public static final String INVALID_MONTH = "Invalid month.";

	public static final String INVALID_DAY = "Invalid day.";

	public static final String PARAMETER_ID_ERROR = "Parameter id error";

	// CompanyInfo:
	public static final String NAME_REQUIRED = "Name required";

	public static final String TAX_ID_REQUIRED = "Tax id required";

	public static final String OWNER_NAME_REQUIRED = "Owner name required";

	public static final String PHONE_REQUIRED = "Phone required";

	public static final String EMAIL_REQUIRED = "Email required";

	public static final String ADDRESS_REQUIRED = "Address required";

	public static final String WEBSITE_REQUIRED = "Website required";

	public static final String ESTABLISHMENT_DATE_REQUIRED = "Establishment date required";

	public static final String CAPITAL_AMOUNT_REQUIRED = "Capital amount required";

	public static final String CAPITAL_AMOUNT_ERROR = "Capital amount error";

	public static final String EMPLOYEE_COUNT_REQUIRED = "Employee count required";

	public static final String STATUS_REQUIRED = "Status required";

	public static final String WORK_START_TIME_REQUIRED = "Work start time required";

	public static final String LUNCH_START_TIME_REQUIRED = "Lunch start time required";

	public static final String LUNCH_END_TIME_REQUIRED = "Lunch end time required";

	public static final String TIMEZONE_REQUIRED = "Timezone required";
}
