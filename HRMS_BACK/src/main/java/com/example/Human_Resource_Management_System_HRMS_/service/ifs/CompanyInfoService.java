package com.example.Human_Resource_Management_System_HRMS_.service.ifs;

import java.time.LocalDate;
import java.util.List;

import com.example.Human_Resource_Management_System_HRMS_.vo.AddAnnualHolidayReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.AddFixedHolidayReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.BasicRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.CompanyInfoRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.FixedHolidayRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.GetAnnualHolidayRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateCompanyInfoReq;

public interface CompanyInfoService {

	// Controller1. 更新公司資訊 API
	public BasicRes updateCompanyInfo(UpdateCompanyInfoReq req);

	// Controller2. 查詢公司資訊 API
	public CompanyInfoRes checkCompanyInfo();

	// Controller3. 查詢公司的每年固定假期資訊
	public FixedHolidayRes getAllFixedHoliday();

	// Controller4. 新增公司的每年固定假期
	public BasicRes addFixedHoliday(List<AddFixedHolidayReq> addFixedHolidayList);

	// Controller5. 刪除公司的每年固定假期
	public BasicRes deleteFixedHoliday(int month, int day);

	// Controller6. 新增公司的今年休假/補班日
	public BasicRes addAnnualHoliday(List<AddAnnualHolidayReq> addAnnualHolidayList);

	// Controller7. 查詢公司的年度行事曆
	public GetAnnualHolidayRes getAllAnnualHoliday();

	// Controller8. 透過日期更新該日資訊
	public BasicRes updateAnnualHoliday(AddAnnualHolidayReq req);

	// Controller9. 透過日期刪除該日資訊
	public BasicRes deleteAnnualHoliday(LocalDate date);

	// Controller10. 透過日期刪除該日資訊
	public BasicRes deleteAllAnnualHoliday();

}
