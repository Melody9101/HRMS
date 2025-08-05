package com.example.Human_Resource_Management_System_HRMS_.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Human_Resource_Management_System_HRMS_.constants.ResMessage;
import com.example.Human_Resource_Management_System_HRMS_.dao.EmployeeDao;
import com.example.Human_Resource_Management_System_HRMS_.service.ifs.EmployeeService;
import com.example.Human_Resource_Management_System_HRMS_.vo.AddEmployeeReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.ApproveEmployeeApplicationReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.BasicRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.EmployeeBasicInfoRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.EmployeeListRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.EmployeeRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.EmployeeResignationReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.LoginReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.ReinstatementEmployeeReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.SearchEmployeeByEmailAndPhoneReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.SubmitEmployeeApplicationReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateEmployeeBasicInfoReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateEmployeeJobReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateEmployeePasswordReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateInfoApplicationRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdatePwdByEmailReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.updateEmployeeUnpaidLeaveReq;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@Tag(name = "員工管理系統")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private EmployeeDao employeeDao;

	/**
	 * Controller1: 新增員工資訊。<br>
	 * 僅有老闆以及人資部門有權限可以新增<br>
	 * 一組信箱只能創建一筆資料<br>
	 * 預設密碼為員工信箱<br>
	 * 此 API 路徑: http://localhost:8080/HRMS/addEmployee
	 * 
	 * @param req
	 * @return
	 */
	@PostMapping(value = "HRMS/addEmployee")
	public BasicRes addEmployee(@Valid @RequestBody AddEmployeeReq req) {
		return employeeService.addEmployee(req);
	}

	/**
	 * Controller2: 透過員編查詢員工資料。<br>
	 * 若查詢對象非本人，僅有老闆以及人資部門以及主管有權限可以使用<br>
	 * 無法查詢比自己職位高的員工<br>
	 * 僅有人資主管可以跨部門查詢，一般部門主管僅能查詢同部門的員工資訊<br>
	 * API 的路徑: http://localhost:8080/HRMS/selectByEmployeeIdList?idList=
	 * 
	 * @param idList
	 * @return
	 */
	@PostMapping(value = "HRMS/selectByEmployeeIdList")
	public EmployeeListRes selectByEmployeeIdList(@RequestParam("idList") List<Integer> idList) {
		return employeeService.selectByEmployeeIdList(idList);
	}

	/**
	 * Controller: 透過員編編輯員工基本資訊。<br>
	 * 僅有老闆以及人資部門有權限可以變更<br>
	 * 若變更人不為人資主管以上的階級，不得變更主管以上階級的資料<br>
	 * 僅有老闆階級能夠更改老闆階級的資料<br>
	 * API 的路徑: http://localhost:8080/HRMS/updateEmployeeBasicInfo
	 * 
	 * @param req
	 * @return
	 */
	@PostMapping(value = "HRMS/updateEmployeeBasicInfo")
	public BasicRes updateEmployeeBasicInfo(@Valid @RequestBody UpdateEmployeeBasicInfoReq req) {
		return employeeService.updateEmployeeBasicInfo(req);
	}

	/**
	 * Controller: 透過員編編輯員工職等、調部門和薪資資訊。<br>
	 * 僅有老闆以及人資主管有權限可以變更<br>
	 * 僅有老闆階級能夠更改老闆階級的資料<br>
	 * API 的路徑: http://localhost:8080/HRMS/updateEmployeeJob
	 * 
	 * @param req
	 * @return
	 */
	@PostMapping(value = "HRMS/updateEmployeeJob")
	public BasicRes updateEmployeeJob(@Valid @RequestBody UpdateEmployeeJobReq req) {
		return employeeService.updateEmployeeJob(req);
	}

	/**
	 * Controller: 透過員編處理員工離職。<br>
	 * 需填寫離職日期、離職理由。<br>
	 * 離職日期不得為未來日期。<br>
	 * 離職後薪水將被自動歸零salaries = 0、雇傭狀態也將自動更新為離職is_employed = false。<br>
	 * 僅有人資主管、老闆階級才有辦法使用此 API<br>
	 * 僅有老闆階級能夠更改老闆階級的資料<br>
	 * API 的路徑: http://localhost:8080/HRMS/resignEmployee
	 * 
	 * @param req
	 * @return
	 */
	@PostMapping(value = "HRMS/resignEmployee")
	public BasicRes resignEmployee(@Valid @RequestBody EmployeeResignationReq req) {
		return employeeService.resignEmployee(req);
	}

	/**
	 * Controller3: 透過信箱以及手機查詢已離職員工資料。<br>
	 * 僅有人資部門、老闆階級才有辦法使用此 API<br>
	 * API 的路徑: http://localhost:8080/HRMS/searchDepartedEmployee
	 * 
	 * @param req
	 * @return
	 */
	@PostMapping(value = "HRMS/searchDepartedEmployee")
	public EmployeeRes searchDepartedEmployee(@Valid @RequestBody SearchEmployeeByEmailAndPhoneReq req) {
		return employeeService.searchEmployeeByEmailAndPhone(req);
	}

	/**
	 * Controller4: 員工復職。<br>
	 * 需先透過 Controller6 將已離職員工的 id 取出，並在呼叫此 API 時帶入<br>
	 * 僅有人資部門、老闆階級才有辦法使用此 API<br>
	 * 密碼會重置為預設密碼<br>
	 * API 的路徑: http://localhost:8080/HRMS/reinstatementEmployee
	 */
	@PostMapping(value = "HRMS/reinstatementEmployee")
	public BasicRes reinstatementEmployee(@Valid @RequestBody ReinstatementEmployeeReq req) {
		return employeeService.reinstatementEmployee(req);
	}

	/**
	 * Controller5: 更新員工密碼。<br>
	 * 僅有本人有權限更改密碼<br>
	 * API 的路徑: http://localhost:8080/HRMS/updateEmployeePassword
	 * 
	 * @param req
	 * @return
	 */
	@PostMapping(value = "HRMS/updateEmployeePassword")
	public BasicRes updateEmployeePassword(@Valid @RequestBody UpdateEmployeePasswordReq req) {
		return employeeService.updateEmployeePassword(req);
	}

	/**
	 * Controller6: 忘記密碼。<br>
	 * 僅有本人有權限更改密碼<br>
	 * API 的路徑: http://localhost:8080/HRMS/updatePwdByEmail
	 * 
	 * @param email
	 * @return
	 */
	@PostMapping(value = "HRMS/updatePwdByEmail")
	public BasicRes updatePwdByEmail(@Valid @RequestBody UpdatePwdByEmailReq req) {
		return employeeService.updatePwdByEmail(req);
	}

	/**
	 * Controller7: 更新員工無薪假、留職停薪等資訊。<br>
	 * 僅有人資主管、老闆階級才有辦法使用此 API<br>
	 * API 的路徑: http://localhost:8080/HRMS/updateEmployeeUnpaidLeaveById
	 * 
	 * @param req
	 * @return
	 */
	@PostMapping(value = "HRMS/updateEmployeeUnpaidLeaveById")
	public BasicRes updateEmployeeUnpaidLeaveById(@Valid @RequestBody updateEmployeeUnpaidLeaveReq req) {
		return employeeService.updateEmployeeUnpaidLeaveById(req);
	}

	/**
	 * Controller8: 清空員工無薪假紀錄<br>
	 * 僅有人資主管、老闆階級才有辦法使用此 API<br>
	 * API 的路徑:
	 * http://localhost:8080/HRMS/reinstateEmployeeUnpaidLeaveById?employeeId=
	 * 
	 * @param employeeId
	 * @return
	 */
	@PostMapping(value = "HRMS/reinstateEmployeeUnpaidLeaveById")
	public BasicRes reinstateEmployeeUnpaidLeaveById(@RequestParam("employeeId") int employeeId) {
		return employeeService.reinstateEmployeeUnpaidLeaveById(employeeId);
	}

	/**
	 * Controller9: 取得所有在職員工的各項資料。<br>
	 * 一般部門無法使用此 API<br>
	 * API 路徑: http://localhost:8080/HRMS/getAllEmployees
	 * 
	 * @return
	 */
	@GetMapping(value = "HRMS/getAllEmployees")
	public EmployeeListRes getAllEmployees() {
		return employeeService.getAllEmployees();
	}

	/**
	 * Controller10: 確認當前是否有登入中的帳號。<br>
	 * 呼叫 Controller14- 登入 API 前，請先呼叫此 API 做確認，否則在使用登入的 API 時會報錯<br>
	 * API 路徑: http://localhost:8080/HRMS/checkLogin
	 * 
	 * @return
	 */
	@GetMapping(value = "HRMS/checkLogin")
	public EmployeeRes checkLogin() {
		return employeeService.checkLogin();
	}

	/**
	 * Controller11: 登入 API<br>
	 * 呼叫此 API 前請先呼叫 Controller13 確認登入中的API，如果沒有先將先前已登入之帳號登出會報錯<br>
	 * API 路徑: http://localhost:8080/HRMS/login
	 * 
	 * @param req
	 * @param session
	 * @return
	 */
	@PostMapping(value = "HRMS/login")
	public BasicRes login(@Valid @RequestBody LoginReq req, HttpSession session) {

		BasicRes res = employeeService.login(req);
		// 判斷帳密是否驗證成功
		if (res.getCode() == 200) {
			int sessionAccountId = employeeDao.selectByEmployeeEmail(req.getAccount()).getId();
			// 帳密驗證成功，使用 session 暫存一些資訊，後續需要先登入後才能使用的 API 可藉此判斷是否成功登入
			session.setAttribute("accountId", sessionAccountId);
			session.setAttribute("sessionId", session.getId());
			// 可設定 session 的存活時間，預設是30分鐘(1800秒)
			session.setMaxInactiveInterval(1800);
		}
		return res;
	}

	/**
	 * Controller12: 登出 API<br>
	 * API 路徑: http://localhost:8080/HRMS/logout
	 * 
	 * @param session
	 * @return
	 */
	@GetMapping(value = "HRMS/logout")
	public BasicRes logout(HttpSession session) {
		// 讓 session 失效
		session.invalidate();
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	/**
	 * Controller13: 發送驗證信(忘記密碼用)<br>
	 * API 路徑: http://localhost:8080/HRMS/sendVerificationLetter
	 * 
	 * @param email
	 * @return
	 */
	@PostMapping(value = "HRMS/sendVerificationLetter")
	public BasicRes sendVerificationLetter(@RequestParam("email") String email) {
		return employeeService.sendVerificationLetter(email);
	}

	/**
	 * Controller14: 認證驗證碼(忘記密碼用)<br>
	 * API 路徑: http://localhost:8080/HRMS/checkVerification
	 * 
	 * @param email
	 * @param code
	 * @return
	 */
	@PostMapping(value = "HRMS/checkVerification")
	public BasicRes checkVerification(//
			@RequestParam("email") String email, //
			@RequestParam("code") String code) {
		return employeeService.checkVerification(email, code);
	}

	/**
	 * Controller15: 確認同部門的所有員工清單<br>
	 * 僅有主管級別有辦法使用<br>
	 * API 路徑: http://localhost:8080/HRMS/getDepartmentEmployeesList
	 * 
	 * @return
	 */
	@GetMapping(value = "HRMS/getDepartmentEmployeesList")
	public EmployeeBasicInfoRes getDepartmentEmployeesList() {
		return employeeService.getDepartmentEmployeesList();
	}

	/**
	 * Controller16: 提交員工資料更新申請<br>
	 * API 路徑: http://localhost:8080/HRMS/submitEmployeeApplication
	 * 
	 * @param req
	 * @return
	 */
	@PostMapping(value = "HRMS/submitEmployeeApplication")
	public BasicRes submitEmployeeApplication(@Valid @RequestBody SubmitEmployeeApplicationReq req) throws Exception {
		return employeeService.submitEmployeeApplication(req);
	}

	/**
	 * Controller17: 查看員工資料更新申請清單<br>
	 * 若職位為員工，僅有人資部門的一般員工可以調用<br>
	 * API 路徑: http://localhost:8080/HRMS/checkUpdateApplication
	 * 
	 * @return
	 */
	@GetMapping(value = "HRMS/checkUpdateApplication")
	public UpdateInfoApplicationRes checkUpdateApplication() throws Exception {
		return employeeService.checkUpdateApplication();
	}

	/**
	 * Controller18: 審核員工資料更新申請<br>
	 * 若非人資部門，一般部門無法使用此 API<br>
	 * API 路徑: http://localhost:8080/HRMS/approveEmployeeApplication
	 * 
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "HRMS/approveEmployeeApplication")
	public BasicRes approveEmployeeApplication(@Valid @RequestBody ApproveEmployeeApplicationReq req) throws Exception {
		return employeeService.approveEmployeeApplication(req);
	}
}