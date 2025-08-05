package com.example.Human_Resource_Management_System_HRMS_.service.ifs;

import java.util.List;

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

public interface EmployeeService {

	// 方法1-addEmployee: 新增員工資訊。
	public BasicRes addEmployee(AddEmployeeReq req);

	// 方法2-selectByEmployeeIdList: 透過員編查詢員工資料。
	public EmployeeListRes selectByEmployeeIdList(List<Integer> idList);

	// 方法3-updateEmployeeBasicInfo: 透過員編編輯員工基本資訊。
	public BasicRes updateEmployeeBasicInfo(UpdateEmployeeBasicInfoReq req);

	// 方法4-updateEmployeeJob: 透過員編編輯員工職等、調部門和薪資資訊。
	public BasicRes updateEmployeeJob(UpdateEmployeeJobReq req);

	// 方法5-resignEmployee: 透過員編處理員工離職。
	public BasicRes resignEmployee(EmployeeResignationReq req);

	// 方法3-searchEmployeeByEmailAndPhone: 透過信箱以及手機查詢已離職員工資料。
	public EmployeeRes searchEmployeeByEmailAndPhone(SearchEmployeeByEmailAndPhoneReq req);

	// 方法4-reinstatementEmployee: 員工復職。
	public BasicRes reinstatementEmployee(ReinstatementEmployeeReq req);

	// 方法5-updateEmployeePassword: 更新員工密碼。
	public BasicRes updateEmployeePassword(UpdateEmployeePasswordReq req);

	// 方法6-updatePwdByEmail: 忘記密碼。
	public BasicRes updatePwdByEmail(UpdatePwdByEmailReq req);

	// 方法7-updateEmployeeUnpaidLeaveById: 更新員工無薪假、留職停薪等資訊。
	public BasicRes updateEmployeeUnpaidLeaveById(updateEmployeeUnpaidLeaveReq req);

	// 方法8-reinstateEmployeeUnpaidLeaveById: 清空員工無薪假紀錄
	public BasicRes reinstateEmployeeUnpaidLeaveById(int employeeId);

	// 方法9-getAllEmployees: 取得所有在職員工的各項資料。
	public EmployeeListRes getAllEmployees();

	// 方法10-checkLogin: 確認當前是否有登入中的帳號。
	public EmployeeRes checkLogin();

	// 方法11-login: 登入 API。
	public BasicRes login(LoginReq req);

	// 方法13-sendVerificationLetter: 發送驗證信
	public BasicRes sendVerificationLetter(String email);

	// 方法14-checkVerification: 認證驗證碼
	public BasicRes checkVerification(String email, String code);

	// 方法15-getDepartmentEmployeesList: 確認同部門的員工清單
	public EmployeeBasicInfoRes getDepartmentEmployeesList();

	// 方法16-submitUpdateEmployeeInfoApplication: 提交員工資料更新申請
	public BasicRes submitEmployeeApplication(SubmitEmployeeApplicationReq req) throws Exception;

	// 方法17-checkUpdateApplication: 查看員工資料更新申請清單
	public UpdateInfoApplicationRes checkUpdateApplication() throws Exception;

	// 方法18-approveUpdateInfoApplication: 審核員工資料更新申請
	public BasicRes approveEmployeeApplication(ApproveEmployeeApplicationReq req) throws Exception;

}
