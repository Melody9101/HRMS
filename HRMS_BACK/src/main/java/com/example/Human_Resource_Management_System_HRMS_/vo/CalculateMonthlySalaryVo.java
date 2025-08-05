package com.example.Human_Resource_Management_System_HRMS_.vo;

import java.math.BigDecimal;
import java.util.List;

import com.example.Human_Resource_Management_System_HRMS_.dto.AttendanceDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.LeaveRecordDto;

public class CalculateMonthlySalaryVo {

	// --- 員工與月份資訊 ---
	// 1. 員工ID。
	private Integer employeeId;

	// 2. 輸入的薪資計算年份。
	private Integer year;

	// 3. 輸入的薪資計算月份。
	private Integer month;

	// --- 薪資計算結果 ---
	// 4. 員工當月基本月薪。
	private BigDecimal baseSalary;

	// 5. 當月總請假扣薪金額。
	private BigDecimal totalDeductionForMonth;

	// 6. 輸入的當月獎金。
	private Integer bonus;

	// --- 僅計算12月時額外顯示。若非12月計算，數值為 BigDecimal.ZERO 或 null。 ---
	// 7. 去年剩餘年假折現金額。
	private BigDecimal previousAnnualLeaveConvertedPay;

	// 8. 去年剩餘年假天數。
	private BigDecimal remainingPreviousAnnualLeave;

	// 9. 最終計算出的當月薪資。
	private BigDecimal finalCalculatedSalary;

	// --- 詳細紀錄列表 ---
	// 10. 當月所有考勤打卡紀錄列表。
	private List<AttendanceDto> allMonthlyAttendanceRecords;

	// 11. 當月所有考勤異常紀錄列表 (如遲到、早退、缺卡等)。
	private List<AttendanceExceptionVo> abnormalAttendanceRecords;

	// 12. 當月所有已核准的請假紀錄列表。
	private List<LeaveRecordDto> allApprovedMonthlyLeaveRecords;

	public CalculateMonthlySalaryVo() {
		super();
	}

	public CalculateMonthlySalaryVo(Integer employeeId, Integer year, Integer month, BigDecimal baseSalary,
			BigDecimal totalDeductionForMonth, Integer bonus, BigDecimal previousAnnualLeaveConvertedPay,
			BigDecimal remainingPreviousAnnualLeave, BigDecimal finalCalculatedSalary,
			List<AttendanceDto> allMonthlyAttendanceRecords, List<AttendanceExceptionVo> abnormalAttendanceRecords,
			List<LeaveRecordDto> allApprovedMonthlyLeaveRecords) {
		super();
		this.employeeId = employeeId;
		this.year = year;
		this.month = month;
		this.baseSalary = baseSalary;
		this.totalDeductionForMonth = totalDeductionForMonth;
		this.bonus = bonus;
		this.previousAnnualLeaveConvertedPay = previousAnnualLeaveConvertedPay;
		this.remainingPreviousAnnualLeave = remainingPreviousAnnualLeave;
		this.finalCalculatedSalary = finalCalculatedSalary;
		this.allMonthlyAttendanceRecords = allMonthlyAttendanceRecords;
		this.abnormalAttendanceRecords = abnormalAttendanceRecords;
		this.allApprovedMonthlyLeaveRecords = allApprovedMonthlyLeaveRecords;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public BigDecimal getBaseSalary() {
		return baseSalary;
	}

	public void setBaseSalary(BigDecimal baseSalary) {
		this.baseSalary = baseSalary;
	}

	public BigDecimal getTotalDeductionForMonth() {
		return totalDeductionForMonth;
	}

	public void setTotalDeductionForMonth(BigDecimal totalDeductionForMonth) {
		this.totalDeductionForMonth = totalDeductionForMonth;
	}

	public Integer getBonus() {
		return bonus;
	}

	public void setBonus(Integer bonus) {
		this.bonus = bonus;
	}

	public BigDecimal getPreviousAnnualLeaveConvertedPay() {
		return previousAnnualLeaveConvertedPay;
	}

	public void setPreviousAnnualLeaveConvertedPay(BigDecimal previousAnnualLeaveConvertedPay) {
		this.previousAnnualLeaveConvertedPay = previousAnnualLeaveConvertedPay;
	}

	public BigDecimal getRemainingPreviousAnnualLeave() {
		return remainingPreviousAnnualLeave;
	}

	public void setRemainingPreviousAnnualLeave(BigDecimal remainingPreviousAnnualLeave) {
		this.remainingPreviousAnnualLeave = remainingPreviousAnnualLeave;
	}

	public BigDecimal getFinalCalculatedSalary() {
		return finalCalculatedSalary;
	}

	public void setFinalCalculatedSalary(BigDecimal finalCalculatedSalary) {
		this.finalCalculatedSalary = finalCalculatedSalary;
	}

	public List<AttendanceDto> getAllMonthlyAttendanceRecords() {
		return allMonthlyAttendanceRecords;
	}

	public void setAllMonthlyAttendanceRecords(List<AttendanceDto> allMonthlyAttendanceRecords) {
		this.allMonthlyAttendanceRecords = allMonthlyAttendanceRecords;
	}

	public List<AttendanceExceptionVo> getAbnormalAttendanceRecords() {
		return abnormalAttendanceRecords;
	}

	public void setAbnormalAttendanceRecords(List<AttendanceExceptionVo> abnormalAttendanceRecords) {
		this.abnormalAttendanceRecords = abnormalAttendanceRecords;
	}

	public List<LeaveRecordDto> getAllApprovedMonthlyLeaveRecords() {
		return allApprovedMonthlyLeaveRecords;
	}

	public void setAllApprovedMonthlyLeaveRecords(List<LeaveRecordDto> allApprovedMonthlyLeaveRecords) {
		this.allApprovedMonthlyLeaveRecords = allApprovedMonthlyLeaveRecords;
	}
}
