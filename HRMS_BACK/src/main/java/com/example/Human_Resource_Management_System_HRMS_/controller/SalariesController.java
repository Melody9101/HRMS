package com.example.Human_Resource_Management_System_HRMS_.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Human_Resource_Management_System_HRMS_.service.ifs.SalariesService;
import com.example.Human_Resource_Management_System_HRMS_.vo.AddSalariesReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.BasicRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.CalculateMonthlySalaryRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.GetAllSalariesOrByEmployeeIdListRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.GetSalaryByEmployeeIdYYMMRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateSalariesByIdReq;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@Tag(name = "薪資系統")
public class SalariesController {

	@Autowired
	private SalariesService salariesService;

	/**
	 * Controller1: addSalaries 新增薪資單。<br>
	 * 會計部門、老闆才可以使用。<br>
	 * API路徑: http://localhost:8080/HRMS/addSalaries
	 */
	@PostMapping(value = "HRMS/addSalaries")
	public BasicRes addSalaries(@Valid @RequestBody AddSalariesReq req) {
		return salariesService.addSalaries(req);
	}

	/**
	 * Controller2: updateSalariesById 透過薪資單ID更新(修改)薪資單內容。<br>
	 * 僅會計部門的"會計主管"可使用。<br>
	 * API路徑: http://localhost:8080/HRMS/updateSalariesById
	 */
	@PostMapping(value = "HRMS/updateSalariesById")
	public BasicRes updateSalariesById(@Valid @RequestBody UpdateSalariesByIdReq req) {
		return salariesService.updateSalariesById(req);
	}

	/**
	 * Controller3: getAllSalariesOrByEmployeeIdList: 透過員編查詢員工薪水。<br>
	 * 會計部門及老闆使用。<br>
	 * API 路徑:
	 * http://localhost:8080/HRMS/getAllSalariesOrByEmployeeIdList?idList=&year=&month=
	 */
	@PostMapping(value = "HRMS/getAllSalariesOrByEmployeeIdList")
	public GetAllSalariesOrByEmployeeIdListRes getAllSalariesOrByEmployeeIdList(//
			@RequestParam("idList") List<Integer> idList, //
			@RequestParam("year") int year, //
			@RequestParam("month") Integer month) {
		return salariesService.getAllSalariesOrByEmployeeIdList(idList, year, month);
	}

	/**
	 * Controller4: 員工查詢薪資單。<br>
	 * 員工使用。<br>
	 * API 路徑: http://localhost:8080/HRMS/getSalaryByEmployeeIdYYMM?year=&month=
	 */
	@PostMapping(value = "HRMS/getSalaryByEmployeeIdYYMM")
	public GetSalaryByEmployeeIdYYMMRes getSalaryByEmployeeIdYYMM(//
			@RequestParam("year") int year, //
			@RequestParam("month") Integer month) {
		return salariesService.getSalaryByEmployeeIdYYMM(year, month);
	}

	/**
	 * 方法5: calculateMonthlySalaryByEmployeeIdYYMMBonus: 用以輔助的薪資計算功能。<br>
	 * 僅會計部門可使用。<br>
	 * 
	 * 權限限制: <br>
	 * (1) 一般員工無權計算主管級以上員工薪資。<br>
	 * (2) 主管級可計算全部員工薪資。<br>
	 * 
	 * API 路徑:
	 * http://localhost:8080/HRMS/calculateMonthlySalaryByEmployeeIdYYMMBonus?employeeId=&year=&month=&bonus=<br>
	 * 
	 * @param employeeId 需計算當月薪資的員工ID。
	 * @param year       需計算的年份。
	 * @param month      需計算的月份。
	 * @param bonus      當月獎金。
	 * 
	 * @return CalculateMonthlySalaryRes
	 */
	@PostMapping(value = "HRMS/calculateMonthlySalaryByEmployeeIdYYMMBonus")
	public CalculateMonthlySalaryRes calculateMonthlySalaryByEmployeeIdYYMMBonus(//
			@RequestParam("employeeId") int employeeId, //
			@RequestParam("year") int year, //
			@RequestParam("month") int month, //
			@RequestParam("bonus") int bonus) {
		return salariesService.calculateMonthlySalaryByEmployeeIdYYMMBonus(employeeId, year, month, bonus);
	}
}
