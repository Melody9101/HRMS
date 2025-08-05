package com.example.Human_Resource_Management_System_HRMS_.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Human_Resource_Management_System_HRMS_.service.ifs.AttendanceService;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceExceptionRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceSearchByDateReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceSearchByYYMMReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceSearchRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceUpdateReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.BasicRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.TodayAttendanceRes;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 釋出給前端的打卡系統 RESTful 架構
 */
@CrossOrigin
@RestController
@Tag(name = "打卡系統")
public class AttendanceController {

	@Autowired
	private AttendanceService attendanceService;

	/**
	 * Controller1. 員工打上班卡<br>
	 * API 的路徑: http://localhost:8080/HRMS/clockIn?clockTime=
	 * 
	 * @param attendaceReq
	 * @return
	 */
	@PostMapping(value = "HRMS/clockIn")
	public BasicRes clockIn(@RequestParam("clockInTime") LocalDateTime clockInTime) {
		return attendanceService.clockIn(clockInTime);
	}

	/**
	 * Controller2. 員工打下班卡<br>
	 * 
	 * @param clockOutTime
	 * @return
	 */
	@PostMapping(value = "HRMS/clockOut")
	public BasicRes clockOut(@RequestParam("clockOutTime") LocalDateTime clockOutTime) {
		return attendanceService.clockOut(clockOutTime);
	}

	/**
	 * Controller3. 透過員編與時間取得特定時間的打卡資料<br>
	 * 若帶入的id並非本人，則僅有老闆、各部門主管以及人資部門員工有權限查詢<br>
	 * API 的路徑: http://localhost:8080/HRMS/getAttendanceByEmployeeIdAndDate
	 * 
	 * @param attendanceSearchByDateReq
	 * @return
	 */
	@PostMapping(value = "HRMS/getAttendanceByEmployeeIdAndDate")
	public AttendanceRes getAttendanceByEmployeeIdAndDate(
			@Valid @RequestBody AttendanceSearchByDateReq attendanceSearchByDateReq) {
		return attendanceService.selectByEmployeeIdAndDate(attendanceSearchByDateReq);
	}

	/**
	 * Controller4. 透過指定時間取得當日的所有打卡紀錄<br>
	 * 僅有老闆以及人資部門有權限查詢<br>
	 * API 的路徑: http://localhost:8080/HRMS/getAllAttendanceByDate?date=
	 * 
	 * @param date
	 * @return
	 */
	@PostMapping(value = "HRMS/getAllAttendanceByDate")
	public AttendanceSearchRes getAllAttendanceByDate(@RequestParam("date") LocalDate date) {
		return attendanceService.selectAllAttendanceByDate(date);
	}

	/**
	 * Controller5. 透過打卡編號更新打卡紀錄<br>
	 * req內有三個參數，分別為id，上班卡時間，下班卡時間<br>
	 * id不能小於0<br>
	 * 上班時間或是下班時間若沒有要改，記得參數要帶null<br>
	 * 僅有老闆以及人資主管有權限更改<br>
	 * API 的路徑: http://localhost:8080/HRMS/updateClockTimeByAttendanceId
	 * 
	 * @param attendanceUpdateReq
	 * @return
	 */
	@PostMapping(value = "HRMS/updateClockTimeByAttendanceId")
	public BasicRes updateClockTimeByAttendanceId(@Valid @RequestBody AttendanceUpdateReq attendanceUpdateReq) {
		return attendanceService.updateClockTimeByEmployeeId(attendanceUpdateReq);
	}

	/**
	 * Controller6. 查詢昨日打卡異常清單(未打下班卡、遲到、早退)<br>
	 * 僅有老闆、各部門主管以及人資部門員工有權限查詢<br>
	 * API 的路徑: http://localhost:8080/HRMS/checkYesterdayAttendanceExceptionList
	 * 
	 * @return
	 */
	@GetMapping(value = "HRMS/checkYesterdayAttendanceExceptionList")
	public AttendanceExceptionRes checkYesterdayAttendanceExceptionList() {
		return attendanceService.checkYesterdayAttendanceExceptionList();
	}

	/**
	 * Controller7. 查詢單一員工所有遲到、早退、未打下班卡紀錄<br>
	 * 若非本人，僅有人資部門以及老闆有權限查詢<br>
	 * API 的路徑: http://localhost:8080/HRMS/checkAttendanceExceptionListByEmployeeId
	 * 
	 * @param employeeId
	 * @return
	 */
	@PostMapping(value = "HRMS/checkAttendanceExceptionListByEmployeeId")
	public AttendanceExceptionRes checkAttendanceExceptionListByEmployeeId(@RequestParam("employeeId") int employeeId) {
		return attendanceService.checkAttendanceExceptionListByEmployeeId(employeeId);
	}

	/**
	 * Controller8. 查詢所有員工所有遲到、早退、未打下班卡記錄<br>
	 * 僅有老闆、各部門主管以及人資部門有權限可以使用<br>
	 * API 的路徑: http://localhost:8080/HRMS/checkAllAttendanceExceptionList
	 * 
	 * @return
	 */
	@GetMapping(value = "HRMS/checkAllAttendanceExceptionList")
	public AttendanceExceptionRes checkAllAttendanceExceptionList() {
		return attendanceService.checkAllAttendanceExceptionList();
	}

	/**
	 * Controller9. 查詢該名員工該月的所有打卡與異常紀錄<br>
	 * 若非本人，一般部門的一般職員沒辦法使用此 API<br>
	 * API 的路徑:
	 * http://localhost:8080/HRMS/checkMonthlyAttendanceRecordsByEmployeeIdYYMM
	 * 
	 * @param req
	 * @return
	 */
	@PostMapping(value = "HRMS/checkMonthlyAttendanceRecordsByEmployeeIdYYMM")
	public AttendanceSearchRes checkMonthlyAttendanceRecordsByEmployeeIdYYMM(
			@RequestBody AttendanceSearchByYYMMReq req) {
		return attendanceService.checkMonthlyAttendanceRecordsByEmployeeIdYYMM(req);
	}

	/**
	 * Controller10. 透過年度與月份確認所有員工該月的所有打卡異常紀錄<br>
	 * 一般部門的員工沒辦法使用此 API<br>
	 * API 的路徑:
	 * http://localhost:8080/HRMS/checkMonthlyAllExceptionAttendanceRecordsByYYMM?year=00&month=00
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	@PostMapping(value = "HRMS/checkMonthlyAllExceptionAttendanceRecordsByYYMM")
	public AttendanceExceptionRes checkMonthlyAllExceptionAttendanceRecordsByYYMM(//
			@RequestParam("year") int year, //
			@RequestParam("month") int month) {
		return attendanceService.checkMonthlyAllExceptionAttendanceRecordsByYYMM(year, month);
	}

	/**
	 * Controller11. 取得登入帳號的當天打卡紀錄<br>
	 * API 的路徑: http://localhost:8080/HRMS/getSessionEmployeeTodayAttendanceRecord
	 * 
	 * @return
	 */
	@GetMapping(value = "HRMS/getSessionEmployeeTodayAttendanceRecord")
	public TodayAttendanceRes getSessionEmployeeTodayAttendanceRecord() {
		return attendanceService.getSessionEmployeeTodayAttendanceRecord();
	}
}
