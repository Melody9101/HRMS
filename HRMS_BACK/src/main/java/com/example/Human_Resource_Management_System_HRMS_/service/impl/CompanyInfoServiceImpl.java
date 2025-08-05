package com.example.Human_Resource_Management_System_HRMS_.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.Human_Resource_Management_System_HRMS_.constants.DepartmentType;
import com.example.Human_Resource_Management_System_HRMS_.constants.ResMessage;
import com.example.Human_Resource_Management_System_HRMS_.dao.AnnualHolidayDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.CompanyInfoDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.EmployeeDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.FixedHolidayDao;
import com.example.Human_Resource_Management_System_HRMS_.dto.AnnualHolidayDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.CompanyInfoDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.EmployeeDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.FixedHolidayDto;
import com.example.Human_Resource_Management_System_HRMS_.service.ifs.CompanyInfoService;
import com.example.Human_Resource_Management_System_HRMS_.vo.AddAnnualHolidayReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.AddFixedHolidayReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.BasicRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.CompanyInfoRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.FixedHolidayRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.GetAnnualHolidayRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateCompanyInfoReq;

import jakarta.servlet.http.HttpSession;

@Service
public class CompanyInfoServiceImpl implements CompanyInfoService {

	// logger 是用來記錄到日誌用的，可加可不加
	// import slf4j 的 Logger
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private FixedHolidayDao fixedHolidayDao;

	@Autowired
	private AnnualHolidayDao annualHolidayDao;

	@Autowired
	private CompanyInfoDao companyInfoDao;

	/**
	 * Controller1. 更新公司資訊 API<br>
	 * 此 API 只有 BOSS 可以用<br>
	 */
	@Override
	public BasicRes updateCompanyInfo(UpdateCompanyInfoReq req) {
		// 確認登入者的權利，如果不是 BOSS 就拋出錯誤
		EmployeeDto sessionEmployee = getSessionEmployee();
		if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
			// logger 是用來記錄到日誌用的，可加可不加
			// logger 還可以接收 try catch 拋出的紀錄
			// logger 要在 exception.GlobalExceptionHandler 裡面額外設定才可以接收在 req 裡定義的 valid
			// 拋出的錯誤訊息
			logger.info(ResMessage.GRADE_INSUFFICIENT.getMessage());

			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		CompanyInfoDto dto = new CompanyInfoDto();

		dto.setId(1); // 更新固定 ID 直接寫死
		dto.setName(req.getName());
		dto.setTaxIdNumber(req.getTaxIdNumber());
		dto.setOwnerName(req.getOwnerName());
		dto.setPhone(req.getPhone());
		dto.setEmail(req.getEmail());
		dto.setAddress(req.getAddress());
		dto.setWebsite(req.getWebsite());
		dto.setEstablishmentDate(req.getEstablishmentDate());
		dto.setCapitalAmount(req.getCapitalAmount());
		dto.setEmployeeCount(req.getEmployeeCount());
		dto.setStatus(req.getStatus());
		dto.setUpdateAt(LocalDateTime.now());
		dto.setWorkStartTime(req.getWorkStartTime());
		dto.setLunchStartTime(req.getLunchStartTime());
		dto.setLunchEndTime(req.getLunchEndTime());
		dto.setTimezone(req.getTimezone());

		companyInfoDao.updateCompanyInfo(dto);

		return new BasicRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage());
	}

	/**
	 * Controller2. 查詢公司資訊 API
	 */
	@Override
	public CompanyInfoRes checkCompanyInfo() {
		CompanyInfoDto companyInfo = companyInfoDao.checkCompanyInfo();
		return new CompanyInfoRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), companyInfo);
	}

	/**
	 * Controller3. 查詢公司的每年固定假期資訊
	 */
	@Override
	public FixedHolidayRes getAllFixedHoliday() {
		List<FixedHolidayDto> dtoList = fixedHolidayDao.getAllFixedHoliday();

		return new FixedHolidayRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), dtoList);
	}

	/**
	 * Controller4. 新增公司的每年固定假期<br>
	 * 僅有人資部門以及老闆有權限新增
	 */
	@Override
	public BasicRes addFixedHoliday(List<AddFixedHolidayReq> addFixedHolidayList) {
		// 確認登入者的權利，如果不是人資部門或是 BOSS 就拋出錯誤
		EmployeeDto sessionEmployee = getSessionEmployee();
		if (!sessionIsHrOrBoss(sessionEmployee)) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		/**
		 * 轉換為 Set ，將重複的 month, day 自動去除<br>
		 * 在 MonthDayReq 中有複寫 equals 與 hashCode ，才可以辦到<br>
		 * 如果 Set 的大小不等於 List 的大小，則代表有重複，拋出錯誤
		 */
		Set<AddFixedHolidayReq> addFixedHolidaySet = new HashSet<>(addFixedHolidayList);
		if (addFixedHolidaySet.size() != addFixedHolidayList.size()) {
			return new BasicRes(ResMessage.DUPLICATE_DATE_ERROR.getCode(), //
					ResMessage.DUPLICATE_DATE_ERROR.getMessage());
		}

		/**
		 * 確認前端輸入的陣列中沒有重複後，將資料庫中的資料取出，比對有無重複<br>
		 * 若有重複則拋出錯誤
		 */
		List<FixedHolidayDto> dtoList = fixedHolidayDao.getAllFixedHoliday();
		Set<MonthDay> dtoSet = dtoList.stream().map(dto -> MonthDay.of(dto.getMonth(), dto.getDay()))
				.collect(Collectors.toSet());

		List<FixedHolidayDto> fixedHolidayList = new ArrayList<>();
		for (AddFixedHolidayReq item : addFixedHolidayList) {
			MonthDay md = MonthDay.of(item.getMonth(), item.getDay());
			if (dtoSet.contains(md)) {
				return new BasicRes(ResMessage.DUPLICATE_DATE_ERROR.getCode(), //
						ResMessage.DUPLICATE_DATE_ERROR.getMessage());
			}

			FixedHolidayDto dto = new FixedHolidayDto();
			dto.setMonth(item.getMonth());
			dto.setDay(item.getDay());
			dto.setName(item.getName());

			fixedHolidayList.add(dto);
		}
		// 確認皆無重複後存進資料庫中
		fixedHolidayDao.insertFixedHoliday(fixedHolidayList);

		return new BasicRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage());
	}

	/**
	 * Controller5. 刪除公司的每年固定假期<br>
	 * 僅有人資部門以及老闆有權限刪除
	 */
	@Override
	public BasicRes deleteFixedHoliday(int month, int day) {
		// 確認登入者的權利，如果不是人資部門或是 BOSS 就拋出錯誤
		EmployeeDto sessionEmployee = getSessionEmployee();
		if (!sessionIsHrOrBoss(sessionEmployee)) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 確認帶入的值，如果 month 或是 day 格式不對，則拋出錯誤
		if (month < 1 || month > 12) {
			return new BasicRes(ResMessage.PARAM_MONTH_ERROR.getCode(), //
					ResMessage.PARAM_MONTH_ERROR.getMessage());
		}

		if (day < 1 || day > 31) {
			return new BasicRes(ResMessage.PARAM_DAY_ERROR.getCode(), //
					ResMessage.PARAM_DAY_ERROR.getMessage());
		}

		int count = fixedHolidayDao.deleteHoliday(month, day);
		if (count != 1) {
			return new BasicRes(ResMessage.FAILED.getCode(), //
					ResMessage.FAILED.getMessage());
		}
		return new BasicRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage());
	}

	/**
	 * Controller6. 新增公司的今年休假/補班日<br>
	 * 僅有人資部門以及老闆有權限新增
	 */
	@Override
	public BasicRes addAnnualHoliday(List<AddAnnualHolidayReq> addAnnualHolidayList) {
		// 確認登入者的權利，如果不是人資部門或是 BOSS 就拋出錯誤
		EmployeeDto sessionEmployee = getSessionEmployee();
		if (!sessionIsHrOrBoss(sessionEmployee)) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 將使用者帶入的日期放到 Set 中，比對筆數是否一致，如果有差代表有重複，則拋出錯誤
		Set<LocalDate> checkSet = new HashSet<>();
		addAnnualHolidayList.forEach(item -> checkSet.add(item.getDate()));
		if (checkSet.size() != addAnnualHolidayList.size()) {
			return new BasicRes(ResMessage.DUPLICATE_DATE_ERROR.getCode(), //
					ResMessage.DUPLICATE_DATE_ERROR.getMessage());
		}

		// 從資料庫取出已經存在的假日（只取日期）
		List<AnnualHolidayDto> dtoList = annualHolidayDao.getAllAnnualHoliday();
		Set<LocalDate> dbDateSet = new HashSet<>();
		for (AnnualHolidayDto dto : dtoList) {
			dbDateSet.add(dto.getDate());
		}
		List<AnnualHolidayDto> forDbData = new ArrayList<>();
		// 比對資料庫的資料是否有和使用者輸入的日期重複，若有則拋出錯誤
		for (AddAnnualHolidayReq item : addAnnualHolidayList) {
			if (dbDateSet.contains(item.getDate())) {
				return new BasicRes(ResMessage.DUPLICATE_DATE_ERROR.getCode(), //
						ResMessage.DUPLICATE_DATE_ERROR.getMessage());
			}

			AnnualHolidayDto dto = new AnnualHolidayDto();

			dto.setDate(item.getDate());
			dto.setName(item.getName());
			dto.setHoliday(item.isHoliday());
			dto.setCeateBy(sessionEmployee.getId());
			dto.setCreateAt(LocalDateTime.now());

			forDbData.add(dto);
		}

		annualHolidayDao.addAnnualHoliday(forDbData);

		return new BasicRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage());
	}

	/**
	 * Controller7. 查詢公司的年度行事曆
	 */
	@Override
	public GetAnnualHolidayRes getAllAnnualHoliday() {
		List<AnnualHolidayDto> annualHolidayList = annualHolidayDao.getAllAnnualHoliday();
		return new GetAnnualHolidayRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), annualHolidayList);
	}

	/**
	 * Controller8. 透過日期更新該日資訊 <br>
	 * 僅有人資部門以及老闆有權限更新<br>
	 */
	@Override
	public BasicRes updateAnnualHoliday(AddAnnualHolidayReq req) {
		// 確認登入者的權利，如果不是人資部門或是 BOSS 就拋出錯誤
		EmployeeDto sessionEmployee = getSessionEmployee();
		if (!sessionIsHrOrBoss(sessionEmployee)) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 將資料更新至資料庫
		AnnualHolidayDto dto = new AnnualHolidayDto();
		dto.setDate(req.getDate());
		dto.setName(req.getName());
		dto.setHoliday(req.isHoliday());
		dto.setCeateBy(sessionEmployee.getId());
		dto.setCreateAt(LocalDateTime.now());

		int count = annualHolidayDao.updateAnnualHoliday(dto);

		if (count != 1) {
			return new BasicRes(ResMessage.FAILED.getCode(), //
					ResMessage.FAILED.getMessage());
		}

		return new BasicRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage());
	}

	/**
	 * Controller9. 透過日期刪除該日資訊 <br>
	 * 僅有人資部門以及老闆有權限刪除
	 */
	@Override
	public BasicRes deleteAnnualHoliday(LocalDate date) {
		// 確認登入者的權利，如果不是人資部門或是 BOSS 就拋出錯誤
		EmployeeDto sessionEmployee = getSessionEmployee();
		if (!sessionIsHrOrBoss(sessionEmployee)) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 刪除資料
		int count = annualHolidayDao.deleteAnnualHoliday(date);
		if (count != 1) {
			return new BasicRes(ResMessage.FAILED.getCode(), //
					ResMessage.FAILED.getMessage());
		}
		return new BasicRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage());
	}

	/**
	 * Controller10. 透過日期刪除該日資訊 <br>
	 * 僅有人資主管以及老闆有權限刪除<br>
	 */
	@Override
	public BasicRes deleteAllAnnualHoliday() {
		// 確認登入者的權利，如果不是人資部門或是 BOSS 就拋出錯誤
		EmployeeDto sessionEmployee = getSessionEmployee();
		if (!sessionIsHrOrBoss(sessionEmployee)) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 刪除資料
		annualHolidayDao.deleteAllAnnualHoliday();
		return new BasicRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage());
	}

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

	/**
	 * sessionIsHrOrBoss: 檢查目前登入者是否為人資部門或老闆。
	 * 
	 * @param sessionEmployee 目前登入者資訊包。
	 * @return 若傳入的EmployeeDto sessionEmployee目前登入者不為HR人資部門 或 BOSS老闆
	 *         則回傳false，是則回傳true。
	 */
	private boolean sessionIsHrOrBoss(EmployeeDto sessionEmployee) {
		// 登入者是否為人資部門或老闆，若皆不是(不成立)回傳false，若皆是(成立)回傳true。
		boolean isHrOrBoss = true;
		DepartmentType type = DepartmentType.fromType(sessionEmployee.getDepartment());

		switch (type) {
		case HR:
		case BOSS:
			isHrOrBoss = true;
			break;
		case ACCOUNTANT:
		case GENERAL_AFFAIRS:
		default:
			isHrOrBoss = false;
			break;
		}
		return isHrOrBoss;
	}

}