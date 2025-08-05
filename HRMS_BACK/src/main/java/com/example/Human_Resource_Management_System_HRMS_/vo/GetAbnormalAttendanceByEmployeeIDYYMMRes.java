package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.util.List;

public class GetAbnormalAttendanceByEmployeeIDYYMMRes extends BasicRes {

	// 存放查詢到的異常出勤紀錄。
	List<AbnormalAttendanceVo> abnormalAttendanceList;

	public GetAbnormalAttendanceByEmployeeIDYYMMRes() {
		super();
	}

	public GetAbnormalAttendanceByEmployeeIDYYMMRes(int code, String message) {
		super(code, message);
	}

	public GetAbnormalAttendanceByEmployeeIDYYMMRes(int code, String message, List<AbnormalAttendanceVo> abnormalAttendanceList) {
		super(code, message);
		this.abnormalAttendanceList = abnormalAttendanceList;
	}

	public GetAbnormalAttendanceByEmployeeIDYYMMRes(List<AbnormalAttendanceVo> abnormalAttendanceList) {
		super();
		this.abnormalAttendanceList = abnormalAttendanceList;
	}

	public List<AbnormalAttendanceVo> getAbnormalAttendanceList() {
		return abnormalAttendanceList;
	}

	public void setAbnormalAttendanceList(List<AbnormalAttendanceVo> abnormalAttendanceList) {
		this.abnormalAttendanceList = abnormalAttendanceList;
	}
}
