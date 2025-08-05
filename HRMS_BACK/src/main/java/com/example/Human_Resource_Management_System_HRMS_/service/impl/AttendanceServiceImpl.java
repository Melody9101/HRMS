package com.example.Human_Resource_Management_System_HRMS_.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.Human_Resource_Management_System_HRMS_.constants.DepartmentType;
import com.example.Human_Resource_Management_System_HRMS_.constants.PositionType;
import com.example.Human_Resource_Management_System_HRMS_.constants.ResMessage;
import com.example.Human_Resource_Management_System_HRMS_.dao.AttendanceDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.CompanyInfoDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.EmployeeDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.LeaveRecordDao;
import com.example.Human_Resource_Management_System_HRMS_.dto.AttendanceDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.CompanyInfoDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.EmployeeDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.LeaveRecordDto;
import com.example.Human_Resource_Management_System_HRMS_.service.ifs.AttendanceService;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceExceptionRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceExceptionVo;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceSearchByDateReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceSearchByYYMMReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceSearchRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceUpdateReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceVo;
import com.example.Human_Resource_Management_System_HRMS_.vo.BasicRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.TodayAttendanceRes;

import jakarta.servlet.http.HttpSession;

@EnableScheduling
@Service
public class AttendanceServiceImpl implements AttendanceService {

	// logger 是用來記錄到日誌用的，可加可不加
	// import slf4j 的 Logger
	private Logger logger = LoggerFactory.getLogger(getClass());

	private List<AttendanceExceptionVo> cachedExceptionList = new ArrayList<>();

	@Autowired
	private AttendanceDao attendanceDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private LeaveRecordDao leaveRecordDao;

	@Autowired
	private CompanyInfoDao companyInfoDao;

//	@Autowired
//	private SseController sseController;

	@Autowired
	private EmailServiceImpl emailServiceImpl;

	// 方法1: 員工打上班卡
	@Override
	public BasicRes clockIn(LocalDateTime clockInTime) {
		// 確認有沒有帶入時間
		if (clockInTime == null) {
			// logger 是用來記錄到日誌用的，可加可不加
			// logger 還可以接收 try catch 拋出的紀錄
			// logger 要在 exception.GlobalExceptionHandler 裡面額外設定才可以接收在 req 裡定義的 valid
			// 拋出的錯誤訊息
			logger.info(ResMessage.CLOCK_TIME_ERROR.getMessage());

			return new BasicRes(ResMessage.CLOCK_TIME_ERROR.getCode(), //
					ResMessage.CLOCK_TIME_ERROR.getMessage());
		}
		// 設定今天時間
		LocalDateTime now = LocalDateTime.now();
		// 確認帶入時間是否在當前時間左右10秒，若超過會拋出「時間需為當前時間」訊息，防止使用者惡意攻擊(擅自亂帶入時間)
		if (clockInTime.isBefore(now.minusSeconds(10)) || clockInTime.isAfter(now.plusSeconds(10))) {
			return new BasicRes(ResMessage.CLOCK_TIME_MUST_BE_NOW.getCode(), //
					ResMessage.CLOCK_TIME_MUST_BE_NOW.getMessage());
		}

		// 取得當前登入者的員編
		int employeeId = getSessionEmployee().getId();

		// 取得&設定今天時間
		LocalDate today = LocalDate.now();
		LocalDateTime dayStart = today.atStartOfDay();
		LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();

		// 確認該員工今天的打卡紀錄
		List<AttendanceDto> list = attendanceDao.selectByEmployeeIdAndDate(employeeId, dayStart, dayEnd);
		AttendanceDto attendance = new AttendanceDto();

		// 因為取出的值有極少數機率因為網路延遲之類的 bug 導致資料出現兩筆以上，以防萬一只取當天第一筆資料比對
		if (list != null && !list.isEmpty()) {
			attendance = list.get(0);
		}

		// 這邊設計如果有打過上班卡/已打下班卡就不能再打上班卡，因此如果今天有紀錄，則確認需拋出哪種訊息
		if (attendance != null) {
			// 若已有上班紀錄，則拋出重複打卡訊息
			if (attendance.getClockIn() != null) {
				return new BasicRes(ResMessage.DUPLICATE_CLOCK_ERROR.getCode(),
						ResMessage.DUPLICATE_CLOCK_ERROR.getMessage());
			}
			// 若已有下班卡紀錄，則掏出打卡順序異常訊息
			if (attendance.getClockOut() != null) {
				return new BasicRes(ResMessage.CLOCK_SEQUENCE_ERROR.getCode(),
						ResMessage.CLOCK_SEQUENCE_ERROR.getMessage());
			}
		}

		// 若無當天紀錄，則新增上班打卡，並拋出打卡成功訊息
		attendanceDao.clockIn(employeeId, clockInTime);
		return new BasicRes(ResMessage.CLOCK_IN_SUCCESS.getCode(), ResMessage.CLOCK_IN_SUCCESS.getMessage());
	}

	// function2. 員工打下班卡
	public BasicRes clockOut(LocalDateTime clockOutTime) {
		// 確認有沒有帶入時間
		if (clockOutTime == null) {
			return new BasicRes(ResMessage.CLOCK_TIME_ERROR.getCode(), //
					ResMessage.CLOCK_TIME_ERROR.getMessage());
		}
		// 設定今天時間
		LocalDateTime now = LocalDateTime.now();
		// 確認帶入時間是否在當前時間左右10秒，若超過會拋出「時間需為當前時間」訊息，防止使用者惡意攻擊(擅自亂帶入時間)
		if (clockOutTime.isBefore(now.minusSeconds(10)) || clockOutTime.isAfter(now.plusSeconds(10))) {
			return new BasicRes(ResMessage.CLOCK_TIME_MUST_BE_NOW.getCode(), //
					ResMessage.CLOCK_TIME_MUST_BE_NOW.getMessage());
		}
		// 取得當前登入者的員編
		int employeeId = getSessionEmployee().getId();
		// 取得&設定今天時間
		LocalDate today = LocalDate.now();
		LocalDateTime dayStart = today.atStartOfDay();
		LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();

		// 確認該員工今天的打卡紀錄
		List<AttendanceDto> list = attendanceDao.selectByEmployeeIdAndDate(employeeId, dayStart, dayEnd);
		AttendanceDto attendance = new AttendanceDto();

		// 因為取出的值有極少數機率因為網路延遲之類的 bug 導致資料出現兩筆以上，以防萬一只取當天第一筆資料比對
		if (list != null && !list.isEmpty()) {
			attendance = list.get(0);
		}

		// 確認該員工今天的打卡紀錄
		if (attendance == null) {
			// 無當天考勤，直接新增下班打卡紀錄(會導致無法再打上班卡，因應前端要求)
			attendanceDao.clockOut(employeeId, clockOutTime);
		} else {
			// 若已有紀錄，則直接更新下班時間
			attendanceDao.clockOutById(attendance.getId(), clockOutTime);
		}
		// 拋出打卡成功訊息
		return new BasicRes(ResMessage.CLOCK_OUT_SUCCESS.getCode(), ResMessage.CLOCK_OUT_SUCCESS.getMessage());
	}

	// 方法3: 透過員編與時間取得特定時間的打卡資料
	@Override
	public AttendanceRes selectByEmployeeIdAndDate(AttendanceSearchByDateReq req) {
		// 取得當前登入者的員編
		int employeeId = req.getEmployeeId();

		// 若是帶入的開始時間比結束時間晚，則拋出時間範圍無效訊息
		if (req.getStartTime().isAfter(req.getEndTime())) {
			return new AttendanceRes(ResMessage.INVALID_TIME_RANGE.getCode(), //
					ResMessage.INVALID_TIME_RANGE.getMessage());
		}

		// 若是對象員工不存在，則拋出錯誤
		if (!checkEmployeeExists(employeeId)) {
			return new AttendanceRes(ResMessage.EMPLOYEE_NOT_FOUND.getCode(), //
					ResMessage.EMPLOYEE_NOT_FOUND.getMessage());
		}

		// 若是欲查詢的 id 不是登入者本人，則確認查詢者的職位
		EmployeeDto sessionEmployee = getSessionEmployee();
		if (sessionEmployee.getId() != employeeId) {
			// 若查詢者的部門不是人資部以及為一般員工時，拋出無權查詢訊息
			if (isEmployeeNotInHRDepartment(sessionEmployee)) {
				return new AttendanceRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
						ResMessage.GRADE_INSUFFICIENT.getMessage());
			}
		}
		// 設定使用者帶入日期的時間
		LocalDateTime startTime = req.getStartTime().atStartOfDay();
		LocalDateTime endTime = req.getEndTime().atTime(23, 59);

		// 取得紀錄
		List<AttendanceDto> attendanceRecordList = attendanceDao.selectByEmployeeIdAndDate(employeeId, startTime,
				endTime);
		List<AttendanceVo> attendanceVoList = new ArrayList<>();
		// 如果選擇的日期內沒有打卡紀錄，則拋出成功訊息，並拋出空陣列
		if (attendanceRecordList == null || attendanceRecordList.isEmpty()) {
			return new AttendanceRes(ResMessage.SUCCESS.getCode(), //
					ResMessage.SUCCESS.getMessage(), //
					attendanceVoList);
		}
		// 若是有紀錄，則將記錄整理
		attendanceVoList = changeAttendanceDtoListToAttendanceVoList(attendanceRecordList);

		// 拋出成功訊息以及整理過後的陣列
		return new AttendanceRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), //
				attendanceVoList);
	}

	// 方法4: 透過指定時間取得當日的所有打卡紀錄
	@Override
	public AttendanceSearchRes selectAllAttendanceByDate(LocalDate date) {
		// 取得當前登入者的資料
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 若是沒有帶入時間，則拋出無效日期格式訊息
		if (date == null) {
			return new AttendanceSearchRes(ResMessage.DATE_FORMAT_ERROR.getCode(), //
					ResMessage.DATE_FORMAT_ERROR.getMessage());
		}
		// 僅有老闆以及人資部門有權限查詢
		if (!sessionIsHrOrBoss(sessionEmployee)) {
			return new AttendanceSearchRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		// 設定帶入日期的時間
		LocalDateTime startTime = date.atStartOfDay();
		LocalDateTime endTime = date.atTime(23, 59);
		// 取得資料
		List<AttendanceDto> attendanceRecordList = attendanceDao.selectAllAttendanceByDate(startTime, endTime);
		List<AttendanceVo> attendanceVoList = new ArrayList<>();
		List<AttendanceExceptionVo> exceptionList = new ArrayList<>();

		// 若當日沒有打卡紀錄，則拋出成功訊息以及空陣列
		if (attendanceRecordList == null || attendanceRecordList.isEmpty()) {
			return new AttendanceSearchRes(ResMessage.SUCCESS.getCode(), //
					ResMessage.SUCCESS.getMessage(), //
					attendanceVoList, exceptionList);
		}
		// 若有資料，則將進行資料整理
		attendanceVoList = changeAttendanceDtoListToAttendanceVoList(attendanceRecordList);
		// 判斷異常紀錄
		exceptionList = checkException(attendanceVoList);
		// 拋出成功訊息以及整理過後的資料與判斷完畢的異常紀錄清單
		return new AttendanceSearchRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), //
				attendanceVoList, exceptionList);
	}

	// 方法5:透過打卡編號更新打卡紀錄
	@Override
	public BasicRes updateClockTimeByEmployeeId(AttendanceUpdateReq attendanceUpdateReq) {
		// 取得當前登入者的資料
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 若上下班時間皆欲更新，則確認當上班卡時間比下班卡時間晚時，拋出無效時間區間訊息
		if (attendanceUpdateReq.getClockInTime() != null && attendanceUpdateReq.getClockOutTime() != null) {
			if (attendanceUpdateReq.getClockInTime().isAfter(attendanceUpdateReq.getClockOutTime())) {
				return new BasicRes(ResMessage.INVALID_TIME_RANGE.getCode(), //
						ResMessage.INVALID_TIME_RANGE.getMessage());
			}
		}
		// 確認變更者的部門，僅有老闆或是人資部門才有權限改變
		if (!sessionIsHrOrBoss(sessionEmployee)) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		// 確認變更者的職位，僅有老闆或是人資主管有權限改變
		if (!sessionIsManagerOrBoss(sessionEmployee)) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		// 更新紀錄
		int successUpdateData = attendanceDao.updateClockTime(attendanceUpdateReq.getAttendanceId(), //
				attendanceUpdateReq.getClockInTime(), //
				attendanceUpdateReq.getClockOutTime(), //
				LocalDate.now(), //
				sessionEmployee.getId());

		// 若更新成功結果為0，則拋出查無打卡單編號訊息
		if (successUpdateData == 0) {
			return new BasicRes(ResMessage.RECORD_NOT_FOUND.getCode(), //
					ResMessage.RECORD_NOT_FOUND.getMessage());
		}
		// 拋出成功訊息
		return new BasicRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage());
	}

	// Controller6. 查詢昨日打卡異常清單
	@Override
	public AttendanceExceptionRes checkYesterdayAttendanceExceptionList() {
		// 取得當前登入者的資料
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 確認查詢者的職位，僅有老闆、各部門主管以及人資員工可以查詢
		if (isEmployeeNotInHRDepartment(sessionEmployee)) {
			return new AttendanceExceptionRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		// 成功訊息以及資料，資料使用下方排程來取得
		return new AttendanceExceptionRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), cachedExceptionList);
	}

	// Controller7. 查詢單一員工所有打卡異常紀錄
	@Override
	public AttendanceExceptionRes checkAttendanceExceptionListByEmployeeId(int employeeId) {
		// 取得當前登入者的資料
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 取得欲查詢對象的資料
		EmployeeDto target = employeeDao.selectByEmployeeId(employeeId);

		// 確認登入者的部門是否為一般部門
		boolean sessionEmployeeIsInGa = sessionEmployee.getDepartment()
				.equalsIgnoreCase(DepartmentType.GENERAL_AFFAIRS.getDepartmentName());

		// 若查詢對象不存在，則拋出查無員工訊息
		if (target == null) {
			return new AttendanceExceptionRes(ResMessage.EMPLOYEE_NOT_FOUND.getCode(), //
					ResMessage.EMPLOYEE_NOT_FOUND.getMessage());
		}
		// 若是欲查詢的 id 不是登入者本人，則確認查詢者的職位
		if (sessionEmployee.getId() != employeeId) {
			if (sessionEmployeeIsInGa) {
				// 當登入者為一般部門，且職位為一般員工時，拋出權限不足錯誤
				if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
					return new AttendanceExceptionRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
							ResMessage.GRADE_INSUFFICIENT.getMessage());
				} else {
					// 當登入者不為一般員工時，確認查詢對象的部門，若和登入者不同部門，則拋出權限不足錯誤
					if (!target.getDepartment().equalsIgnoreCase(sessionEmployee.getDepartment())) {
						return new AttendanceExceptionRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
								ResMessage.GRADE_INSUFFICIENT.getMessage());
					}
				}

			}
		}
		// 取得資料
		List<AttendanceDto> allAttendanceList = attendanceDao.selectByEmployeeIdAndDate(employeeId, //
				LocalDateTime.of(1, 1, 1, 0, 0), //
				LocalDateTime.of(9999, 12, 31, 23, 59));

		List<AttendanceExceptionVo> attendanceExceptionList = new ArrayList<>();
		// 若無資料，則拋出成功訊息以及空陣列
		if (allAttendanceList == null || allAttendanceList.isEmpty()) {
			return new AttendanceExceptionRes(ResMessage.SUCCESS.getCode(), //
					ResMessage.SUCCESS.getMessage(), attendanceExceptionList);
		}
		// 若有資料，則整理資料
		List<AttendanceVo> allrecord = changeAttendanceDtoListToAttendanceVoList(allAttendanceList);
		// 確認是否有異常紀錄
		attendanceExceptionList = checkException(allrecord);
		// 拋出成功訊息以及異常紀錄清單
		return new AttendanceExceptionRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), attendanceExceptionList);
	}

	// Controller8. 查詢所有員工所有打卡異常記錄
	@Override
	public AttendanceExceptionRes checkAllAttendanceExceptionList() {
		// 取得當前登入者的資料
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 確認查詢者的職位，僅有老闆、各部門主管以及人資員工可以查詢
		if (isEmployeeNotInHRDepartment(sessionEmployee)) {
			return new AttendanceExceptionRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		// 取得資料
		List<AttendanceVo> allAttendanceList = changeAttendanceDtoListToAttendanceVoList(//
				attendanceDao.selectAllAttendanceByDate(//
						LocalDateTime.of(1, 1, 1, 0, 0), //
						LocalDateTime.of(9999, 12, 31, 23, 59)));

		List<AttendanceExceptionVo> attendanceExceptionList = new ArrayList<>();
		// 若無資料，則拋出成功訊息以及空陣列
		if (allAttendanceList == null || allAttendanceList.isEmpty()) {
			return new AttendanceExceptionRes(ResMessage.SUCCESS.getCode(), //
					ResMessage.SUCCESS.getMessage(), attendanceExceptionList);
		}
		List<AttendanceExceptionVo> forfrontList = new ArrayList<>();
		// 確認異常清單
		attendanceExceptionList = checkException(allAttendanceList);
		// 若異常清單不為空陣列，則確認每筆異常紀錄的員工
		if (!attendanceExceptionList.isEmpty()) {
			for (AttendanceExceptionVo item : attendanceExceptionList) {
				// 當登入者的部門為一般部門，則當異常紀錄的員工為一般部門時才會加入顯示給前端的清單
				if (sessionEmployee.getDepartment()
						.equalsIgnoreCase(DepartmentType.GENERAL_AFFAIRS.getDepartmentName())) {
					if (item.getDepartment().equalsIgnoreCase(sessionEmployee.getDepartment())) {
						forfrontList.add(item);
					}
					// 當登入者不為一般部門，則將全部的異常紀錄加入顯示給前端的清單
				} else {
					forfrontList.add(item);
				}
			}
		}
		// 拋出成功訊息以及顯示給前端的清單
		return new AttendanceExceptionRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), forfrontList);

	}

	// Controller9. 透過年度與月份確認該名員工該月的所有打卡紀錄
	@Override
	public AttendanceSearchRes checkMonthlyAttendanceRecordsByEmployeeIdYYMM(AttendanceSearchByYYMMReq req) {
		// 取得當前登入者的資料
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 取得欲查詢對象的資料
		EmployeeDto target = employeeDao.selectByEmployeeId(req.getEmployeeId());

		// 若查詢對象不存在，則拋出查無員工訊息
		if (target == null) {
			return new AttendanceSearchRes(ResMessage.EMPLOYEE_NOT_FOUND.getCode(), //
					ResMessage.EMPLOYEE_NOT_FOUND.getMessage());
		}

		// 若是欲查詢的 id 不是登入者本人，則確認查詢者的職位
		if (sessionEmployee.getId() != req.getEmployeeId()) {
			// 當登入者為一般部門，並且為員工時會拋出權限不足錯誤
			if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.GENERAL_AFFAIRS.getDepartmentName())
					&& sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				return new AttendanceSearchRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
						ResMessage.GRADE_INSUFFICIENT.getMessage());
			}
			// 當登入者為員工，且對象不為員工時，則拋出權限不足錯誤
			if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())
					&& !target.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				return new AttendanceSearchRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
						ResMessage.GRADE_INSUFFICIENT.getMessage());
			}
		}

		// 將帶入的時間做成組件
		YearMonth yearMonth = YearMonth.of(req.getYear(), req.getMonth());

		// 若是查詢開始月份大於今日月份，則拋出時間不能為未來時間訊息
		if (yearMonth.isAfter(YearMonth.of(LocalDate.now().getYear(), LocalDate.now().getMonth()))) {
			return new AttendanceSearchRes(ResMessage.CANNOT_QUERY_FUTURE_MONTH.getCode(), //
					ResMessage.CANNOT_QUERY_FUTURE_MONTH.getMessage());
		}

		// 取得當月最後一天時間
		int lastDay = yearMonth.lengthOfMonth();
		LocalDateTime startTime = LocalDateTime.of(req.getYear(), req.getMonth(), 1, 0, 0, 0);
		LocalDateTime endTime = LocalDateTime.of(req.getYear(), req.getMonth(), lastDay, 23, 59, 59);

		// 取得資料
		List<AttendanceDto> targetAttendanceList = attendanceDao.selectByEmployeeIdAndDate(//
				req.getEmployeeId(), startTime, endTime);

		List<AttendanceVo> recordList = new ArrayList<>();
		List<AttendanceExceptionVo> exceptionList = new ArrayList<>();

		// 若無資料，則拋出成功訊息以及空陣列
		if (targetAttendanceList == null || targetAttendanceList.isEmpty()) {
			return new AttendanceSearchRes(ResMessage.SUCCESS.getCode(), //
					ResMessage.SUCCESS.getMessage(), recordList, exceptionList);
		}

		// 整理資料
		recordList = changeAttendanceDtoListToAttendanceVoList(targetAttendanceList);
		// 確認異常清單
		exceptionList = checkException(recordList);

		// 拋出成功訊息以及整理過後的資料與異常清單
		return new AttendanceSearchRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), recordList, exceptionList);
	}

	// Controller10. 透過年度與月份確認所有員工該月的所有打卡異常紀錄
	@Override
	public AttendanceExceptionRes checkMonthlyAllExceptionAttendanceRecordsByYYMM(int year, int month) {
		// 若輸入年份有誤，則拋出年份異常訊息
		if (year < 1) {
			return new AttendanceExceptionRes(ResMessage.PARAM_YEAR_ERROR.getCode(), //
					ResMessage.PARAM_YEAR_ERROR.getMessage());
		}
		// 若輸入的月份有誤，則拋出月份異常訊息
		if (month < 1 || month > 12) {
			return new AttendanceExceptionRes(ResMessage.PARAM_MONTH_ERROR.getCode(), //
					ResMessage.PARAM_MONTH_ERROR.getMessage());
		}

		// 取得當前登入者的資料
		EmployeeDto sessionEmployee = getSessionEmployee();

		// 若登入者的部門為一般部門，則拋出權限不足錯誤
		if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.GENERAL_AFFAIRS.getDepartmentName())) {
			return new AttendanceExceptionRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 將帶入的時間做成組件
		YearMonth yearMonth = YearMonth.of(year, month);

		// 若是查詢開始月份大於今日月份，則拋出時間不能為未來時間訊息
		if (yearMonth.isAfter(YearMonth.of(LocalDate.now().getYear(), LocalDate.now().getMonth()))) {
			return new AttendanceExceptionRes(ResMessage.CANNOT_QUERY_FUTURE_MONTH.getCode(), //
					ResMessage.CANNOT_QUERY_FUTURE_MONTH.getMessage());
		}

		// 取得當月最後一天時間
		int lastDay = yearMonth.lengthOfMonth();
		LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0, 0);
		LocalDateTime endTime = LocalDateTime.of(year, month, lastDay, 23, 59, 59);

		// 取得資料
		List<AttendanceDto> monthlyAllAttendanceList = attendanceDao.selectAllAttendanceByDate(//
				startTime, endTime);
		List<AttendanceExceptionVo> exceptionList = new ArrayList<>();

		// 若無資料，則拋出成功訊息與空陣列
		if (monthlyAllAttendanceList == null || monthlyAllAttendanceList.isEmpty()) {
			return new AttendanceExceptionRes(ResMessage.SUCCESS.getCode(), //
					ResMessage.SUCCESS.getMessage(), exceptionList);
		}
		List<AttendanceDto> filteredList = new ArrayList<>();

		for (AttendanceDto item : monthlyAllAttendanceList) {
			// 取得每筆紀錄的員工資料
			EmployeeDto target = employeeDao.selectByEmployeeId(item.getEmployeeId());
			// 當登入者為員工時，僅有對象同樣為員工時才會將紀錄加入顯示給前端的清單
			if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())
					&& target.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				filteredList.add(item);
			}
			// 當登入者為主管時，對象不為老闆時才會將紀錄加入顯示給前端的清單
			if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.MANAGER.getPositionName())
					&& !target.getPosition().equalsIgnoreCase(PositionType.BOSS.getPositionName())) {
				filteredList.add(item);
			}
			// 當登入者為老闆時，將所有紀錄加入顯示給前端的清單
			if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.BOSS.getPositionName())) {
				filteredList.add(item);
			}
		}
		// 整理資料
		List<AttendanceVo> recordList = changeAttendanceDtoListToAttendanceVoList(filteredList);
		// 確認異常紀錄
		exceptionList = checkException(recordList);

		// 拋出成功訊息以及異常紀錄清單
		return new AttendanceExceptionRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), exceptionList);
	}

	// Controller11. 取得登入帳號的當天打卡紀錄
	@Override
	public TodayAttendanceRes getSessionEmployeeTodayAttendanceRecord() {
		// 取得當前登入者的資料
		EmployeeDto sessionEmployee = getSessionEmployee();

		// 設定當天日期的時間
		LocalDateTime todayStart = LocalDate.now().atStartOfDay();
		LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);

		// 取得資料
		List<AttendanceDto> todayAttendanceRecord = attendanceDao.selectByEmployeeIdAndDate(//
				sessionEmployee.getId(), todayStart, todayEnd);

		AttendanceDto attendance = new AttendanceDto();

		// 因為取出的值有極少數機率因為網路延遲之類的 bug 導致資料出現兩筆以上，以防萬一只取當天第一筆資料比對
		if (todayAttendanceRecord != null && !todayAttendanceRecord.isEmpty()) {
			attendance = todayAttendanceRecord.get(0);
		}

		// 若無資料，則拋出成功訊息以及空資料
		if (attendance == null) {
			return new TodayAttendanceRes(ResMessage.SUCCESS.getCode(), //
					ResMessage.SUCCESS.getMessage(), null, null);
		}

		// 若有資料，則拋出成功訊息以及上下班紀錄時間
		return new TodayAttendanceRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), //
				todayAttendanceRecord.get(0).getClockIn(), //
				todayAttendanceRecord.get(0).getClockOut());
	}

	// Scheduled1.使用排程，在每個星期2-6的 00:05 分確認前一天打卡紀錄有異常的人，並且寄送郵件
	@Override
	@Scheduled(cron = "0 5 0 * * 2-6")
	public void checkAttendance() {

		LocalDate yesterday = LocalDate.now().minusDays(1);
		LocalDateTime yesterdayStartTime = yesterday.atStartOfDay();
		LocalDateTime yesterdayEndTime = yesterday.atTime(23, 59, 59);

		// 取出午休開始與結束時間
		CompanyInfoDto companyInfo = companyInfoDao.checkCompanyInfo();

		// 定義每天上班開始時間與結束時間
		LocalTime workStartTime = companyInfo.getWorkStartTime();
		LocalTime workEndTime = getWorkEndTime(companyInfo);
		LocalDateTime yesterdayWorkStartTime = yesterday.atTime(workStartTime);
		LocalDateTime yesterdayWorkEndTime = yesterday.atTime(workEndTime);

		List<AttendanceVo> yesterdayRecord = changeAttendanceDtoListToAttendanceVoList(
				attendanceDao.selectAllAttendanceByDate(yesterdayStartTime, yesterdayEndTime));

		if (yesterdayRecord == null || yesterdayRecord.isEmpty()) {
			return;
		}

		List<AttendanceExceptionVo> attendanceExceptionList = checkException(yesterdayRecord);

		// 加入異常判斷邏輯
		for (AttendanceExceptionVo attendance : attendanceExceptionList) {
			boolean isClockInNull = attendance.getClockIn() == null;
			boolean isClockOutNull = attendance.getClockOut() == null;

			// 若是有上班卡紀錄，卻沒有下班卡紀錄，則寄送打卡記錄異常信件
			if (isClockInNull || isClockOutNull) {
				String clockInTime = isClockInNull ? "無紀錄" : attendance.getClockIn().toString();
				String clockOutTime = isClockOutNull ? "無紀錄" : attendance.getClockOut().toString();

				String clockOutNullBody = String.format(//
						"您好，\n\n您於昨日 %s 打卡系統記錄如下：\n\n" + //
								"上班打卡時間：%s\n" + //
								"下班打卡時間：%s\n\n" + //
								"請向主管或人資部門申請補打卡。\n\n謝謝您。", //
						yesterday.toString(), //
						clockInTime, //
						clockOutTime);

				emailServiceImpl.sendVerificationCode(//
						employeeDao.selectByEmployeeId(attendance.getEmployeeId()).getEmail(), //
						"【打卡系統】昨日打卡異常通知", //
						clockOutNullBody);
			}

			if (attendance.getClockIn().isAfter(yesterdayWorkStartTime)
					|| attendance.getClockOut().isBefore(yesterdayWorkEndTime)) {
				String lateBody = String.format(//
						"您好，\n\n您於昨日 %s 打卡系統記錄如下：\n\n" + //
								"上班打卡時間：%s\n" + //
								"下班打卡時間：%s\n\n" + //
								"請向主管或人資部門申請請假或是申請修改時間，否則以遲到/早退紀錄。\n\n謝謝您。", //
						yesterday.toString(), //
						attendance.getClockIn().toString(), //
						attendance.getClockOut().toString());

				emailServiceImpl.sendVerificationCode(//
						employeeDao.selectByEmployeeId(attendance.getEmployeeId()).getEmail(), //
						"【打卡系統】昨日打卡異常通知", //
						lateBody);
			}

		}

		cachedExceptionList = attendanceExceptionList;

//		AttendanceExceptionRes res = new AttendanceExceptionRes(ResMessage.SUCCESS.getCode(),
//				ResMessage.SUCCESS.getMessage(), attendanceExceptionList);
//
//		// 發送資料給前端
//		sseController.sendToClients(res);

	}

//	public static boolean isHoliday(LocalDate date) {
//		// 檢查是否為週末
//		if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
//			return true;
//		}
//
//		// 定義固定節日
//		List<LocalDate> fixedHolidays = new ArrayList<>();
//		fixedHolidays.add(LocalDate.of(date.getYear(), Month.JANUARY, 1)); // 元旦
//		fixedHolidays.add(LocalDate.of(date.getYear(), Month.FEBRUARY, 28)); // 和平紀念日
//		fixedHolidays.add(LocalDate.of(date.getYear(), Month.APRIL, 4)); // 清明節
//		fixedHolidays.add(LocalDate.of(date.getYear(), Month.MAY, 1)); // 勞動節
//		fixedHolidays.add(LocalDate.of(date.getYear(), Month.AUGUST, 15)); // 中元節
//		fixedHolidays.add(LocalDate.of(date.getYear(), Month.SEPTEMBER, 28)); // 中秋節
//		fixedHolidays.add(LocalDate.of(date.getYear(), Month.OCTOBER, 10)); // 國慶日
//
//		if (fixedHolidays.contains(date)) {
//			return true;
//		}
//
//		return false;
//	}

	private Integer getSessionAccountId() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes == null) {
			return null; // 可能是非 web 請求，或者沒有 session
		}
		HttpSession session = attributes.getRequest().getSession();
		if (session == null) {
			return null;
		}
		return (Integer) session.getAttribute("accountId");
	}

	private EmployeeDto getSessionEmployee() {
		int sessionId = getSessionAccountId();

		return employeeDao.selectByEmployeeId(sessionId);
	}

	private boolean sessionIsHrOrBoss(EmployeeDto sessionEmployee) {

		if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())
				&& !sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
			return false;
		}
		return true;
	}

	private boolean sessionIsManagerOrBoss(EmployeeDto sessionEmployee) {

		if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
			return false;
		}
		return true;

	}

	private boolean isEmployeeNotInHRDepartment(EmployeeDto sessionEmployee) {
		// 如果是「員工職位」且部門不是「人資部門」，則返回 true
		if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())
				&& !sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())) {
			return true;
		}
		return false; // 否則返回 false
	}

	private List<AttendanceVo> changeAttendanceDtoListToAttendanceVoList(List<AttendanceDto> dtoList) {

		List<AttendanceVo> attendanceVoList = new ArrayList<>();

		List<Integer> employeeIdList = new ArrayList<>();

		for (AttendanceDto item : dtoList) {
			int id = item.getEmployeeId();
			if (!employeeIdList.contains(id)) {
				employeeIdList.add(id);
			}
		}

		List<EmployeeDto> employeeList = employeeDao.selectByEmployeeIdList(employeeIdList);

		// 建立 Map，方便快速查詢 Employee
		Map<Integer, EmployeeDto> employeeMap = new HashMap<>();

		for (EmployeeDto emp : employeeList) {
			employeeMap.put(emp.getId(), emp);
		}

		for (AttendanceDto item : dtoList) {
			EmployeeDto target = employeeMap.get(item.getEmployeeId());

			AttendanceVo vo = new AttendanceVo();

			vo.setEmployeeId(item.getEmployeeId());
			vo.setClockIn(item.getClockIn());
			vo.setClockOut(item.getClockOut());
			vo.setFinalUpdateDate(item.getFinalUpdateDate());
			vo.setFinalUpdateEmployeeId(item.getFinalUpdateEmployeeId());

			if (target != null) {
				vo.setEmployeeName(target.getName());
				vo.setDepartment(target.getDepartment());
			}

			attendanceVoList.add(vo);
		}

		return attendanceVoList;
	}

	private LocalTime getWorkEndTime(CompanyInfoDto companyInfo) {
		LocalTime lunchStartTime = companyInfo.getLunchStartTime();
		LocalTime lunchEndTime = companyInfo.getLunchEndTime();
		// 算出午休時間實際為幾小時幾分鐘
		long minutesSinceLunchTime = java.time.Duration.between(lunchStartTime, lunchEndTime).toMinutes();
		long hours = minutesSinceLunchTime / 60;
		long minutes = minutesSinceLunchTime % 60;

		LocalTime workStartTime = companyInfo.getWorkStartTime();
		LocalTime workEndTime = workStartTime.plusHours(8 + hours).plusMinutes(minutes);

		return workEndTime;

	}

	private List<AttendanceExceptionVo> checkException(List<AttendanceVo> checkItem) {
		List<AttendanceExceptionVo> exceptionList = new ArrayList<>();

		// 定義每天上班開始時間與結束時間
		CompanyInfoDto companyInfo = companyInfoDao.checkCompanyInfo();
		LocalTime workStartTime = companyInfo.getWorkStartTime();
		LocalTime workEndTime = getWorkEndTime(companyInfo);

		for (AttendanceVo item : checkItem) {
			List<String> reasonList = new ArrayList<>();

			boolean isClockInNull = item.getClockIn() == null;
			boolean isClockOutNull = item.getClockOut() == null;

			LocalDateTime itemClockTime = //
					item.getClockIn() != null ? item.getClockIn() //
							: item.getClockOut() != null ? item.getClockOut() //
									: null;

			LocalDateTime thatDayWorkStartTime = LocalDateTime.of(//
					itemClockTime.getYear(), //
					itemClockTime.getMonth(), //
					itemClockTime.getDayOfMonth(), //
					workStartTime.getHour(), //
					workStartTime.getMinute());

			LocalDateTime thatDayWorkEndTime = LocalDateTime.of(//
					itemClockTime.getYear(), //
					itemClockTime.getMonth(), //
					itemClockTime.getDayOfMonth(), //
					workEndTime.getHour(), //
					workEndTime.getMinute());

			LocalDateTime thatDayStart = thatDayWorkStartTime.toLocalDate().atStartOfDay();
			LocalDateTime thatDayEnd = thatDayWorkStartTime.toLocalDate().atTime(23, 59, 59);

			List<LeaveRecordDto> leaveRecords = leaveRecordDao.selectApprovedRecordByEmployeeIdAndYYMM(//
					item.getEmployeeId(), thatDayStart, thatDayEnd);

			if (isClockInNull) {
				boolean hasLeave = isOnLeaveAtTime(leaveRecords, thatDayWorkStartTime);
				if (!hasLeave) {
					reasonList.add("Missed clock in");
				}
			} else if (item.getClockIn().isAfter(thatDayWorkStartTime)) {
				boolean hasLeave = isLateTimeCoveredByLeave(leaveRecords, thatDayWorkStartTime, item.getClockIn());
				if (!hasLeave) {
					reasonList.add("Late arrival");
				}

			}

			if (isClockOutNull) {
				boolean hasLeave = isOnLeaveAtTime(leaveRecords, thatDayWorkEndTime);
				if (!hasLeave) {
					reasonList.add("Missed clock out");
				}

			} else if (item.getClockOut().isBefore(thatDayWorkEndTime)) {
				boolean hasLeave = isEarlyLeaveCoveredByLeave(leaveRecords, item.getClockOut(), thatDayWorkEndTime);
				if (!hasLeave) {
					reasonList.add("Leaving early");
				}

			}

			if (!reasonList.isEmpty()) {

				AttendanceExceptionVo vo = new AttendanceExceptionVo();
				vo.setEmployeeId(item.getEmployeeId());
				vo.setEmployeeName(item.getEmployeeName());
				vo.setDepartment(item.getDepartment());
				vo.setClockIn(item.getClockIn());
				vo.setClockOut(item.getClockOut());
				vo.setFinalUpdateDate(item.getFinalUpdateDate());
				vo.setFinalUpdateEmployeeId(item.getFinalUpdateEmployeeId());
				vo.setReasonList(reasonList);

				exceptionList.add(vo);
			}
		}
		return exceptionList;
	}

	// 判斷未打卡時是否有請假紀錄
	private boolean isOnLeaveAtTime(List<LeaveRecordDto> leaves, LocalDateTime checkTime) {
		for (LeaveRecordDto leave : leaves) {
			if ((leave.getStartTime().isBefore(checkTime) || leave.getStartTime().isEqual(checkTime))
					&& (leave.getEndTime().isAfter(checkTime) || leave.getEndTime().isEqual(checkTime))) {
				return true;
			}
		}
		return false;
	}

	// 判斷遲到時是否有請假紀錄
	private boolean isLateTimeCoveredByLeave(List<LeaveRecordDto> leaves, LocalDateTime scheduledStart,
			LocalDateTime actualClockIn) {
		if (!actualClockIn.isAfter(scheduledStart)) {
			return true; // 沒有遲到
		}

		for (LeaveRecordDto leave : leaves) {
			if ((leave.getStartTime().isBefore(scheduledStart) || leave.getStartTime().isEqual(scheduledStart))
					&& (leave.getEndTime().isAfter(actualClockIn) || leave.getEndTime().isEqual(actualClockIn))) {
				return true; // 遲到段被請假覆蓋
			}
		}

		return false;
	}

	// 判斷早退時是否有請假紀錄
	private boolean isEarlyLeaveCoveredByLeave(List<LeaveRecordDto> leaves, LocalDateTime actualClockOut,
			LocalDateTime scheduledEnd) {
		if (!actualClockOut.isBefore(scheduledEnd)) {
			return true; // 沒有早退
		}

		for (LeaveRecordDto leave : leaves) {
			if ((leave.getStartTime().isBefore(actualClockOut) || leave.getStartTime().isEqual(actualClockOut))
					&& (leave.getEndTime().isAfter(scheduledEnd) || leave.getEndTime().isEqual(scheduledEnd))) {
				return true; // 早退段被請假覆蓋
			}
		}

		return false;
	}

	private boolean checkEmployeeExists(int employeeId) {
		if (employeeDao.selectByEmployeeId(employeeId) == null) {
			return false;
		}
		return true;

	}

	/**
	 * 根據員工ID和指定日期範圍，獲取其考勤異常紀錄清單。
	 *
	 * 本方法負責整合資料查詢與異常判斷邏輯。 <br>
	 * 首先，它會從資料庫中查詢指定員工在給定時間範圍內的所有打卡紀錄 (AttendanceDto)。<br>
	 * 接著，它會將這些原始的打卡紀錄轉換為 AttendanceVo 格式，以便進行後續處理。<br>
	 * 最後，它會調用內部的 `checkException` 輔助方法，對轉換後的打卡紀錄進行詳細分析，<br>
	 * 判斷是否存在遲到、早退、或未打卡等考勤異常，並將已核准的請假紀錄納入考量，<br>
	 * 避免因合法請假而被誤判為異常。
	 *
	 * 這個方法提供了一個標準化的介面，用於檢索員工的月度考勤異常，<br>
	 * 是薪資計算或其他需要考勤異常數據服務的基礎。
	 *
	 * @param employeeId 欲查詢的員工ID。
	 * @param startDate  查詢的開始日期時間 (含)。
	 * @param endDate    查詢的結束日期時間 (含)。
	 * @return 包含該員工在指定期間內所有考勤異常的列表，若無異常則返回空列表。<br>
	 *         列表中每個元素為 {@link AttendanceExceptionVo}，詳細說明了異常類型與相關打卡時間。
	 */
	public Map<String, Object> getEmployeeAttendanceData(int employeeId, LocalDateTime startDate,
			LocalDateTime endDate) {
		// 查詢該日期區間中該位員工所有出勤紀錄清單。
		List<AttendanceDto> allMonthlyAttendanceRecords = attendanceDao.selectByEmployeeIdAndDate(employeeId, startDate,
				endDate);

		// 將 AttendanceDto 列表轉換為 AttendanceVo 列表，以便傳入 checkException
		List<AttendanceVo> attendanceVoList = changeAttendanceDtoListToAttendanceVoList(allMonthlyAttendanceRecords);

		// 調用 checkException 方法來獲取異常清單
		List<AttendanceExceptionVo> abnormalAttendanceRecords = checkException(attendanceVoList);

		Map<String, Object> result = new HashMap<>();
		result.put("allAttendanceRecords", allMonthlyAttendanceRecords);
		result.put("abnormalAttendanceRecords", abnormalAttendanceRecords);
		return result;
	}
}