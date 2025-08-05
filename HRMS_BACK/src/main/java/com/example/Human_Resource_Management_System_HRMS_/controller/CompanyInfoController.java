package com.example.Human_Resource_Management_System_HRMS_.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Human_Resource_Management_System_HRMS_.service.ifs.CompanyInfoService;
import com.example.Human_Resource_Management_System_HRMS_.vo.AddAnnualHolidayReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.AddFixedHolidayReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.BasicRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.CompanyInfoRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.FixedHolidayRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.GetAnnualHolidayRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateCompanyInfoReq;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@Tag(name = "公司資訊系統")
public class CompanyInfoController {

	@Autowired
	private CompanyInfoService companyInfoService;

	/**
	 * Controller1. 更新公司資訊 API<br>
	 * 此 API 只有 BOSS 可以用<br>
	 * API 的路徑: http://localhost:8080/HRMS/updateCompanyInfo
	 * 
	 * @param req
	 * @return
	 */
	@PutMapping(value = "HRMS/updateCompanyInfo")
	public BasicRes updateCompanyInfo(@Valid @RequestBody UpdateCompanyInfoReq req) {
		return companyInfoService.updateCompanyInfo(req);
	}

	/**
	 * Controller2. 查詢公司資訊 API<br>
	 * API 的路徑: http://localhost:8080/HRMS/checkCompanyInfo
	 * 
	 * @return
	 */
	@GetMapping(value = "HRMS/checkCompanyInfo")
	public CompanyInfoRes checkCompanyInfo() {
		return companyInfoService.checkCompanyInfo();
	}

	/**
	 * Controller3. 查詢公司的每年固定假期資訊<br>
	 * API 的路徑: http://localhost:8080/HRMS/getAllFixedHoliday
	 * 
	 * @return
	 */
	@GetMapping(value = "HRMS/getAllFixedHoliday")
	public FixedHolidayRes getAllFixedHoliday() {
		return companyInfoService.getAllFixedHoliday();
	}

	/**
	 * Controller4. 新增公司的每年固定假期<br>
	 * 僅有人資部門以及老闆有權限新增<br>
	 * API 的路徑: http://localhost:8080/HRMS/addFixedHoliday
	 * 
	 * @param addFixedHolidayList
	 * @return
	 */
	@PostMapping(value = "HRMS/addFixedHoliday")
	public BasicRes addFixedHoliday(@Valid @RequestBody List<AddFixedHolidayReq> addFixedHolidayList) {
		return companyInfoService.addFixedHoliday(addFixedHolidayList);
	}

	/**
	 * Controller5. 刪除公司的每年固定假期<br>
	 * 僅有人資部門以及老闆有權限刪除<br>
	 * API 的路徑: http://localhost:8080/HRMS/deleteFixedHoliday?month=O&day=O
	 * 
	 * @param month
	 * @param day
	 * @return
	 */
	@DeleteMapping(value = "HRMS/deleteFixedHoliday")
	public BasicRes deleteFixedHoliday(//
			@RequestParam("month") int month, //
			@RequestParam("day") int day) {
		return companyInfoService.deleteFixedHoliday(month, day);
	}

	/**
	 * Controller6. 新增公司的今年休假/補班日<br>
	 * 僅有人資部門以及老闆有權限新增<br>
	 * API 的路徑: http://localhost:8080/HRMS/addAnnualHoliday
	 * 
	 * @param addAnnualHolidayList
	 * @return
	 */
	@PostMapping(value = "HRMS/addAnnualHoliday")
	public BasicRes addAnnualHoliday(@Valid @RequestBody List<AddAnnualHolidayReq> addAnnualHolidayList) {
		return companyInfoService.addAnnualHoliday(addAnnualHolidayList);
	}

	/**
	 * Controller7. 查詢公司的年度行事曆 <br>
	 * API 的路徑: http://localhost:8080/HRMS/getAllAnnualHoliday
	 * 
	 * @return
	 */
	@GetMapping(value = "HRMS/getAllAnnualHoliday")
	public GetAnnualHolidayRes getAllAnnualHoliday() {
		return companyInfoService.getAllAnnualHoliday();
	}

	/**
	 * Controller8. 透過日期更新該日資訊 <br>
	 * 僅有人資部門以及老闆有權限更新<br>
	 * API 的路徑: http://localhost:8080/HRMS/updateAnnualHoliday
	 * 
	 * @param req
	 * @return
	 */
	@PutMapping(value = "HRMS/updateAnnualHoliday")
	public BasicRes updateAnnualHoliday(@Valid @RequestBody AddAnnualHolidayReq req) {
		return companyInfoService.updateAnnualHoliday(req);
	}

	/**
	 * Controller9. 透過日期刪除該日資訊 <br>
	 * 僅有人資部門以及老闆有權限刪除<br>
	 * API 的路徑: http://localhost:8080/HRMS/deleteAnnualHoliday?date=
	 * 
	 * @param date
	 * @return
	 */
	@DeleteMapping(value = "HRMS/deleteAnnualHoliday")
	public BasicRes deleteAnnualHoliday(@RequestParam("date") LocalDate date) {
		return companyInfoService.deleteAnnualHoliday(date);
	}

	/**
	 * Controller10. 清空年度假期資料 <br>
	 * 僅有人資主管以及老闆有權限刪除<br>
	 * API 的路徑: http://localhost:8080/HRMS/deleteAllAnnualHoliday
	 * 
	 * @return
	 */
	@DeleteMapping(value = "HRMS/deleteAllAnnualHoliday")
	public BasicRes deleteAllAnnualHoliday() {
		return companyInfoService.deleteAllAnnualHoliday();
	}

}
