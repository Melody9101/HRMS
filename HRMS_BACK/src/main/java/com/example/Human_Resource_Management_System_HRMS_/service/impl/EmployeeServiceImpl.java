package com.example.Human_Resource_Management_System_HRMS_.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.Human_Resource_Management_System_HRMS_.constants.ApplicationType;
import com.example.Human_Resource_Management_System_HRMS_.constants.ClockType;
import com.example.Human_Resource_Management_System_HRMS_.constants.DepartmentAndPosition;
import com.example.Human_Resource_Management_System_HRMS_.constants.DepartmentType;
import com.example.Human_Resource_Management_System_HRMS_.constants.PositionType;
import com.example.Human_Resource_Management_System_HRMS_.constants.ResMessage;
import com.example.Human_Resource_Management_System_HRMS_.dao.AttendanceDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.EmailVerificationDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.EmployeeDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.EmployeeApplicationDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.EmployeeApplicationRecordDao;
import com.example.Human_Resource_Management_System_HRMS_.dto.AttendanceDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.EmailVerificationDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.EmployeeDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.EmployeeApplicationDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.EmployeeApplicationRecordDto;
import com.example.Human_Resource_Management_System_HRMS_.exception.ApplicationException;
import com.example.Human_Resource_Management_System_HRMS_.service.ifs.EmployeeService;
import com.example.Human_Resource_Management_System_HRMS_.vo.AddEmployeeReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.ApproveEmployeeApplicationReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.BasicRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.EmployeeBasicInfoRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.EmployeeBasicInfoVo;
import com.example.Human_Resource_Management_System_HRMS_.vo.EmployeeInfoVo;
import com.example.Human_Resource_Management_System_HRMS_.vo.EmployeeListRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.EmployeeRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.EmployeeResignationReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.LoginReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.OvertimeApplicationReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.ReinstatementEmployeeReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.SearchEmployeeByEmailAndPhoneReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.SubmitEmployeeApplicationReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateClockTimeReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateEmployeeBasicInfoReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateEmployeeInfoApplicationVo;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateEmployeeJobReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateEmployeePasswordReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateInfoApplicationRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdatePwdByEmailReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.updateEmployeeUnpaidLeaveReq;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@EnableScheduling
@Service
public class EmployeeServiceImpl implements EmployeeService {

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	// logger 是用來記錄到日誌用的，可加可不加
	// import slf4j 的 Logger
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final ObjectMapper mapper;

	public EmployeeServiceImpl(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private EmployeeApplicationDao employeeApplicationDao;

	@Autowired
	private EmployeeApplicationRecordDao employeeApplicationRecordDao;

	@Autowired
	private EmailVerificationDao emailVerificationDao;

	@Autowired
	private AttendanceDao attendanceDao;

	@Autowired
	private EmailServiceImpl emailServiceImpl;
	
	/**
	 * 方法1-addEmployee: 新增員工資訊。
	 * 僅老闆、人資部門有權新增員工。
	 * 
	 * 檢查:
	 * 1. 電子信箱Email不可重複。
	 * 2. 部門有效性檢查。
	 * 3. 職等有效性檢查。
	 * 4. position和grade對應的判斷。
	 */
	@Override
	public BasicRes addEmployee(AddEmployeeReq req) {
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 確認新增者的單位，僅有老闆以及人資部門可以新增
		if (!sessionIsHrOrBoss(sessionEmployee)) {
			// logger 是用來記錄到日誌用的，可加可不加
			// logger 還可以接收 try catch 拋出的紀錄
			// logger 要在 exception.GlobalExceptionHandler 裡面額外設定才可以接收在 req 裡定義的 valid
			// 拋出的錯誤訊息
			logger.info(ResMessage.GRADE_INSUFFICIENT.getMessage());
			
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 1. 電子郵件重複性檢查
		if (employeeDao.countByEmail(req.getEmail()) != 0) {
			return new BasicRes(ResMessage.EMAIL_ALREADY_EXISTS.getCode(), //
					ResMessage.EMAIL_ALREADY_EXISTS.getMessage());
		}

		// 2. 部門有效性檢查 (使用 DepartmentType)。
		if (!DepartmentType.isValidDepartment(req.getDepartment())) {
			return new BasicRes(ResMessage.INVALID_DEPARTMENT.getCode(), ResMessage.INVALID_DEPARTMENT.getMessage());
		}

		// 3. 職等有效性檢查 (使用 PositionType)。
		String position = req.getPosition();
		if (!PositionType.isValidPosition(position)) {
			return new BasicRes(ResMessage.INVALID_POSITION.getCode(), ResMessage.INVALID_POSITION.getMessage());
		}

		// 4. position和grade對應的判斷。
		boolean isValidCombination = positionIsValidCombination(position, req.getGrade());
		if (!isValidCombination) {
			return new BasicRes(ResMessage.POSITION_GRADE_MISMATCH.getCode(),
					ResMessage.POSITION_GRADE_MISMATCH.getMessage());
		}

		// 5. 將輸入的值用employeeDao.addEmployee存入資料庫中，並回傳SUCCESS成功訊息。
		try {
			employeeDao.addEmployee(req.getDepartment(), req.getName(), req.getEmail(), //
					encoder.encode(req.getEmail()), req.getPhone(), req.getGender(), //
					req.getGrade(), req.getEntryDate(), req.getSalaries(), //
					true, req.getPosition(), LocalDate.now(), //
					getSessionEmployee().getId());
			
			return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
		} catch (Exception e) {
			return new BasicRes(ResMessage.FAILED.getCode(), ResMessage.FAILED.getMessage());
		}
	}
	
	/**
	 * 方法2-selectByEmployeeIdList: 透過員編查詢員工資料。
	 * 
	 */
	@Override
	public EmployeeListRes selectByEmployeeIdList(List<Integer> idList) {
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 1. 若idList為null 或 為空isEmpty 回傳PARAM_ID_LIST_ERROR。
		if (idList == null || idList.isEmpty()) {
			return new EmployeeListRes(ResMessage.PARAM_ID_LIST_ERROR.getCode(), //
					ResMessage.PARAM_ID_LIST_ERROR.getMessage());
		}
		
		// 2. 若輸入的 id 小於、等於 0 就排除不進行查詢並回傳id錯誤訊息。
		for (Integer id : idList) {
			if (id <= 0) {
				return new EmployeeListRes(ResMessage.PARAM_ID_ERROR.getCode(), ResMessage.PARAM_ID_ERROR.getMessage());
			}
		}
		
		// 3. 當查詢者為一般部門、一般員工時，限制僅可查詢自己的資料。
		if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())//
				&& sessionEmployee.getDepartment()
						.equalsIgnoreCase(DepartmentType.GENERAL_AFFAIRS.getDepartmentName())) {
			// 因只可查詢自己的資料idList長度應為1，若大於1回傳權限不足。
			if (idList.size() > 1) {
				return new EmployeeListRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
						ResMessage.GRADE_INSUFFICIENT.getMessage());
			}
			// getSessionEmployee().getId()目前登入者ID應與idList.get(0)相同才代表是同一位員工，若不同則回傳權限不足。
			if (getSessionEmployee().getId() != idList.get(0)) {
				return new EmployeeListRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
						ResMessage.GRADE_INSUFFICIENT.getMessage());
			}
		}
		
		// 4. 將查詢到的員工資料存放到 employeeList 中，資料型態為 List<Employee>。
		List<EmployeeDto> employeeList = employeeDao.selectByEmployeeIdList(idList);
		
		// 5. 創建空列表employeeInfoVoList用以當employeeList 為null 或 為空 時的回傳資料。
		List<EmployeeInfoVo> employeeInfoVoList = new ArrayList<>();
		
		// 6. 判斷是否有查詢到資料，若employeeList 為null 或 為空 時回傳SUCCESS訊息、空的employeeInfoVoList清單。
		if (employeeList == null || employeeList.isEmpty()) {
			return new EmployeeListRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), //
					employeeInfoVoList);
		}
		
		// 7. 判斷查詢者與被查詢者的職位與權限。
		for (EmployeeDto item : employeeList) {
			// 當查詢者職等為一般員工，但查詢對象職等不為一般員工，即回傳GRADE_INSUFFICIENT無權限查詢。
			if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				if (!item.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
					return new EmployeeListRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
							ResMessage.GRADE_INSUFFICIENT.getMessage());
				}
			}
			
			// 當查詢者職等為主管階級。
			if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.MANAGER.getPositionName())) {
				// 當查詢者部門為一般部門，若查詢者和查詢對象部門不同，回傳權限不足。
				if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.GENERAL_AFFAIRS.getDepartmentName())
						&& !item.getDepartment().equalsIgnoreCase(sessionEmployee.getDepartment())) {
					return new EmployeeListRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
							ResMessage.GRADE_INSUFFICIENT.getMessage());
				}
				// 當查詢對象為老闆時回傳權限不足。
				if (item.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
					return new EmployeeListRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
							ResMessage.GRADE_INSUFFICIENT.getMessage());
				}
			}
			// 將過濾後的查詢資料item加到employeeInfoVoList當中。
			employeeInfoVoList.add(changeToFrontData(item));
		}
		// 回傳成功訊息、過濾後的查詢資料employeeInfoVoList。
		return new EmployeeListRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), //
				employeeInfoVoList);
	}
	
	/**
	 * 方法3-updateEmployeeBasicInfo: 透過員編編輯員工基本資訊。
	 * 僅老闆及人資部門有權更新員工資料。
	 * 
	 * 其他限制:
	 * 1. 一般員工無法編輯主管職等以上資料。
	 * 2. 若不為老闆不可更改老闆資料。
	 * 3. 因為帳號為Email信箱，因此Email資料也不能為已存在信箱。
	 */
	@Override
	public BasicRes updateEmployeeBasicInfo(UpdateEmployeeBasicInfoReq req) {
		// 1. 
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 2. 確認變更者的單位，僅有老闆以及人資部門可以更改。
		if (!sessionIsHrOrBoss(sessionEmployee)) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		
		// 3. target為目前欲更新的員工資料。
		EmployeeDto target = employeeDao.selectByEmployeeId(req.getId());
		
		// 4. 先確定是否有輸入員編 id 的資料。
		if (target == null || !target.getEmployed()) {
			return new BasicRes(ResMessage.EMPLOYEE_NOT_FOUND.getCode(), //
					ResMessage.EMPLOYEE_NOT_FOUND.getMessage());
		}
		
		// 5. 確認變更者的職位，若變更人不為人資主管以上的階級，不得變更主管的資料。
		if (!sessionIsManagerOrBoss(sessionEmployee)) {
			if (!target.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
						ResMessage.GRADE_INSUFFICIENT.getMessage());
			}
		}
		
		// 6. 若變更人不為老闆，不能更改老闆的資料
		if (!getSessionEmployee().getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
			if (target.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
				return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
						ResMessage.GRADE_INSUFFICIENT.getMessage());
			}
		}

		// 7. 確認輸入的 email 沒有和資料庫中的 email 重複。
		if (!req.getEmail().equals(target.getEmail())) {
			if (employeeDao.countByEmail(req.getEmail()) != 0) {
				return new BasicRes(ResMessage.EMAIL_ALREADY_EXISTS.getCode(), //
						ResMessage.EMAIL_ALREADY_EXISTS.getMessage());
			}
		}
		
		// 8. 編輯員工基本資訊、回傳成功訊息。
		int updateSuccessCount = employeeDao.updateEmployeeBasicInfo(req.getId(), req.getName(), req.getEmail(), //
				req.getPhone(), req.getGender(), LocalDate.now(), //
				getSessionEmployee().getId());
		if (updateSuccessCount != 1) {
			return new BasicRes(ResMessage.FAILED.getCode(), ResMessage.FAILED.getMessage());
		}
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}
	
	/**
	 * 方法4-updateEmployeeJob: 透過員編編輯員工職等、調部門和薪資資訊。
	 * 僅老闆及人資主管有權編輯。
	 * 
	 * 其他限制:
	 * 1. 僅老闆可編輯自己資訊。
	 * 
	 * 檢查:
	 * 1. 部門名稱有效性檢查。
	 * 2. 職等有效性檢查。
	 * 3. 透過positionIsValidCombination方法進行position和grade對應的判斷。
	 */
	@Override
	public BasicRes updateEmployeeJob(UpdateEmployeeJobReq req) {
		EmployeeDto sessionEmployee = getSessionEmployee();

		// 1. 確認變更者的單位，僅有老闆以及人資部門可以更改
		if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())
				&& !sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 2. 確認變更者的職位，若變更人不為人資主管以上的階級，則拋出錯誤
		if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		
		EmployeeDto target = employeeDao.selectByEmployeeId(req.getId());

		// 3. 先確定是否有輸入員編 id 的資料。
		if (target == null || !target.getEmployed()) {
			return new BasicRes(ResMessage.EMPLOYEE_NOT_FOUND.getCode(), //
					ResMessage.EMPLOYEE_NOT_FOUND.getMessage());
		}
		
		// 4. 若變更人不為老闆，不能更改老闆的資料
		if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
			if (target.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
				return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
						ResMessage.GRADE_INSUFFICIENT.getMessage());
			}
		}

		// 5. 部門名稱 需進行有效性檢查 (使用 DepartmentType)。
		if (!DepartmentType.isValidDepartment(req.getDepartment())) {
			return new BasicRes(ResMessage.INVALID_DEPARTMENT.getCode(), //
					ResMessage.INVALID_DEPARTMENT.getMessage());
		}

		// 6. 職等 有效性檢查 (使用 PositionType)。
		String position = req.getPosition();
		if (!PositionType.isValidPosition(position)) {
			return new BasicRes(ResMessage.INVALID_POSITION.getCode(), ResMessage.INVALID_POSITION.getMessage());
		}

		// 7. position和grade對應的判斷。
		boolean isValidCombination = positionIsValidCombination(position, req.getGrade());
		if (!isValidCombination) {
			return new BasicRes(ResMessage.POSITION_GRADE_MISMATCH.getCode(),
					ResMessage.POSITION_GRADE_MISMATCH.getMessage());
		}

		// 8. 透過員編編輯員工職等、調部門和薪資資訊。
		int updateSuccessCount = employeeDao.updateEmployeeJob(req.getId(), req.getDepartment(), req.getGrade(), //
				req.getSalaries(), req.getPosition(), LocalDate.now(), //
				getSessionEmployee().getId());
		
		// 9. 若updateSuccessCount不為1代表編輯失敗，為1代表編輯成功。
		if (updateSuccessCount != 1) {
			return new BasicRes(ResMessage.FAILED.getCode(), ResMessage.FAILED.getMessage());
		}
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}
	
	/**
	 * 方法5-resignEmployee: 透過員編處理員工離職。
	 * 僅人資部門主管、老闆可以使用。
	 * 
	 * 其他限制:
	 * 僅老闆可編輯自己資訊。
	 * 離職日期不得早於入職日期。離職日期僅能輸入 入職日當天 或 入職日後的未來日期。
	 */
	@Override
	@Transactional
	public BasicRes resignEmployee(EmployeeResignationReq req) {
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 1. 確認變更者的單位，僅有老闆以及人資部門可以更改
		if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())
				&& !sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		// 2. 確認變更者的職位，只有人資主管以上的階級才有辦法使用此 API
		if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		// 3. 查詢該位欲編輯員工的全部資料。
		EmployeeDto target = employeeDao.selectByEmployeeId(req.getId());
		// 4. 先確定是否有輸入員編 id 的資料。
		if (target == null || !target.getEmployed()) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}
		// 5. 若變更人不為老闆，不能更改老闆的資料
		if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
			if (target.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
				return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
						ResMessage.GRADE_INSUFFICIENT.getMessage());
			}
		}
		// 6. 離職日期不得早於入職日期，離職日期僅能輸入 入職日當天 或 入職日後的未來日期。
		if (req.getResignationDate().isBefore(target.getEntryDate())) {
			return new BasicRes(ResMessage.RESIGNATION_DATE_ERROR.getCode(),
					ResMessage.RESIGNATION_DATE_ERROR.getMessage());
		}
		// 7. 透過員編處理員工離職資訊。
		int updateSuccessCount = employeeDao.resignEmployee(req.getId(), req.getResignationDate(),
				req.getResignationReason(), //
				LocalDate.now(), getSessionEmployee().getId());
		// 8. 若updateSuccessCount不為1代表離職失敗，為1代表離職成功。
		if (updateSuccessCount != 1) {
			return new BasicRes(ResMessage.FAILED.getCode(), ResMessage.FAILED.getMessage());
		}
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}
	
	/**
	 * 方法6-searchEmployeeByEmailAndPhone: 透過信箱以及手機查詢已離職員工資料。
	 * 僅老闆及人資部門有權查詢。
	 */
	@Override
	public EmployeeRes searchEmployeeByEmailAndPhone(SearchEmployeeByEmailAndPhoneReq req) {
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 1. 確認變更者的單位，僅有老闆以及人資部門可以查詢
		if (!sessionIsHrOrBoss(sessionEmployee)) {
			return new EmployeeRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		
		// 2. 透過信箱及手機查詢已離職員工資料。
		EmployeeDto employeeInfoData = employeeDao.selectByEmailAndPhone(req.getEmail(), req.getPhone());
		
		// 3. 若employeeInfoData為null代表無查詢到離職員工。
		if (employeeInfoData == null) {
			return new EmployeeRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}
		
		// 4. 若有查詢到離職員工則回傳成功訊息、employeeInfoData離職員工資訊。
		// 透過changeToFrontData方法將查詢到的已離職員工資料EmployeeDto employeeInfoData轉為EmployeeInfoVo。
		return new EmployeeRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), //
				changeToFrontData(employeeInfoData));
	}
	
	/**
	 * 方法7-reinstatementEmployee: 員工復職。
	 * 僅人資部門及老闆有權讓員工復職。
	 * 
	 * 檢查:
	 * 1. 部門有效性檢查。
	 * 2. 職等有效性檢查。
	 * 3. 透過positionIsValidCombination方法進行position和grade對應的判斷。
	 */
	@Override
	public BasicRes reinstatementEmployee(ReinstatementEmployeeReq req) {
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 1. 僅人資部門及老闆有權讓員工復職，若非屬這兩個部門則回傳GRADE_INSUFFICIENT權限不足。
		if (!sessionIsHrOrBoss(sessionEmployee)) {
			return new EmployeeRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		
		// 2. 部門有效性檢查 (使用 DepartmentType)。
		if (!DepartmentType.isValidDepartment(req.getDepartment())) {
			return new BasicRes(ResMessage.INVALID_DEPARTMENT.getCode(), ResMessage.INVALID_DEPARTMENT.getMessage());
		}
		
		// 3. 職等有效性檢查 (使用 PositionType)。
		String position = req.getPosition();
		if (!PositionType.isValidPosition(position)) {
			return new BasicRes(ResMessage.INVALID_POSITION.getCode(), ResMessage.INVALID_POSITION.getMessage());
		}
		
		// 4. position和grade對應的判斷。
		boolean isValidCombination = positionIsValidCombination(position, req.getGrade());
		if (!isValidCombination) {
			return new BasicRes(ResMessage.POSITION_GRADE_MISMATCH.getCode(),
					ResMessage.POSITION_GRADE_MISMATCH.getMessage());
		}
		
		// 5. selectByEmployeeId: 透過輸入的ID查詢目前正在操作復職的員工的資料。
		EmployeeDto employee = employeeDao.selectByEmployeeId(req.getEmployeeId());
		
		// 6. 若employee為空代表無查到員工資訊NOT_FOUND，可能是輸錯或根本沒輸入ID。
		if (employee == null) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}
		
		// 7. 創建空的EmployeeDto reinstatementEmployee物件。
		EmployeeDto reinstatementEmployee = new EmployeeDto();
		// 8. 將輸入的各個req資料放入reinstatementEmployee當中。
		reinstatementEmployee.setId(req.getEmployeeId());
		reinstatementEmployee.setDepartment(req.getDepartment());
		reinstatementEmployee.setEmail(req.getEmail());
		reinstatementEmployee.setPassword(encoder.encode(req.getEmail())); // 預設密碼為信箱，密碼需加密。
		reinstatementEmployee.setPhone(req.getPhone());
		reinstatementEmployee.setGrade(req.getGrade());
		reinstatementEmployee.setEntryDate(req.getReinstatementDate());
		reinstatementEmployee.setResignationDate(null);
		reinstatementEmployee.setResignationReason(null);
		reinstatementEmployee.setSalaries(req.getSalaries());
		reinstatementEmployee.setPosition(req.getPosition());
		reinstatementEmployee.setEmployed(true);
		reinstatementEmployee.setRemainingPreviousAnnualLeave(null);
		reinstatementEmployee.setRemainingCurrentAnnualLeave(null);
		reinstatementEmployee.setRemainingPaidSickLeave(null);
		reinstatementEmployee.setUnpaidLeaveStartDate(null);
		reinstatementEmployee.setUnpaidLeaveEndDate(null);
		reinstatementEmployee.setUnpaidLeaveReason(null);
		reinstatementEmployee.setFinalUpdateDate(LocalDate.now());
		reinstatementEmployee.setFinalUpdateEmployeeId(getSessionEmployee().getId());
		// 9. 將員工復職資訊寫入資料庫中。
		int updateSuccessCount = employeeDao.reinstatementEmployee(reinstatementEmployee);
		// 10. 若updateSuccessCount不為1代表操作失敗，若為1代表操作成功。
		if (updateSuccessCount != 1) {
			return new BasicRes(ResMessage.FAILED.getCode(), ResMessage.FAILED.getMessage());
		}
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}
	
	/**
	 * 方法8-updateEmployeePassword: 透過Email信箱(帳號)更新員工密碼。
	 */
	@Override
	public BasicRes updateEmployeePassword(UpdateEmployeePasswordReq req) {
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 1. email格式、oldPassword格式 及 newPassword格式 均已在 UpdateEmployeePasswordReq 檢查完畢。
		// 2. 確認 email 是否存在，若為0代表email不存在無法進行更新密碼。
		if (employeeDao.countByEmail(req.getEmail()) == 0) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}
		
		// 3. 確認req.getEmail()輸入的Email是否與sessionEmployee.getEmail()目前登入者Email相同(是否為本人)，若不為本人無法更新密碼、回傳權限不足。
		if (!req.getEmail().equalsIgnoreCase(sessionEmployee.getEmail())) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		
		// 4. 比對 輸入的密碼 和 資料庫中已加密過的密碼 是否相符。
		boolean checkPassword = encoder.matches(req.getOldPassword(), sessionEmployee.getPassword());
		// 若checkPassword為false回傳密碼錯誤。
		if (!checkPassword) {
			return new BasicRes(ResMessage.PASSWORD_ERROR.getCode(), ResMessage.PASSWORD_ERROR.getMessage());
		}
		
		// 5. 對輸入的新密碼進行加密。
		String encryptNewPwd = encoder.encode(req.getNewPassword());
		
		// 6. 透過 email 更新員工密碼，將 加密後的新密碼 更新到資料庫中。
		int updateSuccessCount = employeeDao.updateEmployeePassword(req.getEmail(), encryptNewPwd);
		// 若updateSuccessCount不為1代表操作失敗。
		if (updateSuccessCount != 1) {
			return new BasicRes(ResMessage.FAILED.getCode(), ResMessage.FAILED.getMessage());
		}
		// 若updateSuccessCount為1代表操作成功。
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}
	
	/**
	 * 方法9-updatePwdByEmail: 忘記密碼。
	 * 需先驗證Email信箱，若無先驗證會回傳VERIFICATION_FAILED驗證失敗。
	 */
	@Override
	public BasicRes updatePwdByEmail(UpdatePwdByEmailReq req) {
		// 1. 透過輸入的Email查詢目前驗證狀態EmailVerificationDto。
		EmailVerificationDto verificationData = emailVerificationDao.selectByEmail(req.getEmail());
		
		// 2. 檢查資料，若是驗證狀態還是 false 代表尚未驗證，調用錯 API
		if (!verificationData.isVerified()) {
			return new BasicRes(ResMessage.VERIFICATION_FAILED.getCode(), //
					ResMessage.VERIFICATION_FAILED.getMessage());
		}
		
		// 3. 若已驗證過Email則進行密碼更新，updateEmployeePassword透過Email更新密碼。
		int updateSuccessCount = employeeDao.updateEmployeePassword(req.getEmail(), encoder.encode(req.getNewPassword()));
		
		// 4. 若updateSuccessCount不為1代表更新失敗FAILED。
		if (updateSuccessCount != 1) {
			return new BasicRes(ResMessage.FAILED.getCode(), ResMessage.FAILED.getMessage());
		}
		
		// 5. 若updateSuccessCount為1代表更新成功SUCCESS。
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}
	
	/**
	 * 方法10-updateEmployeeUnpaidLeaveById: 更新員工無薪假、留職停薪等資訊。
	 * 僅老闆及人資部門主管有權更改。
	 * 老闆資訊僅老闆可變更。
	 */
	@Override
	public BasicRes updateEmployeeUnpaidLeaveById(updateEmployeeUnpaidLeaveReq req) {
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 1. 僅老闆及人資部門有權更改。
		if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())
				&& !sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		
		// 2. 若登入者為人資部門一般員工則無權使用此API做更改。
		if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		
		// 3. 使用輸入的ID查詢欲編輯員工的資料target。
		EmployeeDto target = employeeDao.selectByEmployeeId(req.getId());
		
		// 4. 若target為 null 或 目前不為在職員工 回傳無找到員工資料NOT_FOUND。
		if (target == null || !target.getEmployed()) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}
		
		// 5. 老闆資料僅老闆本人可操作，若登入者非老闆則回傳權限不足GRADE_INSUFFICIENT。
		if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
			if (target.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
				return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
						ResMessage.GRADE_INSUFFICIENT.getMessage());
			}
		}
		
		// 6. 無薪假開始日期應早於無薪假結束日期。
		if (req.getUnpaidLeaveEndDate().isBefore(req.getUnpaidLeaveStartDate())) {
			return new BasicRes(ResMessage.END_DATE_BEFORE_START_DATE.getCode(),
					ResMessage.END_DATE_BEFORE_START_DATE.getMessage());
		}
		
		// 7. 更新員工無薪假資訊、回傳成功 或 失敗訊息。
		int updateSuccessCount = employeeDao.updateEmployeeUnpaidLeaveById(req.getId(), req.getUnpaidLeaveStartDate(),
				req.getUnpaidLeaveEndDate(), req.getUnpaidLeaveReason(), LocalDate.now(), //
				getSessionEmployee().getId());
		// 若updateSuccessCount不為1代表更新失敗FAILED。
		if (updateSuccessCount != 1) {
			return new BasicRes(ResMessage.FAILED.getCode(), ResMessage.FAILED.getMessage());
		}
		// 若updateSuccessCount為1代表更新成功SUCCESS。
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}
	
	/**
	 * 方法11-reinstateEmployeeUnpaidLeaveById: 清空員工無薪假紀錄
	 * 僅老闆及人資主管有權清空員工無薪假紀錄。
	 */
	@Override
	public BasicRes reinstateEmployeeUnpaidLeaveById(int employeeId) {
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 1. 檢查登入者部門: 僅老闆及人資部門有權更改。
		if (!sessionIsHrOrBoss(sessionEmployee)) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		
		// 2. 檢查登入者職等: 若不為主管或老闆回傳權限不足。
		if (!sessionIsManagerOrBoss(sessionEmployee)) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		
		// 3. 使用輸入的ID查詢欲編輯員工的資料target。
		EmployeeDto target = employeeDao.selectByEmployeeId(employeeId);
		
		// 4. 若target 為null 或 target為非在職員工回傳無查詢到員工資料。
		if (target == null || !target.getEmployed()) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}
		
		// 5. 更新員工無薪假資訊、回傳成功 或 失敗訊息。
		int updateSuccessCount = employeeDao.updateEmployeeUnpaidLeaveById(employeeId, null, null, //
				null, LocalDate.now(), getSessionEmployee().getId());
		// 若updateSuccessCount不為1代表清空員工無薪假紀錄失敗FAILED。
		if (updateSuccessCount != 1) {
			return new BasicRes(ResMessage.FAILED.getCode(), ResMessage.FAILED.getMessage());
		}
		// 若updateSuccessCount為1代表清空員工無薪假紀錄成功SUCCESS。
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}
	
	/**
	 * 方法12-getAllEmployees: 取得所有在職員工的各項資料。
	 * 一般部門無法使用此方法。
	 * 
	 * 其他限制:
	 * 1. 若目前登入者為一般員工，則僅輸出一般員工的資料。若不為一般員工則輸出全部資料(全部皆有權限查看)。
	 * 
	 * return List<EmployeeInfoVo> employeeInfoVoList
	 */
	@Override
	public EmployeeListRes getAllEmployees() {
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 1. 確認變更者的單位，若查詢者部門為一般部門，則拋出錯誤
		if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.GENERAL_AFFAIRS.getDepartmentName())) {
			return new EmployeeListRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		
		// 2. 取得全部員工資訊。
		List<EmployeeDto> employeeList = employeeDao.getAllEmployees();
		
		// 3. 創建List<EmployeeInfoVo> employeeInfoVoList用以。
		List<EmployeeInfoVo> employeeInfoVoList = new ArrayList<>();
		
		for (EmployeeDto item : employeeList) {
			// 4. 若目前登入者為一般員工，則僅輸出一般員工的資料。
			if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				if (item.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
					EmployeeInfoVo EmployeeInfo = changeToFrontData(item);
					employeeInfoVoList.add(EmployeeInfo);
				}
			} else {
				// 5. 若不為一般員工則輸出全部資料。
				EmployeeInfoVo EmployeeInfo = changeToFrontData(item);
				employeeInfoVoList.add(EmployeeInfo);
			}
		}
		
		// 6. 將查詢到的員工資料和成功訊息一起回傳。
		return new EmployeeListRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), employeeInfoVoList);
	}
	
	/**
	 * 方法13-checkLogin: 確認當前是否有登入中的帳號。
	 * 
	 * 此方法會檢查當前請求的 HTTP Session 是否存在，並且 Session 中是否存有使用者帳號 ID。
	 * 如果找到有效的登入 Session，則回傳使用者資訊；否則，回傳 null。
	 * 
	 * @return EmployeeRes 物件。如果使用者已登入，其 data 欄位會包含前端所需的使用者資料，如果未登入，其 data 欄位為 null。
	 */
	@Override
	public EmployeeRes checkLogin() {
		// 1. 為了在非 Controller 層也能存取 Session，透過 RequestContextHolder 取得當前請求。
		// 取得 Spring 框架中，當前請求的屬性。
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		
		// 2. 從屬性中取得 HTTP 請求，並從中獲取 Session (不主動創建)。
		// 取得 Session 物件。傳入 false 確保如果沒有 Session 就不會主動創建，避免不必要的資源浪費。
		HttpSession session = attributes.getRequest().getSession(false);
		
		// 3. 判斷當前請求是否具備有效的登入 Session。
		// 檢查 session 物件是否存在，以及 session 中是否存有 "accountId" 屬性。
		if (session != null && session.getAttribute("accountId") != null) {
			// 使用者已登入，回傳包含使用者資料的成功回應。
			return new EmployeeRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), //
					changeToFrontData(getSessionEmployee()));
		} else {
			// 使用者未登入，回傳 null 資料的成功回應。
			return new EmployeeRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), null);
		}
	}
	
	/**
	 * 方法14-login: 登入 API。
	 * 
	 * 處理使用者登入請求。
	 * 此方法會先檢查使用者是否已處於登入狀態，若否，則根據提供的帳號（Email）和密碼進行驗證。
	 * 如果帳號或密碼不正確，或帳號不存在，則回傳對應的錯誤訊息。
	 * 
	 * @param req 包含登入帳號（Email）和密碼的 LoginReq 請求物件。
	 * @return BasicRes 物件。如果成功登入，回傳 SUCCESS 訊息；若已登入、帳號不存在或密碼不符，回傳對應的錯誤訊息。
	 */
	@Override
	public BasicRes login(LoginReq req) {
		// 1. 取得 Spring 框架中當前請求的屬性。
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		// 2. 從屬性中取得 HTTP Session 物件。傳入 false 確保如果沒有 Session 就不會主動創建。
		HttpSession session = attributes.getRequest().getSession(false);
		
		// 3. 若 session 存在且包含 "accountId" 屬性，表示使用者已登入，回傳 PLEASE_LOGOUT_FIRST 要求先登出。
		if (session != null && session.getAttribute("accountId") != null) {
			return new BasicRes(ResMessage.PLEASE_LOGOUT_FIRST.getCode(), ResMessage.PLEASE_LOGOUT_FIRST.getMessage());
		}
		
		// 4. 使用輸入的 Email (帳號) 查詢該位員工資料。
		EmployeeDto employeeInfo = employeeDao.selectByEmployeeEmail(req.getAccount());
		
		// 5. 執行帳號驗證及密碼比對，如果驗證失敗會回傳對應的錯誤訊息物件。
		BasicRes checkRes = checkAccount(employeeInfo, req.getPassword());
		
		// 6. 如果 checkAccount 回傳了非 null 的錯誤訊息（例如帳號不存在或密碼錯誤），則直接回傳該錯誤。
		if (checkRes != null) {
			return new BasicRes(checkRes.getCode(), checkRes.getMessage());
		}
		
		// 7. 帳號及密碼驗證成功，登入完成，回傳 SUCCESS 訊息。
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}
	
	/**
	 * 方法15-sendVerificationLetter: 發送驗證信
	 * 
	 */
	@Override
	public BasicRes sendVerificationLetter(String email) {
		// 1. 先確定使用者輸入的Email是否有員工在使用、是否存在。
		if (employeeDao.selectByEmployeeEmail(email) == null) {
			return new BasicRes(ResMessage.NOT_FOUND.getCode(), ResMessage.NOT_FOUND.getMessage());
		}
		// 2. 透過randomCodeGenerator()方法產出驗證碼code。
		String code = randomCodeGenerator();
		// 3. 將Email、驗證碼透過insertOrUpdate存入emailVerificationDao中，使用者必須在設定保存的10分鐘內驗證完畢。
		emailVerificationDao.insertOrUpdate(email, code, LocalDateTime.now().plusMinutes(10));
		// 4. 寄出驗證碼信件。
		emailServiceImpl.sendVerificationCode(//
				email, //
				"【HR系統】驗證碼通知", //
				"您的驗證碼是： " + code + " ，請在10分鐘內完成驗證");
		// 5. 回傳成功訊息SUCCESS。
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}
	
	/**
	 * 方法16-checkVerification: 認證驗證碼。
	 */
	@Override
	public BasicRes checkVerification(String email, String code) {
		// 1. 使用使用者輸入的Email查詢發送驗證碼紀錄。
		EmailVerificationDto verificationData = emailVerificationDao.selectByEmail(email);
		// 2. 若無發送驗證碼紀錄，verificationData為null，代表需先發送驗證碼，回傳操作失敗FAILED。
		if (verificationData == null) {
			return new BasicRes(ResMessage.FAILED.getCode(), //
					ResMessage.FAILED.getMessage());
		}
		// 3. 認證驗證碼: 若輸入的code與資料庫中的getCode()不符合，會回傳驗證失敗VERIFICATION_FAILED。
		if (!verificationData.getCode().equals(code)) {
			return new BasicRes(ResMessage.VERIFICATION_FAILED.getCode(), //
					ResMessage.VERIFICATION_FAILED.getMessage());
		}
		// 4. 若目前時間已超過資料庫中紀錄的驗證碼期限(10分鐘)，回傳驗證碼失效VERIFICATION_CODE_VALID。
		if (LocalDateTime.now().isAfter(verificationData.getExpireAt())) {
			return new BasicRes(ResMessage.VERIFICATION_CODE_VALID.getCode(), //
					ResMessage.VERIFICATION_CODE_VALID.getMessage());
		}
		// 5. 調用emailVerificationDao.updateVerified方法，透過email將驗證狀態改為已驗證verified = true。
		emailVerificationDao.updateVerified(email);
		// 6. 回傳成功訊息SUCCESS。
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}
	
	/**
	 * 方法17-getDepartmentEmployeesList: 確認同部門的員工清單
	 * 一般員工不可使用，僅主管及老闆可用。
	 * 
	 * 限制:
	 * 1. 一般員工不可使用此API。
	 * 2. 僅可查詢同部門員工資料。
	 */
	@Override
	public EmployeeBasicInfoRes getDepartmentEmployeesList() {
		// 1. 取得當前登入者的員工資訊。
		EmployeeDto sessionEmployee = getSessionEmployee();
		
		// 2. 職等若為一般員工則限制不可使用，回傳權限不足GRADE_INSUFFICIENT。
		if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
			return new EmployeeBasicInfoRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		
		// 3. 取得全部員工資料。
		List<EmployeeDto> allEmployeeList = employeeDao.getAllEmployees();
		
		// 4. 創建空的List<EmployeeBasicInfoVo> EmployeeBasicInfoVoList。
		List<EmployeeBasicInfoVo> EmployeeBasicInfoVoList = new ArrayList<>();
		
		// 5. 若allEmployeeList為null或為空，回傳成功訊息、空的EmployeeBasicInfoVoList。
		if (allEmployeeList == null || allEmployeeList.isEmpty()) {
			return new EmployeeBasicInfoRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(),
					EmployeeBasicInfoVoList);
		}
		
		// 6. 若查詢到的員工item.getDepartment() 和 調用者sessionEmployee.getDepartment() 同部門，將資料輸出。
		for (EmployeeDto item : allEmployeeList) {
			if (sessionEmployee.getDepartment().equalsIgnoreCase(item.getDepartment())) {
				// 透過changeDtoToEmployeeBasicInfoVoList將item資料型態轉為EmployeeBasicInfoVo放入vo中。
				EmployeeBasicInfoVo vo = changeDtoToEmployeeBasicInfoVoList(item);
				// 將vo加入EmployeeBasicInfoVoList。
				EmployeeBasicInfoVoList.add(vo);
			}
		}
		
		// 7. 回傳SUCCESS成功訊息、同部門所有員工資訊EmployeeBasicInfoVoList。
		return new EmployeeBasicInfoRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(),
				EmployeeBasicInfoVoList);
	}
	
	/**
	 * 方法18-submitUpdateEmployeeInfoApplication: 提交員工資料更新申請
	 */
	@Override
	@Transactional(rollbackOn = Exception.class)
	public BasicRes submitEmployeeApplication(SubmitEmployeeApplicationReq req) throws Exception {
		// 1. 。
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 2. 。
		EmployeeDto target = employeeDao.selectByEmployeeId(req.getEmployeeId());
		// 3. 確定是否有輸入員編 id 的資料。
		if (target == null || !target.getEmployed()) {
			return new BasicRes(ResMessage.EMPLOYEE_NOT_FOUND.getCode(), //
					ResMessage.EMPLOYEE_NOT_FOUND.getMessage());
		}
		// 4. 。
		String approvalPendingRole = "";
		boolean submitUp = true;

		ApplicationType applicationType = req.getType();
		// 第一段 switch 先判斷送審對象以及是否往上送審
		switch (applicationType) {
		case basic_info:
		case clock:
			// 一般部門員工僅能申請修改自己的基本資料
			if (isEmployeeNotInHRDepartment(sessionEmployee)) {
				if (sessionEmployee.getId() != target.getId()) {
					return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
							ResMessage.GRADE_INSUFFICIENT.getMessage());
				}
			}
			if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				if (sessionEmployee.getDepartment()//
						.equalsIgnoreCase(DepartmentType.HR.getDepartmentName())) {
					approvalPendingRole = DepartmentAndPosition.HR_MANAGER.getPosition();
					submitUp = false;
				} else if (sessionEmployee.getDepartment()//
						.equalsIgnoreCase(DepartmentType.ACCOUNTANT.getDepartmentName())) {
					approvalPendingRole = DepartmentAndPosition.ACCOUNTING_MANAGER.getPosition();
				} else if (sessionEmployee.getDepartment()//
						.equalsIgnoreCase(DepartmentType.GENERAL_AFFAIRS.getDepartmentName())) {
					approvalPendingRole = DepartmentAndPosition.GENERAL_MANAGER.getPosition();
				}

			} else if (!sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				if (sessionEmployee.getDepartment()//
						.equalsIgnoreCase(DepartmentType.HR.getDepartmentName())) {
					approvalPendingRole = DepartmentAndPosition.BOSS.getPosition();
					submitUp = false;
				} else if (sessionEmployee.getDepartment()//
						.equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
					approvalPendingRole = DepartmentAndPosition.HR_MANAGER.getPosition();
					submitUp = false;
				} else {
					approvalPendingRole = DepartmentAndPosition.HR_EMPLOYEE.getPosition();
				}
			}
			break;

		case job:
		case resign:
		case overtime:
			// 不得申請修改自己的離職或調薪或調部門
			if (sessionEmployee.getId() == target.getId()) {
				return new BasicRes(ResMessage.CANNOT_UPDATE_SELF_INFO.getCode(), //
						ResMessage.CANNOT_UPDATE_SELF_INFO.getMessage());
			}

			if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
						ResMessage.GRADE_INSUFFICIENT.getMessage());
			}

			// 不得申請不同部門員工的變更
			if (!sessionEmployee.getDepartment().equalsIgnoreCase(target.getDepartment())) {
				return new BasicRes(ResMessage.CANNOT_UPDATE_DIFFERENT_DEPARTMENT_EMPLOYEE_INFO.getCode(), //
						ResMessage.CANNOT_UPDATE_DIFFERENT_DEPARTMENT_EMPLOYEE_INFO.getMessage());
			}

			// 判斷申請人的部門，來決定要交由誰審核
			if (sessionEmployee.getDepartment()//
					.equalsIgnoreCase(DepartmentType.HR.getDepartmentName())) {
				approvalPendingRole = DepartmentAndPosition.BOSS.getPosition();
				submitUp = false;
			} else {
				if (!target.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
					approvalPendingRole = DepartmentAndPosition.HR_MANAGER.getPosition();
					submitUp = false;
				} else {
					approvalPendingRole = DepartmentAndPosition.HR_EMPLOYEE.getPosition();
				}

			}
			break;
		}

		// 第二段 switch 判斷是否輸入資料格式有誤
		switch (applicationType) {
		case basic_info:
			UpdateEmployeeBasicInfoReq basicInfo = (UpdateEmployeeBasicInfoReq) req.getData();
			if (!basicInfo.getEmail().equals(target.getEmail())) {
				if (employeeDao.countByEmail(basicInfo.getEmail()) != 0) {
					return new BasicRes(ResMessage.EMAIL_ALREADY_EXISTS.getCode(), //
							ResMessage.EMAIL_ALREADY_EXISTS.getMessage());
				}
			}
			break;
		case clock:
			UpdateClockTimeReq clock = (UpdateClockTimeReq) req.getData();
			if (!ClockType.checkAllType(clock.getClockType())) {
				return new BasicRes(ResMessage.CLOCK_TYPE_INVALID.getCode(), //
						ResMessage.CLOCK_TYPE_INVALID.getMessage());
			}

			AttendanceDto record = attendanceDao.selectAttendanceById(clock.getAttendanceId());
			if (record == null) {
				return new BasicRes(ResMessage.CLOCK_RECORD_NOT_FOUND.getCode(), //
						ResMessage.CLOCK_RECORD_NOT_FOUND.getMessage());
			}

			if (record.getEmployeeId() != target.getId()) {
				return new BasicRes(ResMessage.ATTENDANCE_RECORD_NOT_SAME_EMPLOYEE.getCode(), //
						ResMessage.ATTENDANCE_RECORD_NOT_SAME_EMPLOYEE.getMessage());
			}
			break;
		case job:
			UpdateEmployeeJobReq jobInfo = (UpdateEmployeeJobReq) req.getData();

			// 部門名稱 需進行有效性檢查 (使用 DepartmentType)。
			if (!DepartmentType.isValidDepartment(jobInfo.getDepartment())) {
				return new BasicRes(ResMessage.INVALID_DEPARTMENT.getCode(), //
						ResMessage.INVALID_DEPARTMENT.getMessage());
			}

			// 職等 有效性檢查 (使用 PositionType)。
			String position = jobInfo.getPosition();
			if (!PositionType.isValidPosition(position)) {
				return new BasicRes(ResMessage.INVALID_POSITION.getCode(), //
						ResMessage.INVALID_POSITION.getMessage());
			}

			// position和grade對應的判斷。
			boolean isValidCombination = positionIsValidCombination(position, jobInfo.getGrade());
			if (!isValidCombination) {
				return new BasicRes(ResMessage.POSITION_GRADE_MISMATCH.getCode(), //
						ResMessage.POSITION_GRADE_MISMATCH.getMessage());
			}
			break;
		case resign:
			EmployeeResignationReq resignInfo = (EmployeeResignationReq) req.getData();
			// 若變更人不為老闆，不能更改老闆的資料
			if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
				if (target.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
					return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
							ResMessage.GRADE_INSUFFICIENT.getMessage());
				}
				// 離職日期不得早於入職日期，離職日期僅能輸入 入職日當天 或 入職日後的未來日期。
				if (resignInfo.getResignationDate().isBefore(target.getEntryDate())) {
					return new BasicRes(ResMessage.RESIGNATION_DATE_ERROR.getCode(),
							ResMessage.RESIGNATION_DATE_ERROR.getMessage());
				}
			}
			break;
		case overtime:
			OvertimeApplicationReq overtimeApplication = (OvertimeApplicationReq) req.getData();
			// 確認申請開始時間不能比結束時間晚
			if (overtimeApplication.getStartTime().isAfter(overtimeApplication.getEndTime())) {
				return new BasicRes(ResMessage.INVALID_TIME_RANGE.getCode(), //
						ResMessage.INVALID_TIME_RANGE.getMessage());
			}

			// 限制加班時間至少需滿 30 分鐘
			long minutes = Duration.between(overtimeApplication.getStartTime(), overtimeApplication.getEndTime())
					.toMinutes();
			if (minutes < 30) {
				return new BasicRes(ResMessage.OVERTIME_TOO_SHORT.getCode(),
						ResMessage.OVERTIME_TOO_SHORT.getMessage());
			}
			break;
		}

		try {
			String dataStr = mapper.writeValueAsString(req.getData());
			EmployeeApplicationDto applicationDto = new EmployeeApplicationDto();

			applicationDto.setEmployeeId(req.getEmployeeId());
			applicationDto.setComment(req.getComment());
			applicationDto.setType(req.getType().getType());
			applicationDto.setData(dataStr);
			applicationDto.setApplyDateTime(LocalDateTime.now());
			applicationDto.setSubmitUp(submitUp);
			applicationDto.setApprovalPendingRole(approvalPendingRole);
			applicationDto.setApplyerId(sessionEmployee.getId());
			applicationDto.setApplicationGroup(0);

			employeeApplicationDao.insertApplication(applicationDto);

			employeeApplicationDao.updateApplicationGroupIdById(applicationDto.getId(),
					applicationDto.getId());
		} catch (Exception e) {
			throw e;
		}

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	// 方法19-checkUpdateApplication: 查看員工資料更新申請清單
	@Override
	public UpdateInfoApplicationRes checkUpdateApplication() throws Exception {
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 若查詢者為一般員工且非人資部門，拋出錯誤
		if (isEmployeeNotInHRDepartment(sessionEmployee)) {
			return new UpdateInfoApplicationRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		List<EmployeeApplicationDto> applicationList = employeeApplicationDao
				.checkAllUpdateApplicationList();
		List<UpdateEmployeeInfoApplicationVo> forFrontList = new ArrayList<>();
		if (applicationList == null || applicationList.isEmpty()) {
			return new UpdateInfoApplicationRes(ResMessage.SUCCESS.getCode(), //
					ResMessage.SUCCESS.getMessage(), forFrontList);
		}

		for (EmployeeApplicationDto item : applicationList) {
			if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.GENERAL_AFFAIRS.getDepartmentName())
					&& !sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				if (item.getApprovalPendingRole()//
						.equalsIgnoreCase(DepartmentAndPosition.GENERAL_MANAGER.getPosition())) {
					UpdateEmployeeInfoApplicationVo vo = changeApplicationDtoToVo(item);
					forFrontList.add(vo);
				}
			} else if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.ACCOUNTANT.getDepartmentName())
					&& !sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				if (item.getApprovalPendingRole()//
						.equalsIgnoreCase(DepartmentAndPosition.ACCOUNTING_MANAGER.getPosition())) {
					UpdateEmployeeInfoApplicationVo vo = changeApplicationDtoToVo(item);
					forFrontList.add(vo);
				}
			} else if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())
					&& sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				if (item.getApprovalPendingRole().//
						equalsIgnoreCase(DepartmentAndPosition.HR_EMPLOYEE.getPosition())) {
					UpdateEmployeeInfoApplicationVo vo = changeApplicationDtoToVo(item);
					forFrontList.add(vo);
				}
			} else if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())
					&& !sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				if (item.getApprovalPendingRole().//
						equalsIgnoreCase(DepartmentAndPosition.HR_MANAGER.getPosition())) {
					UpdateEmployeeInfoApplicationVo vo = changeApplicationDtoToVo(item);
					forFrontList.add(vo);
				}
			} else if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
				if (item.getApprovalPendingRole().//
						equalsIgnoreCase(DepartmentAndPosition.BOSS.getPosition())) {
					UpdateEmployeeInfoApplicationVo vo = changeApplicationDtoToVo(item);
					forFrontList.add(vo);
				}
			} else {
				return new UpdateInfoApplicationRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
						ResMessage.GRADE_INSUFFICIENT.getMessage());
			}
		}

		return new UpdateInfoApplicationRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), forFrontList);
	}

	// 方法20-submitEmployeeApplication: 審核員工資料更新申請
	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes approveEmployeeApplication(ApproveEmployeeApplicationReq req) throws Exception {
		EmployeeDto sessionEmployee = getSessionEmployee();

		if (isEmployeeNotInHRDepartment(sessionEmployee)) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		if (!req.isApproved()) {
			if (req.getRejectionReason() == null || req.getRejectionReason().isBlank()) {
				return new BasicRes(ResMessage.REJECTION_REASON_REQUIRED.getCode(), //
						ResMessage.REJECTION_REASON_REQUIRED.getMessage());
			}
		}

		EmployeeApplicationDto applicationDto = employeeApplicationDao
				.getByApplicationId(req.getApplicationId());

		if (applicationDto == null) {
			return new BasicRes(ResMessage.APPLICATION_NOT_FOUND.getCode(), //
					ResMessage.APPLICATION_NOT_FOUND.getMessage());
		}

		EmployeeDto target = employeeDao.selectByEmployeeId(applicationDto.getEmployeeId());

		// 不得審核自己的申請
		if (sessionEmployee.getId() == applicationDto.getEmployeeId()) {
			return new BasicRes(ResMessage.CANNOT_APPROVE_SELF_INFO.getCode(), //
					ResMessage.CANNOT_APPROVE_SELF_INFO.getMessage());
		}
		boolean submitUp = checkSubmitUp(sessionEmployee, applicationDto);

		EmployeeApplicationRecordDto recordDto = changeApplicationDtoToRecordDto(//
				applicationDto, req, sessionEmployee);

		try {
			// 將申請資料寫進申請紀錄後刪除申請資料
			employeeApplicationRecordDao.insertApplicationRecord(recordDto);

			if (req.isApproved() && submitUp) {
				String approvalPendingRole = checkApprovalPendingRole(sessionEmployee);
				EmployeeApplicationDto insertApplicationDto = new EmployeeApplicationDto();

				insertApplicationDto.setEmployeeId(applicationDto.getEmployeeId());
				insertApplicationDto.setComment(applicationDto.getComment());
				insertApplicationDto.setType(applicationDto.getType());
				insertApplicationDto.setData(applicationDto.getData());
				insertApplicationDto.setApplyDateTime(applicationDto.getApplyDateTime());
				insertApplicationDto.setSubmitUp(submitUp);
				insertApplicationDto.setApprovalPendingRole(approvalPendingRole);
				insertApplicationDto.setApplyerId(applicationDto.getApplyerId());
				insertApplicationDto.setApplicationGroup(applicationDto.getApplicationGroup());

				employeeApplicationDao.insertApplication(insertApplicationDto);

			}

			if (req.isApproved() && !submitUp) {
				ApplicationType type = ApplicationType.fromType(applicationDto.getType());

				switch (type) {
				case basic_info:
					UpdateEmployeeBasicInfoReq basicInfo = mapper.readValue(applicationDto.getData(), //
							UpdateEmployeeBasicInfoReq.class);
					if (!basicInfo.getEmail().equals(target.getEmail())) {
						if (employeeDao.countByEmail(basicInfo.getEmail()) != 0) {
							throw new ApplicationException(ResMessage.EMAIL_ALREADY_EXISTS.getCode(), //
									ResMessage.EMAIL_ALREADY_EXISTS.getMessage());
						}
					}
					employeeDao.updateEmployeeBasicInfo(//
							recordDto.getEmployeeId(), //
							basicInfo.getName(), //
							basicInfo.getEmail(), //
							basicInfo.getPhone(), //
							basicInfo.getGender(), //
							LocalDate.now(), //
							sessionEmployee.getId());
					break;
				case job:
					UpdateEmployeeJobReq jobInfo = mapper.readValue(applicationDto.getData(), //
							UpdateEmployeeJobReq.class);

					employeeDao.updateEmployeeJob(//
							recordDto.getEmployeeId(), //
							jobInfo.getDepartment(), //
							jobInfo.getGrade(), //
							jobInfo.getSalaries(), //
							jobInfo.getPosition(), //
							LocalDate.now(), //
							sessionEmployee.getId());
					break;
				case resign:
					EmployeeResignationReq resignInfo = mapper.readValue(applicationDto.getData(), //
							EmployeeResignationReq.class);
					employeeDao.resignEmployee(//
							recordDto.getEmployeeId(), //
							resignInfo.getResignationDate(), //
							resignInfo.getResignationReason(), //
							LocalDate.now(), //
							sessionEmployee.getId());
					break;
				case clock:
					UpdateClockTimeReq clock = mapper.readValue(applicationDto.getData(), //
							UpdateClockTimeReq.class);
					LocalDateTime clockIn = null;
					LocalDateTime clockOut = null;

					if (clock.getClockType().equalsIgnoreCase(ClockType.CLOCK_IN.getType())) {
						clockIn = clock.getClockTime();
					} else {
						clockOut = clock.getClockTime();
					}

					attendanceDao.updateClockTime(//
							clock.getAttendanceId(), //
							clockIn, //
							clockOut, //
							LocalDate.now(), //
							sessionEmployee.getId());

					break;
				case overtime:
					OvertimeApplicationReq overtimeApplication = mapper.readValue(applicationDto.getData(), //
							OvertimeApplicationReq.class);
					break;
				}
			}
			employeeApplicationDao.deleteByApplicationId(applicationDto.getId());
		} catch (Exception e) {
			throw e;
		}

		return new BasicRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage());

	}
	
	/**
	 * 額外方法: 產出8碼大小寫英亂數（信箱驗證用）。
	 * 
	 * @return code: 長度為8碼的驗證碼。
	 */
	private String randomCodeGenerator() {
		// 1. 創建驗證信亂碼用字串池charPool。
		String charPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		// 2. 創建random物件。
		Random random = new Random();
		// 3. 創建空字串code用以存放產出的亂碼。
		String code = "";
		// for迴圈設定跑8次，驗證碼長度為8。
		for (int i = 0; i < 8; i++) {
			// 從字元池的長度範圍內，隨機生成一個索引值。
			int index = random.nextInt(charPool.length());
			// 根據隨機生成的索引值，從字元池中取出對應的字元，並將其添加到驗證碼字串code中。
			code += charPool.charAt(index);
		}
		// 回傳已組裝好的驗證碼code。
		return code;
	}
	
	/**
	 * checkAccount:
	 * 被Login調用的額外方法，用以檢查輸入的密碼和資料庫中的密碼是否相符。
	 * 
	 * @param employeeInfo 該位員工的各項資訊。
	 * @param password 使用者輸入的密碼。
	 * @return 若檢查通過會回傳null。
	 */
	private BasicRes checkAccount(EmployeeDto employeeInfo, String password) {
		// 1. 若傳入的employeeInfo為null代表未查到該員工帳號，回傳ACCOUNT_NOT_FOUND。
		if (employeeInfo == null) {
			return new BasicRes(ResMessage.ACCOUNT_NOT_FOUND.getCode(), //
					ResMessage.ACCOUNT_NOT_FOUND.getMessage());
		}
		
		// 2. 若目前查詢的員工非在職員工也回傳未查到該員工帳號ACCOUNT_NOT_FOUND。
		if (!employeeInfo.getEmployed()) {
			return new BasicRes(ResMessage.ACCOUNT_NOT_FOUND.getCode(), //
					ResMessage.ACCOUNT_NOT_FOUND.getMessage());
		}
		
		// 3. 比對 輸入的密碼password 和 資料庫中的密碼employeeInfo.getPassword()，兩者相符true、兩者不相符false。
		boolean checkResult = encoder.matches(password, employeeInfo.getPassword());
		
		// 4. checkResult為 false 代表兩者不相符，回傳PASSWORD_MISMATCH。
		if (!checkResult) {
			return new BasicRes(ResMessage.PASSWORD_MISMATCH.getCode(), //
					ResMessage.PASSWORD_MISMATCH.getMessage());
		}
		
		// 5. 若checkResult為 true 代表兩者相符，回傳 null。
		return null;
	}
	
	/**
	 * 額外方法 - getSessionAccountId: 
	 * 
	 * 從當前的 HTTP Session 中檢索帳戶 ID。
	 * 此方法設計用於有會話可用的 Web 上下文。
	 *
	 * @return 如果在 Session 中找到帳戶 ID，則回傳 Integer 類型的帳戶 ID；
	 * 如果不是 Web 請求、沒有活躍的會話，或者 Session 中不存在 "accountId" 屬性，則回傳 null。
	 */
	private Integer getSessionAccountId() {
		// 1. 獲取當前的請求屬性。
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		
		// 2. 如果沒有找到請求屬性（可能是非 web 請求　或　沒有 session），回傳null。
		if (attributes == null) {
			return null;
		}
		
		// 3. 獲取與當前請求相關聯的 HTTP Session。
		HttpSession session = attributes.getRequest().getSession();
		
		// 4. 如果當前請求沒有會話，則回傳 null。
		if (session == null) {
			return null;
		}
		
		// 5. 回傳儲存在 Session 中的 "accountId" 屬性，並將其轉換為 Integer 類型。
		return (Integer) session.getAttribute("accountId");
	}

	/**
	 * getSessionEmployee: 使用session中儲存的ID資料取得目前登入員工的資訊。
	 * 
	 * @return 目前登入員工資訊--EmployeeDto sessionEmployee。
	 */
	private EmployeeDto getSessionEmployee() {
		// 1. 從 session 中獲取當前登入帳戶的 ID。
		int sessionId = getSessionAccountId();
		
		// 2. 使用獲取到的員工 ID 從 DAO (Data Access Object) 中查詢並回傳員工資訊。
		return employeeDao.selectByEmployeeId(sessionId);
	}

	/**
	 * sessionIsManagerOrBoss: 檢查目前登入者是否為主管或老闆。
	 * 
	 * @param sessionEmployee 目前登入者資訊包。
	 * @return 若傳入的EmployeeDto sessionEmployee目前登入者為null 或 一般員工就回傳false，否則回傳true。
	 */
	private boolean sessionIsManagerOrBoss(EmployeeDto sessionEmployee) {
		// 1. 檢查sessionEmployee是否為空、sessionEmployee職等是否為一般員工。
		if (sessionEmployee == null
				|| sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
			// sessionEmployee為空 或 為一般員工時，回傳false。
			return false;
		}
		// sessionEmployee不為空 或 為主管或老闆時，回傳true。
		return true;
	}

	/**
	 * sessionIsHrOrBoss: 檢查目前登入者是否為人資部門或老闆。
	 * 
	 * @param sessionEmployee 目前登入者資訊包。
	 * @return 若傳入的EmployeeDto sessionEmployee目前登入者不為HR人資部門 或 BOSS老闆 則回傳false，是則回傳true。
	 */
	private boolean sessionIsHrOrBoss(EmployeeDto sessionEmployee) {
		// 登入者是否為人資部門或老闆，若皆不是(不成立)回傳false，若皆是(成立)回傳true。
		if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())
				&& !sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
			return false;
		}
		return true;
	}

	/**
	 * 額外方法--
	 * positionIsValidCombination: 檢查職位 (position) 和職等 (grade) 是否為有效的組合。
	 *
	 * @param position 員工的職位，例如 "boss", "manager", "employee"。
	 * @param grade 員工的職等boss: 11、manager: 6~10、employee: 1~5。
	 * 
	 * @return 如果職位和職等是有效的組合，則回傳 true；否則回傳 false。
	 */
	private boolean positionIsValidCombination(String position, int grade) {
		// 1. 初始化一個布林變數，用於儲存職位和職等組合是否有效。
		boolean isValidCombination = false;
		// 2. 根據職位（轉換為小寫以進行不區分大小寫的比較）來判斷職等是否符合預期範圍。
		switch (position.toLowerCase()) {
		case "boss": // 如果職位是 "boss"，則只有職等為 11 才是有效組合。
			isValidCombination = (grade == 11);
			break;
		case "manager": // 如果職位是 "manager"，則職等必須在 6 到 10 之間才有效。
			isValidCombination = (grade >= 6 && grade <= 10);
			break;
		case "employee": // 如果職位是 "employee"，則職等必須在 1 到 5 之間才有效。
			isValidCombination = (grade >= 1 && grade <= 5);
			break;
		}
		// 3. 回傳判斷結果，表示職位和職等是否為有效的組合。
		return isValidCombination;
	}
	
	/**
	 * 輔助方法--
	 * changeToFrontData: 將傳入的EmployeeDto data轉換資料型態為EmployeeInfoVo後回傳EmployeeInfoVo employeeInfo。
	 * 會被各方法調用來轉換資料型態為EmployeeInfoVo。
	 * 
	 * @param data
	 * @return EmployeeInfoVo employeeInfo
	 */
	private EmployeeInfoVo changeToFrontData(EmployeeDto data) {
		// 1. 創建EmployeeInfoVo employeeInfo的空EmployeeInfoVo。
		EmployeeInfoVo employeeInfo = new EmployeeInfoVo();
		// 2. 將傳入的各個資料放入步驟1.創建的employeeInfo當中。
		employeeInfo.setId(data.getId());
		employeeInfo.setDepartment(data.getDepartment());
		employeeInfo.setName(data.getName());
		employeeInfo.setEmail(data.getEmail());
		employeeInfo.setPhone(data.getPhone());
		employeeInfo.setGender(data.getGender());
		employeeInfo.setGrade(data.getGrade());
		employeeInfo.setEntryDate(data.getEntryDate());
		employeeInfo.setResignationDate(data.getResignationDate());
		employeeInfo.setResignationReason(data.getResignationReason());
		employeeInfo.setSalaries(data.getSalaries());
		employeeInfo.setPosition(data.getPosition());
		employeeInfo.setEmployed(data.getEmployed());
		employeeInfo.setRemainingPreviousAnnualLeave(data.getRemainingPreviousAnnualLeave());
		employeeInfo.setRemainingCurrentAnnualLeave(data.getRemainingCurrentAnnualLeave());
		employeeInfo.setRemainingPaidSickLeave(data.getRemainingPaidSickLeave());
		employeeInfo.setUnpaidLeaveStartDate(data.getUnpaidLeaveStartDate());
		employeeInfo.setUnpaidLeaveEndDate(data.getUnpaidLeaveEndDate());
		employeeInfo.setUnpaidLeaveReason(data.getUnpaidLeaveReason());
		employeeInfo.setFinalUpdateDate(data.getFinalUpdateDate());
		employeeInfo.setFinalUpdateEmployeeId(data.getFinalUpdateEmployeeId());
		// 3. 回傳已經資料型態轉換後的EmployeeInfoVo employeeInfo。
		return employeeInfo;
	}
	
	/**
	 * 額外方法--
	 * changeDtoToEmployeeBasicInfoVoList: 轉換資料型態EmployeeDto => EmployeeBasicInfoVo。
	 * 
	 * @param allEmployeeList 員工資料。
	 * @return 回傳轉換好的EmployeeBasicInfoVo vo。
	 */
	private EmployeeBasicInfoVo changeDtoToEmployeeBasicInfoVoList(EmployeeDto allEmployeeList) {
		// 1. 創建EmployeeBasicInfoVo vo的空EmployeeBasicInfoVo。
		EmployeeBasicInfoVo vo = new EmployeeBasicInfoVo();
		
		// 2. 將傳入的allEmployeeList放到EmployeeBasicInfoVo vo當中。
		vo.setId(allEmployeeList.getId());
		vo.setDepartment(allEmployeeList.getDepartment());
		vo.setName(allEmployeeList.getName());
		vo.setEmail(allEmployeeList.getEmail());
		vo.setPhone(allEmployeeList.getPhone());
		vo.setGender(allEmployeeList.getGender());
		vo.setGrade(allEmployeeList.getGrade());
		vo.setEntryDate(allEmployeeList.getEntryDate());
		vo.setUnpaidLeaveStartDate(allEmployeeList.getUnpaidLeaveStartDate());
		vo.setUnpaidLeaveEndDate(allEmployeeList.getUnpaidLeaveEndDate());
		vo.setUnpaidLeaveReason(allEmployeeList.getUnpaidLeaveReason());
		vo.setPosition(allEmployeeList.getPosition());
		vo.setFinalUpdateDate(allEmployeeList.getFinalUpdateDate());
		vo.setFinalUpdateEmployeeId(allEmployeeList.getFinalUpdateEmployeeId());
		
		// 3. 回傳轉換好的EmployeeBasicInfoVo vo。
		return vo;
	}
	
	/**
	 * 額外方法--
	 * changeApplicationDtoToVo
	 *
	 * 將 UpdateEmployeeInfoApplicationDto 物件轉換為 UpdateEmployeeInfoApplicationVo 物件。
	 * 此方法主要負責複製 DTO 中的屬性到 VO，並特別處理 `data` 欄位的 JSON 轉換。
	 *
	 * @param dto 包含員工資訊更新申請資料的 DTO 物件。
	 * @return 轉換後的 UpdateEmployeeInfoApplicationVo 物件。
	 * @throws Exception 如果在 JSON 轉換 `data` 欄位時發生錯誤。
	 */
	private UpdateEmployeeInfoApplicationVo changeApplicationDtoToVo(EmployeeApplicationDto dto)
			throws Exception {
		// 1. 創建一個新的 UpdateEmployeeInfoApplicationVo 物件作為回傳結果。
		UpdateEmployeeInfoApplicationVo vo = new UpdateEmployeeInfoApplicationVo();
		// 2. 嘗試將 DTO 中的 JSON 格式 `data` 字串轉換為 Java 物件並設定到 VO 中。 
		try {
			// 使用 ObjectMapper 將 JSON 字串 (dto.getData()) 讀取並轉換為泛型物件。
			Object object = mapper.readValue(dto.getData(), new TypeReference<>() {
			});
			// 將轉換後的物件設定到 VO 的 data 屬性中。
			vo.setData(object);
		} catch (Exception e) {
			// 如果轉換過程中發生任何異常，則重新拋出該異常。
			throw e;
		}
		// 3. 將 DTO 中其他簡單的屬性值複製到 VO 對應的屬性中。
		vo.setId(dto.getId()); // 設定申請 ID
		vo.setEmployeeId(dto.getEmployeeId()); // 設定員工 ID
		vo.setType(dto.getType()); // 設定申請類型
		vo.setComment(dto.getComment());
		vo.setApplyDateTime(dto.getApplyDateTime()); // 設定申請時間
		vo.setSubmitUp(dto.getSubmitUp());
		vo.setApprovalPendingRole(dto.getApprovalPendingRole());
		// 4. 回傳已填充資料的 UpdateEmployeeInfoApplicationVo 物件。
		return vo;
	}
	
	/**
	 * 額外方法--
	 * isEmployeeNotInHRDepartment: 確認目前登入者是否不為人資部門的一般員工。
	 * 
	 * @param sessionEmployee 目前登入者。
	 * @return 成立: true、不成立: false。
	 */
	private boolean isEmployeeNotInHRDepartment(EmployeeDto sessionEmployee) {
		// 如果是「員工職位」且部門不是「人資部門」，則返回 true，否則返回 false。
		if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())
				&& !sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())) {
			return true;
		}
		return false;
	}
	
	/**
	 * 額外方法--
	 * checkSubmitUp: 檢查員工資訊更新申請是否需要往上呈報。
	 * 判斷邏輯基於提交者的部門、職位以及申請的類型。
	 *
	 * @param sessionEmployee 提交申請的員工的 DTO 資訊(目前登入者)。
	 * @param applicationDto 員工資訊更新申請的 DTO 物件。
	 * 
	 * @return 如果申請需要往上呈報則回傳 true，否則回傳 false。
	 */
	private boolean checkSubmitUp(EmployeeDto sessionEmployee, EmployeeApplicationDto applicationDto) {
		// 1. 初始化 submitUp 變數為 true，表示預設情況下，申請都需要往上送審。
		boolean submitUp = true;
		// 2. 根據提交申請的員工（sessionEmployee）的部門和職位來判斷是否需要往上送審。
		if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())) {
			if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				// 若審核人是人資部門一般員工 且 僅變更員工基本資料或打卡時間，就無需往上送審submitUp = false。
				if (applicationDto.getType().equalsIgnoreCase(ApplicationType.basic_info.getType()) //
						|| applicationDto.getType().equalsIgnoreCase(ApplicationType.clock.getType())) {
					submitUp = false;
				}
			} else {
				// 若審核人是人資部門主管，則無需往上送審，因為已經是審核的API了。
				submitUp = false;
			}
		} else if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
			submitUp = false; // 老闆比照人資主管辦理，無需往上送審submitUp = false。
		}
		// 3. 回傳最終的判斷結果，決定申請是否需要往上呈報。
		return submitUp;
	}
	
	/**
	 * checkApprovalPendingRole
	 *
	 * 根據當前會話中的員工資訊，判斷下一個應待審核的角色。
	 * 此方法主要用於確定當前提交的申請在特定情況下應流向哪個角色進行審核。
	 *
	 * @param sessionEmployee 當前登入員工的 DTO 資訊。
	 * @return 代表下一個待審核角色的字串。
	 */
	private String checkApprovalPendingRole(EmployeeDto sessionEmployee) {
		// 1. 初始化 approvalPendingRole 為空字串，用於儲存判斷出的待審核角色。
		String approvalPendingRole = "";
		// 2. 判斷下一個待審核的角色。Boss 和人資主管無需判斷，因為在調用此方法前已確認他們不需再送審。
		if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())
				&& sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
			// 如果當前員工是人資部門的「員工」（非主管），則下一個待審核的角色應為人資經理。
			approvalPendingRole = DepartmentAndPosition.HR_MANAGER.getPosition();
		} else {
			approvalPendingRole = DepartmentAndPosition.HR_EMPLOYEE.getPosition();
		}
		// 3. 回傳判斷出的下一個待審核角色。
		return approvalPendingRole;
	}
	
	/**
	 * changeApplicationDtoToRecordDto
	 *
	 * 將員工資訊更新的申請 DTO 轉換為記錄 DTO。
	 * 此方法將原始的申請資料與審批相關的資訊（如審批結果、審批人等）結合，
	 * 建立一個用於記錄目的的物件。
	 *
	 * @param applicationDto 員工資訊更新的申請 DTO，包含原始申請詳情。
	 * @param req 審批更新資訊的請求物件，包含審批結果和拒絕原因。
	 * @param sessionEmployee 當前會話中執行審批操作的員工資訊。
	 * @return 轉換後的 UpdateEmployeeInfoRecordDto 物件，用於儲存審批記錄。
	 */
	private EmployeeApplicationRecordDto changeApplicationDtoToRecordDto(//
			EmployeeApplicationDto applicationDto, //
			ApproveEmployeeApplicationReq req, //
			EmployeeDto sessionEmployee) {
		// 1. 創建一個新的 UpdateEmployeeInfoRecordDto 物件，作為轉換後的記錄。
		EmployeeApplicationRecordDto recordDto = new EmployeeApplicationRecordDto();
		// 2. 將申請 DTO、請求和會話員工的相關資訊複製到記錄 DTO 中。
		recordDto.setApplicationId(applicationDto.getId());
		recordDto.setEmployeeId(applicationDto.getEmployeeId());
		recordDto.setComment(applicationDto.getComment());
		recordDto.setType(applicationDto.getType());
		recordDto.setData(applicationDto.getData());
		recordDto.setApplyDateTime(applicationDto.getApplyDateTime());
		recordDto.setSubmitUp(applicationDto.getSubmitUp());
		recordDto.setApproved(req.isApproved());
		recordDto.setApproverId(sessionEmployee.getId());
		recordDto.setApproverRole(applicationDto.getApprovalPendingRole());
		recordDto.setRejectionReason(req.getRejectionReason());
		recordDto.setApplicationGroup(applicationDto.getApplicationGroup());
		recordDto.setApprovedDateTime(LocalDateTime.now());
		recordDto.setApplyerId(applicationDto.getApplyerId());
		// 3. 回傳已填充所有相關資訊的記錄 DTO。
		return recordDto;
	}
	
	/**
	 * 排程在每日 3:00 a.m 時清除所有驗證信資料
	 */
	@Scheduled(cron = "0 0 3 * * *") // 設定排程：每天凌晨 3 點 0 分 0 秒執行。
	public void deleteVerification() {
		// 呼叫 emailVerificationDao 的 deleteAll 方法，清除所有驗證信資料。
		emailVerificationDao.deleteAll();
	}
}