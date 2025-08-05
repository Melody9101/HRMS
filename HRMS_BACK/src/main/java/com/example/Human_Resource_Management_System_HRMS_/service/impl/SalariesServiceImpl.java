package com.example.Human_Resource_Management_System_HRMS_.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.Human_Resource_Management_System_HRMS_.constants.DepartmentType;
import com.example.Human_Resource_Management_System_HRMS_.constants.LeaveType;
import com.example.Human_Resource_Management_System_HRMS_.constants.PositionType;
import com.example.Human_Resource_Management_System_HRMS_.constants.ResMessage;
import com.example.Human_Resource_Management_System_HRMS_.dao.CompanyInfoDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.EmployeeDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.LeaveRecordDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.SalariesDao;
import com.example.Human_Resource_Management_System_HRMS_.dto.AttendanceDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.CompanyInfoDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.EmployeeDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.LeaveRecordDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.SalariesDto;
import com.example.Human_Resource_Management_System_HRMS_.service.ifs.SalariesService;
import com.example.Human_Resource_Management_System_HRMS_.vo.AddSalariesReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.AttendanceExceptionVo;
import com.example.Human_Resource_Management_System_HRMS_.vo.BasicRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.CalculateMonthlySalaryRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.CalculateMonthlySalaryVo;
import com.example.Human_Resource_Management_System_HRMS_.vo.GetAllSalariesOrByEmployeeIdListRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.GetSalaryByEmployeeIdYYMMRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateSalariesByIdReq;

import jakarta.servlet.http.HttpSession;

@Service
public class SalariesServiceImpl implements SalariesService {

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private SalariesDao salariesDao;

	@Autowired
	private LeaveRecordDao leaveRecordDao;

	@Autowired
	private CompanyInfoDao companyInfoDao;

	@Autowired
	private AttendanceServiceImpl attendanceServiceImpl;
	
	/**
	 * 功能: 新增員工薪資單。
	 * 
	 * 權限: 僅會計部門員工、老闆可使用。
	 * 
	 * @param AddSalariesReq
	 * 
	 * @return
	 * GRADE_INSUFFICIENT: 權限不足。<br>
	 * EMPLOYEE_ID_NOT_FOUND: 查無員工ID。<br>
	 * SALARY_ALREADY_EXISTS: 薪資單已存在。<br>
	 * SUCCESS: 資料已成功儲存。
	 */
	@Override
	public BasicRes addSalaries(AddSalariesReq req) {
		// 1. 取得目前登入者的員工資料。
		EmployeeDto currentUser = getSessionEmployee();

		// 2. 檢查權限: 僅會計部門員工或老闆可使用，若不屬於則表示權限不足。
		if (!sessionIsAccountantOrBoss()) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 3. 查詢目前需增加薪資單的員工的基本資料。
		EmployeeDto target = employeeDao.selectByEmployeeId(req.getEmployeeId());

		// 4. 確認有無這位員工，若沒有查到就表示該員工ID不存在。
		if (target == null) {
			return new BasicRes(ResMessage.EMPLOYEE_ID_NOT_FOUND.getCode(),
					ResMessage.EMPLOYEE_ID_NOT_FOUND.getMessage());
		}

		// 5. 會計部門一般員工無法新增老闆和主管的薪資單，若檢查不通過表示操作權限不足。
		if (PositionType.EMPLOYEE.getPositionName().equalsIgnoreCase(currentUser.getPosition())) {
			if (PositionType.BOSS.getPositionName().equalsIgnoreCase(target.getPosition())
					|| PositionType.MANAGER.getPositionName().equalsIgnoreCase(target.getPosition())) {
				return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(),
						ResMessage.GRADE_INSUFFICIENT.getMessage());
			}
		}

		// 6. 會計部門一般員工無法新增自己本人的薪資單，若檢查不通過表示操作權限不足。
		if (!sessionIsAccountantManagerOrBoss() && target.getId() == currentUser.getId()) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 7. 因美年月份薪資單僅能有一份，故檢查薪資單是否重複，若重複就表示薪資單已存在。
		int count = salariesDao.countSalariesIdByEmpIdYYMM(req.getEmployeeId(), req.getYear(), req.getMonth());
		if (count > 0) {
			return new BasicRes(ResMessage.SALARY_ALREADY_EXISTS.getCode(),
					ResMessage.SALARY_ALREADY_EXISTS.getMessage());
		}

		// 8. 將輸入的資料寫入資料庫中，並顯示成功訊息。
		salariesDao.addSalaries(//
				req.getYear(), req.getMonth(), //
				req.getEmployeeId(), req.getSalary(), req.getBonus(), //
				LocalDate.now(), currentUser.getId());
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	/**
	 * 功能: 透過薪資單ID更新或修正已存在的薪資資料。
	 * 
	 * 権限: 僅會計部門主管可使用。
	 *
	 * @param UpdateSalariesByIdReq
	 * 
	 * @return 
	 * GRADE_INSUFFICIENT: 權限不足。<br>
	 * SUCCESS: 成功訊息。
	 */
	@Override
	public BasicRes updateSalariesById(UpdateSalariesByIdReq req) {
		// 1. 取得目前登入者的員工資料。
		EmployeeDto currentUser = getSessionEmployee();

		// 2. 檢查權限: 僅會計部門主管可使用，若不是表示權限不足。
		if (!sessionIsAccountingManager()) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 3. 更新薪資資料至資料庫中。
		salariesDao.updateSalariesById(//
				req.getId(), req.getSalary(), req.getBonus(), //
				LocalDate.now(), currentUser.getId());
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	/**
	 * 功能: 查詢指定員工指定期間的薪資單。
	 * 
	 * 權限: 僅會計部門、老闆可使用。
	 * (1) 若無輸入員工ID: 查詢全部員工資料。
	 * (2) 會計部門主管及老闆可查詢全部員工的薪資資料。
	 * 
	 * @param idList 將查詢的員工ID。
	 * @param year 指定年份。
	 * @param month 指定月份。
	 * 
	 * @return 
	 * salaries: 薪資。
	 * finalAuthorizedIds: 員工ID。
	 * 
	 * (1) 有查到: 成功訊息+薪資單+目前有權限查詢的員工ID。
	 * (2) 無查到: 未找到+空薪資列表+目前有權限查詢的員工ID。
	 */
	@Override
	public GetAllSalariesOrByEmployeeIdListRes getAllSalariesOrByEmployeeIdList(List<Integer> idList, int year,
			Integer month) {
		// 1. 為防止前端輸入錯誤，需檢查年是否為負數，若是負數表示年份錯誤。
		if (year < 1) {
			return new GetAllSalariesOrByEmployeeIdListRes(ResMessage.PARAM_YEAR_ERROR.getCode(), //
					ResMessage.PARAM_YEAR_ERROR.getMessage());
		}

		// 2. 為防止前端輸入錯誤，需檢查月份是否比1小或大於12，若是表示月份錯誤。
		// 因不能直接檢查month數值、會報錯，故需先檢查月份是否有帶入(為null)。
		if (month != null) {
			if (month < 1 || month > 12) {
				return new GetAllSalariesOrByEmployeeIdListRes(ResMessage.PARAM_MONTH_ERROR.getCode(), //
						ResMessage.PARAM_MONTH_ERROR.getMessage());
			}
		}

		// 3. 獲取目前登入者員工資料。
		EmployeeDto currentUser = getSessionEmployee();

		// 4. 權限檢查: 僅會計部門或老闆可使用，若不屬於表示權限不足。
		if (!sessionIsAccountantOrBoss()) {
			return new GetAllSalariesOrByEmployeeIdListRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 存放權限檢查後的員工ID。
		List<Integer> finalAuthorizedIds = new ArrayList<>();

		// 存放指定查詢ID的員工資料。
		List<EmployeeDto> employeesToProcess = new ArrayList<>();

		// 5. 分為兩種情況:
		// (1) 若無輸入員工ID: 查詢全部員工資料。
		// (2) 若有輸入員工ID: 查詢指定員工資料。
		if (idList == null || idList.isEmpty()) {
			employeesToProcess = employeeDao.getAllEmployees();
		} else {
			for (Integer id : idList) {
				EmployeeDto target = employeeDao.selectByEmployeeId(id);
				// 若查詢到的員工資料為空，表示該員工ID不存在。
				if (target == null) {
					return new GetAllSalariesOrByEmployeeIdListRes(ResMessage.EMPLOYEE_ID_NOT_FOUND.getCode(), //
							ResMessage.EMPLOYEE_ID_NOT_FOUND.getMessage());
				}
			}
			employeesToProcess = employeeDao.selectByEmployeeIdList(idList);
		}

		// 6. 權限檢查:
		// (1) 會計部門一般員工僅能查詢同職位的員工薪資資料。
		// (2) 會計部門主管及老闆可查詢全部員工的薪資資料。
		if (PositionType.EMPLOYEE.getPositionName().equalsIgnoreCase(currentUser.getPosition())) {
			for (EmployeeDto employee : employeesToProcess) {
				if (PositionType.EMPLOYEE.getPositionName().equalsIgnoreCase(employee.getPosition())) {
					finalAuthorizedIds.add(employee.getId());
				}
			}
		} else {
			for (EmployeeDto employee : employeesToProcess) {
				finalAuthorizedIds.add(employee.getId());
			}
		}

		// 7. 檢查ID清單是否為空，若為空表示目前輸入的ID皆無權限查看。
		if (finalAuthorizedIds.isEmpty()) {
			return new GetAllSalariesOrByEmployeeIdListRes(ResMessage.NOT_FOUND.getCode(),
					ResMessage.NOT_FOUND.getMessage(), new ArrayList<>(), finalAuthorizedIds);
		}

		// 8. 查詢有權限查閱的員工ID指定年月份的薪資單。
		List<SalariesDto> salaries = new ArrayList<>();
		salaries = salariesDao.getAllSalariesOrByEmployeeIdList(finalAuthorizedIds, year, month);

		// 9. 有可能全部員工在指定年月份時尚未有薪資紀錄，故先檢查是否有資料。
		// (1) 有查到: 成功訊息+薪資單+目前有權限查詢的員工ID。
		// (2) 無查到: 未找到+空薪資列表+目前有權限查詢的員工ID。
		if (salaries != null && !salaries.isEmpty()) {
			return new GetAllSalariesOrByEmployeeIdListRes(ResMessage.SUCCESS.getCode(),
					ResMessage.SUCCESS.getMessage(), salaries, finalAuthorizedIds);
		} else {
			return new GetAllSalariesOrByEmployeeIdListRes(ResMessage.NOT_FOUND.getCode(),
					ResMessage.NOT_FOUND.getMessage(), salaries, finalAuthorizedIds);
		}
	}

	/**
	 * 功能: 僅查詢目前登入者的指定年月份薪資資料。
	 * 
	 * 權限: 需登入。
	 *
	 * @param year 年份。
	 * @param month 月份。
	 * 
	 * @return 
	 * PARAM_YEAR_ERROR: 年份不得為負數。<br>
	 * PARAM_MONTH_ERROR: 月份不得小於1或大於12。<br>
	 * SUCCESS: 成功。<br>
	 * NOT_FOUND: 指定日期區間沒有找到薪資資訊。<br>
	 * salaries: 查詢到的指定年月薪資資料。
	 */
	@Override
	public GetSalaryByEmployeeIdYYMMRes getSalaryByEmployeeIdYYMM(int year, Integer month) {
		// 1. 為防止前端輸入錯誤，需檢查年是否為負數，若是負數表示年份錯誤。
		if (year < 1) {
			return new GetSalaryByEmployeeIdYYMMRes(ResMessage.PARAM_YEAR_ERROR.getCode(), //
					ResMessage.PARAM_YEAR_ERROR.getMessage());
		}

		// 2. 為防止前端輸入錯誤，需檢查月份是否比1小或大於12，若是表示月份錯誤。
		// 因不能直接檢查month數值、會報錯，故需先檢查月份是否有帶入(為null)。
		if (month != null) {
			if (month < 1 || month > 12) {
				return new GetSalaryByEmployeeIdYYMMRes(ResMessage.PARAM_MONTH_ERROR.getCode(), //
						ResMessage.PARAM_MONTH_ERROR.getMessage());
			}
		}

		// 3. 獲取目前登入者員工資料。
		EmployeeDto currentUser = getSessionEmployee();

		// 4. 查詢目前登入者的指定年月份薪資資料。
		SalariesDto salaries = salariesDao.getSalaryByEmployeeIdYYMM(currentUser.getId(), year, month);
		
		// 5. 確認有無查詢到薪資資料(salaries是否為空)。
		// (1) 有查到: 代表成功並輸出薪資資訊。
		// (2) 無查到: 代表沒查到。
		if (salaries != null) {
			return new GetSalaryByEmployeeIdYYMMRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(),
					salaries);
		} else {
			return new GetSalaryByEmployeeIdYYMMRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}
	}

	/**
	 * 取得目前登入者的員工ID。
	 */
	private Integer getSessionAccountId() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		
		if (attributes == null) {
			return null;
		}
		
		HttpSession session = attributes.getRequest().getSession();
		
		if (session == null) {
			return null;
		}
		return (Integer) session.getAttribute("accountId");
	}

	/**
	 * 利用session中儲存的員工ID獲取目前登入者的員工資料。
	 *
	 * @return EmployeeDto 目前登入者的員工資料(若尚未登入為null)。
	 */
	private EmployeeDto getSessionEmployee() {
		// 獲取目前登入者的員工ID。
		int sessionId = getSessionAccountId();
		// 查詢目前登入者的員工資料。
		return employeeDao.selectByEmployeeId(sessionId);
	}

	/**
	 * 功能: 確認是否為會計部門成員或老闆。
	 * 
	 * @return 
	 * true: 是會計部門成員或老闆。<br>
	 * false: 非會計部門成員或老闆、尚未登入。<br>
	 */
	private boolean sessionIsAccountantOrBoss() {
		// 1. 取得目前登入者的員工資料。
		EmployeeDto sessionEmployee = getSessionEmployee();

		// 2. 情況一: 尚未登入無法判斷-false。
		if (sessionEmployee == null) {
			return false;
		}

		// 3. 情況二: 判斷目前登入者是否為會計部門成員或老闆。
		// true: 屬於。false: 不屬於。
		if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.ACCOUNTANT.getDepartmentName())
				&& !sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 功能: 確認是否為會計部門主管或老闆。
	 * 
	 * @return
	 * true: 是會計部門主管或老闆。<br>
	 * false: 尚未登入、不是會計部門主管或老闆。<br>
	 */
	private boolean sessionIsAccountantManagerOrBoss() {
		// 1. 取得目前登入者的員工資料。
		EmployeeDto sessionEmployee = getSessionEmployee();

		// 2. 情況一: 尚未登入無法判斷-false。
		if (sessionEmployee == null) {
			return false;
		}

		// 3. 判斷目前登入者是否為會計部門主管。
		boolean isAccountantManager = sessionEmployee.getDepartment()
				.equalsIgnoreCase(DepartmentType.ACCOUNTANT.getDepartmentName())
				&& sessionEmployee.getPosition().equalsIgnoreCase(PositionType.MANAGER.getPositionName());

		// 4. 判斷目前登入者是否為老闆。
		boolean isBossDepartment = sessionEmployee.getDepartment()
				.equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName());

		// 5. 判斷部門:
		// true: 屬於其中一個部門。false: 都不屬於。
		return isAccountantManager || isBossDepartment;
	}

	/**
	 * 功能: 確認是否為會計部門主管。
	 * 
	 * @return 
	 * true: 是會計部門主管或老闆。<br>
	 * false: 尚未登入、不是會計部門主管或老闆。<br>
	 */
	private boolean sessionIsAccountingManager() {
		// 1. 取得目前登入者的員工資料。
		EmployeeDto sessionEmployee = getSessionEmployee();

		// 2. 情況一: 尚未登入無法判斷-false。
		if (sessionEmployee == null) {
			return false;
		}

		// 3. 情況二: 判斷目前登入者是否為會計部門主管。
		// true: 屬於。false: 不屬於。
		if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.ACCOUNTANT.getDepartmentName())
				&& sessionEmployee.getPosition().equalsIgnoreCase(PositionType.MANAGER.getPositionName())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 功能: 確認是否為會計部門成員。
	 * 
	 * @return 
	 * true: 是會計部門成員。<br>
	 * false: 尚未登入、不是會計部門成員。<br>
	 */
	private boolean sessionIsAccountant() {
		// 1. 取得目前登入者的員工資料。
		EmployeeDto sessionEmployee = getSessionEmployee();
		
		// 2. 情況一: 尚未登入無法判斷-false。
		if (sessionEmployee == null) {
			return false;
		}
		
		// 3. 情況二: 判斷目前登入者是否為會計部門成員。
		// true: 屬於。
		// false: 不屬於。
		if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.ACCOUNTANT.getDepartmentName())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 計算指定員工指定年月份薪資。<br>
	 * 
	 * 權限詳細：<br>
	 * (1) 此功能僅限會計部門成員使用。<br>
	 * (2) 會計部門一般員工（職位等級 1~5）無權計算主管及老闆（職位等級 6~10）薪資。<br>
	 * (3) 會計部門主管（職位等級 6~10）可以計算所有員工薪資。<br>
	 * 
	 * 注意事項:<br>
	 * 此方法僅為輔助薪資計算，需交由操作者確認後透過addSalaries寫入資料庫中。<br>
	 * 
	 * 未來優化:<br>
	 * 目前僅考慮週休二日情況，未來可視需求加入國定假日判斷在isWorkingDay方法當中。<br>
	 * 
	 * @param employeeId 需計算薪資的員工的員編。
	 * @param year       需計算薪資的年份。
	 * @param month      需計算薪資的月份。
	 * @param bonus      當月獎金。
	 *
	 * @return 
	 * GRADE_INSUFFICIENT: 權限不足。<br>
	 * EMPLOYEE_NOT_FOUND: 查無指定員工。<br>
	 * BONUS_ERROR: 獎金錯誤。<br>
	 * DATE_ERROR: 查詢日期不得為未來日期。<br>
	 * FAILED: 無法獲取公司資訊，造成無法計算。<br>
	 * SUCCESS: 薪資計算成功。
	 */
	@Override
	public CalculateMonthlySalaryRes calculateMonthlySalaryByEmployeeIdYYMMBonus(int employeeId, int year, int month,
			int bonus) {
		// --- 1. 操作者權限及參數檢查。 ---
		// 取得目前登入者的員工資料。
		EmployeeDto currentUser = getSessionEmployee();

		// 判斷目前登入者是否為會計部門員工，若不是代表權限不足。
		if (!sessionIsAccountant()) {
			return new CalculateMonthlySalaryRes(ResMessage.GRADE_INSUFFICIENT.getCode(),
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 取得目前需計算的指定員工ID的員工資料。
		EmployeeDto target = employeeDao.selectByEmployeeId(employeeId);
		// 判斷是否有查詢到指定員工，若target為null代表查無此員工。
		if (target == null) {
			return new CalculateMonthlySalaryRes(ResMessage.EMPLOYEE_NOT_FOUND.getCode(),
					ResMessage.EMPLOYEE_NOT_FOUND.getMessage());
		}

		// 目前操作者若為一般員工，因為權限不足，無法計算老闆或主管的薪資。
		if (PositionType.EMPLOYEE.getPositionName().equalsIgnoreCase(currentUser.getPosition())) {
			if (PositionType.BOSS.getPositionName().equalsIgnoreCase(target.getPosition())
					|| PositionType.MANAGER.getPositionName().equalsIgnoreCase(target.getPosition())) {
				return new CalculateMonthlySalaryRes(ResMessage.GRADE_INSUFFICIENT.getCode(),
						ResMessage.GRADE_INSUFFICIENT.getMessage());
			}
		}

		// 為防止前端輸入錯誤，檢查獎金是否為正數，若為負數代表獎金錯誤。
		if (bonus < 0) {
			return new CalculateMonthlySalaryRes(ResMessage.BONUS_ERROR.getCode(), ResMessage.BONUS_ERROR.getMessage());
		}

		// 因尚未有未來差勤、休假等計算薪資的必要資訊，故輸入年月份不得是未來日期，若為未來日期代表日期錯誤。
		LocalDate now = LocalDate.now(); // 取得現在的日期。
		YearMonth currentYearMonth = YearMonth.from(now); // 取得現在的年月 (例如: 2025-06)。
		YearMonth inputYearMonth = YearMonth.of(year, month); // 輸入的年月份 (例如: 2025-05)。
		if (inputYearMonth.isAfter(currentYearMonth) || inputYearMonth.equals(currentYearMonth)) {
			return new CalculateMonthlySalaryRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
		}

		// --- 2. 準備稍後查詢及計算薪資需使用的資料。 ---
		// 取得查詢月的第一天開始、最後一天結束。
		LocalDateTime startDateOfMonth = inputYearMonth.atDay(1).atStartOfDay();
		LocalDateTime endDateOfMonth = inputYearMonth.atEndOfMonth().atTime(LocalTime.MAX);

		// 取得指定員工的基本月薪。
		BigDecimal baseSalary = new BigDecimal(target.getSalaries());

		// 取得公司資料。
		CompanyInfoDto companyInfo = companyInfoDao.checkCompanyInfo();
		// 因若無公司資料會無法判斷，故不存在的情況會顯示失敗訊息。
		if (companyInfo == null) {
			return new CalculateMonthlySalaryRes(ResMessage.FAILED.getCode(), ResMessage.FAILED.getMessage());
		}

		// 上班開始時間。
		LocalTime companyWorkStartTime = companyInfo.getWorkStartTime();
		// 公司午休開始時間。
		LocalTime companyLunchStartTime = companyInfo.getLunchStartTime();
		// 公司午休結束時間。
		LocalTime companyLunchEndTime = companyInfo.getLunchEndTime();
		// 午休總時長。
		long lunchMinutes = Duration.between(companyLunchStartTime, companyLunchEndTime).toMinutes();
		// 下班時間 = 上班開始時間 + 8小時工作時間 + 午休時長。
		LocalTime companyWorkEndTime = companyWorkStartTime.plusHours(8).plusMinutes(lunchMinutes);
		
		// --- 3. 獲取指定員工指定年月出缺勤紀錄清單。 ---
		// 使用方法取得指定員工指定年月的全部出缺勤紀錄。
		Map<String, Object> attendanceData = attendanceServiceImpl.getEmployeeAttendanceData(employeeId,
				startDateOfMonth, endDateOfMonth);
		
		// 獲取指定年月指定員工全部的打卡紀錄(包含出勤異常)。
		@SuppressWarnings("unchecked")
		List<AttendanceDto> allMonthlyAttendanceRecords = (List<AttendanceDto>) attendanceData
				.get("allAttendanceRecords");
		
		// 獲取指定年月指定員工扣除已請假紀錄後的出勤異常清單(未請假的出勤異常)。
		@SuppressWarnings("unchecked")
		List<AttendanceExceptionVo> abnormalAttendanceRecords = (List<AttendanceExceptionVo>) attendanceData
				.get("abnormalAttendanceRecords");
		
		// 僅顯示指定月份期間指定員工出缺勤狀況，會計部門可根據公司規定決定此部分薪資計算方式。
		
		// --- 4. 計算指定員工休假扣除額及查詢請假紀錄清單。 ---
		// 計算當月休假總扣除額。
		BigDecimal totalDeductionForMonth = calculateApprovedMonthlyLeaveDeduction(employeeId, startDateOfMonth,
				endDateOfMonth, baseSalary, companyWorkStartTime, companyWorkEndTime, companyLunchStartTime,
				companyLunchEndTime);
		// 取得指定員工當月請假紀錄清單。
		List<LeaveRecordDto> allApprovedMonthlyLeaveRecords = leaveRecordDao
				.getApprovedMonthlyLeaveByEmpIdAndDate(employeeId, startDateOfMonth, endDateOfMonth);

		// --- 5. 12月時需將去年有薪年假轉為薪資。 ---
		// 儲存轉換薪資用的物件。
		BigDecimal annualLeaveConversionPay = BigDecimal.ZERO;
		
		// 僅輸入月份為12時需要換算。
		if (month == 12) {
			// 取得去年剩餘的帶薪年假天數。
			BigDecimal prevAnnualLeaveDays = target.getRemainingPreviousAnnualLeave();
			// 轉換薪資 = (基本月薪 / 30天) * 去年剩餘年假天數。
			// 為符合勞基法小數點採無條件進位處理。
			annualLeaveConversionPay = baseSalary.divide(new BigDecimal("30"), 2, RoundingMode.UP)
					.multiply(prevAnnualLeaveDays).setScale(0, RoundingMode.UP);
		}
		
		// --- 6. 最終薪資的計算。 ---
		// 當月月薪 = 員工基本月薪 - 休假扣薪 + 獎金 + 去年剩餘的年假天數折算成的薪資。
		BigDecimal finalCalculatedSalary = baseSalary.subtract(totalDeductionForMonth).add(new BigDecimal(bonus))
				.add(annualLeaveConversionPay);
		
		// --- 7. 將上述查詢、計算的薪資相關資料顯示給前端。 ---
		// 將查詢、計算的薪資相關資料儲存到salaryDetail變數中。
		CalculateMonthlySalaryVo salaryDetail = new CalculateMonthlySalaryVo(target.getId(), year, month, baseSalary,
				totalDeductionForMonth, bonus, annualLeaveConversionPay, target.getRemainingPreviousAnnualLeave(),
				finalCalculatedSalary, allMonthlyAttendanceRecords, abnormalAttendanceRecords,
				allApprovedMonthlyLeaveRecords);
		
		// 最後顯示成功訊息和薪資相關資料。
		return new CalculateMonthlySalaryRes( //
				ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), //
				salaryDetail);
	}
	
	/**
	 * 輔助方法
	 * 功能: 計算當月該員工全部已批准的請假紀錄的應扣金額。
	 * 
	 * @param employeeId       員工ID
	 * @param startDateOfMonth 查詢月份的第一天00:00:00
	 * @param endDateOfMonth   查詢月份的最後一天23:59:59
	 * @param baseSalary       員工基本月薪
	 * @param workStartTime    每天的上班開始時間
	 * @param workEndTime      每天的上班結束時間
	 * @param lunchStartTime   午休開始時間
	 * @param lunchEndTime     午休結束時間
	 * 
	 * @return BigDecimal totalDeductionForMonth: 當月已批准的休假需扣除的總金額。
	 */
	public BigDecimal calculateApprovedMonthlyLeaveDeduction(int employeeId, LocalDateTime startDateOfMonth,
			LocalDateTime endDateOfMonth, BigDecimal baseSalary, LocalTime workStartTime, LocalTime workEndTime,
			LocalTime lunchStartTime, LocalTime lunchEndTime) {
		// 1. 獲取指定員工指定月份全部已被批准的休假紀錄。
		List<LeaveRecordDto> allApprovedMonthlyLeaveRecords = leaveRecordDao
				.getApprovedMonthlyLeaveByEmpIdAndDate(employeeId, startDateOfMonth, endDateOfMonth);
		
		// 2. 存放已批准休假需扣除的總扣除金額，初始值為0。
		BigDecimal totalDeductionForMonth = BigDecimal.ZERO;
		
		// 3. 每小時扣薪金額 = 月薪 / 30天 / 8小時。
		BigDecimal hourlyRate = baseSalary.divide(new BigDecimal("30"), 8, RoundingMode.HALF_UP)
				.divide(new BigDecimal("8"), 8, RoundingMode.HALF_UP);

		// 4. 為確保下個步驟不報錯需先檢查allApprovedMonthlyLeaveRecords是否為null和空。
		if (allApprovedMonthlyLeaveRecords != null && !allApprovedMonthlyLeaveRecords.isEmpty()) {
			// 遍歷該員工該月休假紀錄，計算休假應扣除金額。
			for (LeaveRecordDto approvedLeave : allApprovedMonthlyLeaveRecords) {
				// 該筆請假紀錄的原始開始時間。
				LocalDateTime leaveOriginalStart = approvedLeave.getStartTime();
				// 該筆請假紀錄的原始結束時間。
				LocalDateTime leaveOriginalEnd = approvedLeave.getEndTime();

				// 5. 調整跨越休假的情況。
				// 請假紀錄開始時間在月份開始時間前採取月份開始時間，否則採取請假紀錄開始時間。
				LocalDateTime actualLeaveStartTimeInMonth = (leaveOriginalStart.isBefore(startDateOfMonth)) ?
						startDateOfMonth : leaveOriginalStart;
				// 請假紀錄結束時間在月份結束時間後採取月份結束時間，否則採取請假紀錄結束時間。
				LocalDateTime actualLeaveEndTimeInMonth = (leaveOriginalEnd.isAfter(endDateOfMonth)) ?
						endDateOfMonth : leaveOriginalEnd;

				// 6. 取得該筆休假紀錄實際的休假時間。
				BigDecimal actualTotalNetLeaveHours = calculateTotalLeaveHours(actualLeaveStartTimeInMonth,
						actualLeaveEndTimeInMonth, workStartTime, workEndTime, lunchStartTime, lunchEndTime);

				// 7. 若計算的實際休假時間小於等於0，就跳過這筆休假紀錄。
				if (actualTotalNetLeaveHours.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				// 8. 轉換該筆請假紀錄的類型為LeaveType，以便後續根據類型計算扣薪。
				LeaveType leaveType;
				try {
					leaveType = LeaveType.valueOf(approvedLeave.getLeaveType().toUpperCase());
				} catch (IllegalArgumentException e) {
					System.err.println("無法識別的請假類型字串: " + approvedLeave.getLeaveType() + ". 預設為OTHER類型。");
					leaveType = LeaveType.OTHER;
				}
				
				// 9. 存放單筆已批准休假需扣除的扣除金額，初始值為0。
				BigDecimal deductionForThisLeave = BigDecimal.ZERO;
				
				// 10. 根據休假類型計算扣除金額。
				switch (leaveType) {
				// 無薪資扣除的請假類型: 
				case ANNUAL: // 年假
				case MARRIAGE: // 結婚假（公司福利）。
				case BEREAVEMENT: // 喪假（公司福利）。
				case MATERNITY: // 產假（公司福利）。
				case PATERNITY: // 陪產假（公司福利）。
				case OFFICIAL: // 公假。
				case PAID_SICK_LEAVE: // 有薪病假（公司福利）。
					deductionForThisLeave = BigDecimal.ZERO;
					break;
				// 半額控除:
				case MENSTRUAL: // 生理假。
					// 該筆請假應扣金額 = 每小時薪資 * 0.5 * 實際工作時數。
					deductionForThisLeave = hourlyRate.multiply(new BigDecimal("0.5"))
							.multiply(actualTotalNetLeaveHours);
					break;
				// 全額控除:
				case SICK: // 無薪病假。
				case PERSONAL: // 事假。
				case OTHER: // 其他假。
					// 該筆請假應扣金額 = 每小時薪資 * 實際請假時數。
					deductionForThisLeave = hourlyRate.multiply(actualTotalNetLeaveHours);
					break;
				default: // 為了不報錯，採取以下處理方式：無薪資扣除。
					System.err.println("Switch 語句中觸發了未知的請假類型: " + leaveType + "，請檢查扣款邏輯！");
					deductionForThisLeave = BigDecimal.ZERO;
					break;
				}
				// 11. 當月總扣除金額 = 單筆請假紀錄扣款的全部加總。
				totalDeductionForMonth = totalDeductionForMonth.add(deductionForThisLeave);
			}
		}
		// 12. 將當月請假全部應扣除金額無條件捨去到整數。
		return totalDeductionForMonth.setScale(0, RoundingMode.DOWN);
	}

	/**
	 * 輔助方法
	 * 功能: 計算跨多日的總淨請假時數。
	 * 這個方法會逐日遍歷請假期間，並計算每一天在工作時間內應請假的時數，然後將其加總。
	 * 
	 * @param startOfPeriod  該筆請假的開始時間。
	 * @param endOfPeriod    該筆請假的結束時間。
	 * @param workStartTime  公司規定的上班時間。
	 * @param workEndTime    公司規定的下班時間。
	 * @param lunchStartTime 午休開始時間。
	 * @param lunchEndTime   午休結束時間。
	 * 
	 * @return BigDecimal totalNetLeaveHours: 跨多日總淨請假時數。
	 */
	private BigDecimal calculateTotalLeaveHours(LocalDateTime startOfPeriod, LocalDateTime endOfPeriod,
			LocalTime workStartTime, LocalTime workEndTime, LocalTime lunchStartTime, LocalTime lunchEndTime) {
		// 1. 初始化總淨請假時數為零。
		BigDecimal totalWorkingHours = BigDecimal.ZERO;
		
		// 2. 設定指定期間的開始日期。
		LocalDate currentDay = startOfPeriod.toLocalDate();
		
		// -- 3. 將一筆跨多日的請假紀錄，切割成每一天來獨立計算。 --
		// 逐日遍歷從開始日期到結束日期。
		while (!currentDay.isAfter(endOfPeriod.toLocalDate())) {
			// 判斷該日的計算起始時間。
			LocalDateTime dayStart;

			// 如果是請假期間的第一天，則使用請假的確切開始時間，否則從該日的工作開始時間算起。
			if (currentDay.isEqual(startOfPeriod.toLocalDate())) {
				dayStart = startOfPeriod;
			} else {
				dayStart = currentDay.atStartOfDay();
			}

			// 判斷該日的計算結束時間。
			LocalDateTime dayEnd;
			
			// 如果是請假期間的最後一天，則使用請假的確切結束時間，否則計算到該日的工作結束時間為止。
			if (currentDay.isEqual(endOfPeriod.toLocalDate())) {
				dayEnd = endOfPeriod;
			} else {
				dayEnd = currentDay.atTime(LocalTime.MAX);
			}

			// 計算該日指定範圍內有效的淨請假時數（會排除週末、非工作時段、午休）。
			BigDecimal dailyHours = CalculateSingleApplicationNetLeaveHours(dayStart, dayEnd, workStartTime, workEndTime,
					lunchStartTime, lunchEndTime);
			
			// 將該日淨請假時數累加到總時數。
			totalWorkingHours = totalWorkingHours.add(dailyHours);

			// 前進到下一天。
			currentDay = currentDay.plusDays(1);
		}
		
		// 4. 最後輸出計算出的總淨請假時數。
		return totalWorkingHours;
	}
	
	/**
	 * 輔助方法
	 * 功能: 計算單筆請假區間內總淨請假時數。
	 * 目前僅考慮週休二日、每天上下班時間、午休等情況。
	 * 
	 * @param startOfPeriod  當筆請假的開始時間。
	 * @param endOfPeriod    當筆請假的結束時間。
	 * @param workStartTime  公司規定的上班時間。
	 * @param workEndTime    公司規定的下班時間。
	 * @param lunchStartTime 公司規定的午休開始時間（例：12:00）。
	 * @param lunchEndTime   公司規定的午休結束時間（例：13:00）。
	 * 
	 * @return 
	 * BigDecimal.ZERO: (1)非上班日。(2)時間錯誤。
	 * BigDecimal netDuration: 單筆請假區間內總淨請假時數。(四捨五入到小數第二位)。
	 */
	private BigDecimal CalculateSingleApplicationNetLeaveHours(LocalDateTime startOfPeriod, LocalDateTime endOfPeriod,
			LocalTime workStartTime, LocalTime workEndTime, LocalTime lunchStartTime, LocalTime lunchEndTime) {
		// -- 1. 如果是非上班日，則該日沒有有效的請假時數。 --
		if (!isWorkingDay(startOfPeriod.toLocalDate())) {
			return BigDecimal.ZERO;
		}
		
		// -- 2. 判斷請假當日實際工作開始及結束時間。 --
		// 設定當天標準工作開始時間。
		LocalDateTime dayStartWork = startOfPeriod.toLocalDate().atTime(workStartTime);
		// 設定當天標準工作結束時間。
		LocalDateTime dayEndWork = startOfPeriod.toLocalDate().atTime(workEndTime);
		
		// 判斷請假開始時間:
		// 若 實際請假開始時間 晚於或等於 標準上班時間，取實際請假開始時間，若不成立取標準上班時間。
		LocalDateTime actualPeriodStart = (startOfPeriod.isAfter(dayStartWork) || startOfPeriod.isEqual(dayStartWork)) ?
				startOfPeriod : dayStartWork;
		
		// 實際請假結束時間 早於或等於 標準下班時間，取實際請假結束時間，若不成立取標準下班時間。
		LocalDateTime actualPeriodEnd = (endOfPeriod.isBefore(dayEndWork) || endOfPeriod.isEqual(dayEndWork)) ?
				endOfPeriod : dayEndWork;
		
		// 若實際上班開始時間 晚於或等於 實際上班結束時間，表示時間錯誤(以 無工時 處理)。
		if (actualPeriodStart.isAfter(actualPeriodEnd) || actualPeriodStart.isEqual(actualPeriodEnd)) {
			return BigDecimal.ZERO;
		}
		
		// -- 3. 計算請假當天工作時數與午休時數重疊時長。 --
		// 初始化午休重疊時長為 0。
		Duration lunchBreakOverlapDuration = Duration.ZERO;
		
		// 設定當天的標準午休開始時間。
		LocalDateTime lunchStart = startOfPeriod.toLocalDate().atTime(lunchStartTime);
		
		// 設定當天的標準午休結束時間。
		LocalDateTime lunchEnd = startOfPeriod.toLocalDate().atTime(lunchEndTime);
		
		// 若工作開始時間早於午休結束時間 且 工作結束時間早於午休開始時間。 
		if (actualPeriodStart.isBefore(lunchEnd) && actualPeriodEnd.isAfter(lunchStart)) {
			
			// 重疊開始時間：取請假開始時間與午休開始時間中較晚者。
			LocalDateTime overlapStart = (actualPeriodStart.isAfter(lunchStart) ? actualPeriodStart : lunchStart);
			
			// 重疊結束時間：取請假結束時間與午休結束時間中較早者。
			LocalDateTime overlapEnd = (actualPeriodEnd.isBefore(lunchEnd) ? actualPeriodEnd : lunchEnd);
			
			// 如果重疊的開始時間早於結束時間，則計算實際重疊時長。
			if (overlapStart.isBefore(overlapEnd)) {
				lunchBreakOverlapDuration = Duration.between(overlapStart, overlapEnd);
			}
		}
		
		// -- 4. 最後計算單筆請假區間內的淨請假時數。 --
		// 計算請假區間在工作時間內的總時長（未扣除午休）。
		Duration grossDuration = Duration.between(actualPeriodStart, actualPeriodEnd);
		
		// 總淨請假時數 = 請假總時長 - 午休重疊時長。
		Duration netDuration = grossDuration.minus(lunchBreakOverlapDuration);
		
		// 將計算出的淨請假時數從毫秒轉換為小時，並四捨五入到小數點後兩位。
		return BigDecimal.valueOf(netDuration.toMillis()).divide(BigDecimal.valueOf(1000 * 60 * 60), 2,
				RoundingMode.HALF_UP);
	}
	
	/**
	 * 輔助方法
	 * 
	 * 功能: 判斷當天是否為上班日。
	 * 
	 * **目前僅判斷是否為週休二日，後續優化可在此加入國定假日判斷。**
	 * 
	 * @param date 請假日期。
	 * @return 
	 * 上班日: true。休息日: false。
	 */
	private boolean isWorkingDay(LocalDate date) {
		// true: 上班日。false: 週休二日。
		if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
			return false;
		}
		return true;
	}
}