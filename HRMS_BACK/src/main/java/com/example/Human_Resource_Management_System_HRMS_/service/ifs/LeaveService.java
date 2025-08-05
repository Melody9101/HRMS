package com.example.Human_Resource_Management_System_HRMS_.service.ifs;

import java.util.List;

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

public interface LeaveService {

	public BasicRes applyLeave(ApplyLeaveReq req);

	public BasicRes approveLeave(ApproveLeaveReq req) throws Exception;

	public BasicRes updateLeaveApplication(UpdateApplicationReq req);

	public BasicRes cancelLeaveApplication(List<Integer> leaveIdList) throws Exception;

	public SearchLeaveRes searchLeaveRecord(LeaveSearchReq req) throws Exception;

	public CheckApplicationRes checkLeaveApplication();

	public RemainingPreviousAnnualLeaveRes checkRemainingPreviousAnnualLeaveList();

	public CheckRecordRes getApprovedLeaveRecordsByEmployeeIdAndYYMM(GetApprovedReq req);
	
	public CheckRecordRes getAllApprovedLeaveRecordsByYYMM(int year, int month);
}
