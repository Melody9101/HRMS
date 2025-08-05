package com.example.Human_Resource_Management_System_HRMS_.service.ifs;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceExceptionRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceSearchByDateReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceSearchByYYMMReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceSearchRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceUpdateReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.BasicRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.TodayAttendanceRes;

public interface AttendanceService {

	public BasicRes clockIn(LocalDateTime clockInTime);

	public BasicRes clockOut(LocalDateTime clockOutTime);

	public AttendanceRes selectByEmployeeIdAndDate(AttendanceSearchByDateReq attendanceSearchByDateReq);

	public AttendanceSearchRes selectAllAttendanceByDate(LocalDate date);

	public BasicRes updateClockTimeByEmployeeId(AttendanceUpdateReq attendanceUpdateReq);

	// 使用排程，在每個星期2-6的 00:05 分確認前一天打卡紀錄有異常的人
	public void checkAttendance();

	public AttendanceExceptionRes checkYesterdayAttendanceExceptionList();

	public AttendanceExceptionRes checkAttendanceExceptionListByEmployeeId(int employeeId);

	public AttendanceExceptionRes checkAllAttendanceExceptionList();

	public AttendanceSearchRes checkMonthlyAttendanceRecordsByEmployeeIdYYMM(AttendanceSearchByYYMMReq req);

	public AttendanceExceptionRes checkMonthlyAllExceptionAttendanceRecordsByYYMM(int year, int month);

	public TodayAttendanceRes getSessionEmployeeTodayAttendanceRecord();
}