package com.example.Human_Resource_Management_System_HRMS_.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Human_Resource_Management_System_HRMS_.service.ifs.LeaveService;
import com.example.Human_Resource_Management_System_HRMS_.vo.ApplyLeaveReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.ApproveLeaveReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.BasicRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.CheckApplicationRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.CheckRecordRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.GetApprovedReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.LeaveSearchReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.RemainingPreviousAnnualLeaveRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.SearchLeaveRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateApplicationReq;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 釋出給前端的請假系統 RESTful 架構
 */
@CrossOrigin
@RestController
@Tag(name = "員工請假系統")
public class LeaveController {

	@Autowired
	private LeaveService leaveService;

	/**
	 * Controller1. 員工請假申請<br>
	 * API 路徑為: http://localhost:8080/HRMS/applyLeave
	 */
	@PostMapping(value = "HRMS/applyLeave")
	public BasicRes applyLeave(@Valid @RequestBody ApplyLeaveReq req) {
		return leaveService.applyLeave(req);
	}

	/**
	 * Controller2. 審核假單<br>
	 * 僅有人資部門或是主管以上職位可以調用此 API<br>
	 * API路徑為: http://localhost:8080/HRMS/approveLeave
	 * 
	 * @throws Exception
	 */
	@PostMapping(value = "HRMS/approveLeave")
	public BasicRes approveLeave(@Valid @RequestBody ApproveLeaveReq req) throws Exception {
		return leaveService.approveLeave(req);
	}

	/**
	 * Controller3. 員工補證明文件 <br>
	 * 僅有申請者本人能使用此 API，會透過 session 來檢查<br>
	 * 當前狀態為待補文件才有辦法更新<br>
	 * API 路徑為: http://localhost:8080/HRMS/updateLeaveApplicationCertificate
	 * 
	 * @return
	 */
	@PostMapping(value = "HRMS/updateLeaveApplicationCertificate")
	public BasicRes updateLeaveApplicationCertificate(@Valid @RequestBody UpdateApplicationReq req) {
		return leaveService.updateLeaveApplication(req);
	}

	/**
	 * Controller4. 搜尋假單歷史紀錄<br>
	 * 若非本人查詢，則僅有老闆以上職位以及人資部門能夠查詢<br>
	 * 如果不是老闆或人資部門，無法跨部門查詢<br>
	 * API路徑為: http://localhost:8080/HRMS/searchLeaveRecordByEmployeeIdAndDate
	 * 
	 */
	@PostMapping(value = "HRMS/searchLeaveRecordByEmployeeIdAndDate")
	public SearchLeaveRes searchLeaveRecordByEmployeeIdAndDate(@Valid @RequestBody LeaveSearchReq req)
			throws Exception {
		return leaveService.searchLeaveRecord(req);
	}

	/**
	 * Controller5. 取消休假申請<br>
	 * 僅有人資主管以及老闆有權限使用此API<br>
	 * 需先自行透過 searchApi 抓出有哪些假單更改<br>
	 * 會透過假單的申請人檢查是否更動的對象是否為同一人<br>
	 * API路徑為: http://localhost:8080/HRMS/cancelLeaveApplication?leaveIdList=
	 * 
	 * @throws Exception
	 */
	@PostMapping(value = "HRMS/cancelLeaveApplication")
	public BasicRes cancelLeaveApplication(@RequestParam("leaveIdList") List<Integer> leaveIdList) throws Exception {
		return leaveService.cancelLeaveApplication(leaveIdList);
	}

	/**
	 * Controller6. 確認有沒有尚未審核的請假申請<br>
	 * 由於有設定登入 AOP ，這邊不需要帶任何值進來<br>
	 * 會透過登入的 session 的權限來搜尋並傳送資料<br>
	 * 僅有老闆、各部門主管以及人資部門有權限可以使用此API<br>
	 * API路徑為: http://localhost:8080/HRMS/checkLeaveApplication
	 * 
	 * @return
	 */
	@GetMapping(value = "HRMS/checkLeaveApplication")
	public CheckApplicationRes checkLeaveApplication() {
		return leaveService.checkLeaveApplication();
	}

	/**
	 * Controller7. 取得尚有剩餘去年年假或是有薪病假的名單<br>
	 * 僅有老闆或人資部門有辦法使用此 API<br>
	 * API路徑為: http://localhost:8080/HRMS/checkRemainingPreviousLeaveList
	 * 
	 * @return
	 */
	@GetMapping(value = "HRMS/checkRemainingPreviousLeaveList")
	public RemainingPreviousAnnualLeaveRes checkRemainingPreviousLeaveList() {
		return leaveService.checkRemainingPreviousAnnualLeaveList();
	}

	/**
	 * Controller8. 取得該名員工該月請假成功紀錄<br>
	 * 若非本人，一般部門的一般職員沒辦法使用此 API<br>
	 * API 的路徑:
	 * http://localhost:8080/HRMS/getApprovedLeaveRecordsByEmployeeIdAndYYMM
	 * 
	 * @param req
	 * @return
	 */
	@PostMapping(value = "HRMS/getApprovedLeaveRecordsByEmployeeIdAndYYMM")
	public CheckRecordRes getApprovedLeaveRecordsByEmployeeIdAndYYMM(@Valid @RequestBody GetApprovedReq req) {
		return leaveService.getApprovedLeaveRecordsByEmployeeIdAndYYMM(req);
	}

	/**
	 * Controller9. 透過年度與月份確認所有員工該月的請假成功紀錄<br>
	 * 一般部門的員工沒辦法使用此 API<br>
	 * * API 的路徑:
	 * http://localhost:8080/HRMS/getAllApprovedLeaveRecordsByYYMM?year=00&month=00
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	@PostMapping(value = "HRMS/getAllApprovedLeaveRecordsByYYMM")
	public CheckRecordRes getAllApprovedLeaveRecordsByYYMM(//
			@RequestParam("year") int year, //
			@RequestParam("month") int month) {
		return leaveService.getAllApprovedLeaveRecordsByYYMM(year, month);
	}
}
