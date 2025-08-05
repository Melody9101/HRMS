package com.example.Human_Resource_Management_System_HRMS_.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.Human_Resource_Management_System_HRMS_.constants.DepartmentAndPosition;
import com.example.Human_Resource_Management_System_HRMS_.constants.DepartmentType;
import com.example.Human_Resource_Management_System_HRMS_.constants.LeaveStatus;
import com.example.Human_Resource_Management_System_HRMS_.constants.LeaveType;
import com.example.Human_Resource_Management_System_HRMS_.constants.PositionType;
import com.example.Human_Resource_Management_System_HRMS_.constants.RejectionReason;
import com.example.Human_Resource_Management_System_HRMS_.constants.ResMessage;
import com.example.Human_Resource_Management_System_HRMS_.dao.AnnualHolidayDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.CompanyInfoDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.EmployeeDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.LeaveApplicationDao;
import com.example.Human_Resource_Management_System_HRMS_.dao.LeaveRecordDao;
import com.example.Human_Resource_Management_System_HRMS_.dto.AnnualHolidayDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.CompanyInfoDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.EmployeeDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.LeaveApplicationDto;
import com.example.Human_Resource_Management_System_HRMS_.dto.LeaveRecordDto;
import com.example.Human_Resource_Management_System_HRMS_.service.ifs.LeaveService;
import com.example.Human_Resource_Management_System_HRMS_.vo.ApplyLeaveReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.ApproveLeaveReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.BasicRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.CheckApplicationRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.CheckRecordRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.GetApprovedReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.LeaveSearchReq;
import com.example.Human_Resource_Management_System_HRMS_.vo.RemainingPreviousAnnualLeaveRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.RemainingPreviousAnnualLeaveVo;
import com.example.Human_Resource_Management_System_HRMS_.vo.SearchLeaveRes;
import com.example.Human_Resource_Management_System_HRMS_.vo.UpdateApplicationReq;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@EnableScheduling
@Service
public class LeaveServiceImpl implements LeaveService {

//	private List<RemainingPreviousAnnualLeaveVo> cachedReminderList;

	// logger 是用來記錄到日誌用的，可加可不加
	// import slf4j 的 Logger
//	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private LeaveApplicationDao leaveApplicationDao;

	@Autowired
	private LeaveRecordDao leaveRecordDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private CompanyInfoDao companyInfoDao;

	@Autowired
	private AnnualHolidayDao annualHolidayDao;

	@Autowired
	private EmailServiceImpl emailServiceImpl;

//	@Autowired
//	private SseController sseController;

	// function1. 員工請假系統
	@Override
	public BasicRes applyLeave(ApplyLeaveReq req) {
		EmployeeDto sessionEmployee = getSessionEmployee();
		// 確認前端帶入時間，如果開始時間在結束時間之後，拋出錯誤訊息
		if (req.getStartTime().isAfter(req.getEndTime())) {
			return new BasicRes(ResMessage.INVALID_TIME_RANGE.getCode(), //
					ResMessage.INVALID_TIME_RANGE.getMessage());
		}

		// 確認時間是否正確，時間只能以半小時為單位
		if (!(req.getStartTime().getMinute() == 0 || req.getStartTime().getMinute() == 30)
				|| !(req.getEndTime().getMinute() == 0 || req.getEndTime().getMinute() == 30)
				|| req.getStartTime().getSecond() != 0 || req.getEndTime().getSecond() != 0) {
			return new BasicRes(ResMessage.TIME_FORMAT_ERROR.getCode(), //
					ResMessage.TIME_FORMAT_ERROR.getMessage());
		}

		// 確認假別，如果不符合規定會拋出錯誤
		if (!LeaveType.checkAllType(req.getLeaveType())) {
			return new BasicRes(ResMessage.LEAVE_TYPE_ERROR.getCode(), //
					ResMessage.LEAVE_TYPE_ERROR.getMessage());
		}

		// 當假別為生理假時，確認是否為女性
		if (req.getLeaveType().equalsIgnoreCase(LeaveType.MENSTRUAL.getType()) //
				&& sessionEmployee.getGender()) {
			return new BasicRes(ResMessage.INVALID_GENDER_FOR_LEAVE.getCode(), //
					ResMessage.INVALID_GENDER_FOR_LEAVE.getMessage());
		}

		// 1天=8小時
		// 半小時=0.0625
		BigDecimal leaveHours = calculateLeaveHours(req.getStartTime(), req.getEndTime());
		BigDecimal leaveDays = leaveHours.divide(BigDecimal.valueOf(8), 4, RoundingMode.DOWN);
		// 確認當前有沒有其他年假的請假申請，如果有就計算申請中的天數與本次申請的天數總和不能超過員工剩餘天數
		List<LeaveApplicationDto> sessionLeaveApplicationList = leaveApplicationDao.selectByEmployeeIdAndDate(
				sessionEmployee.getId(), //
				LocalDateTime.of(1, 1, 1, 0, 0), //
				LocalDateTime.of(9999, 12, 31, 23, 59));

		if (req.getLeaveType().equalsIgnoreCase(LeaveType.ANNUAL.getType())) {
			BigDecimal previouAnnualLeaveBalance = sessionEmployee.getRemainingPreviousAnnualLeave();
			BigDecimal currentAnnualLeaveBalance = sessionEmployee.getRemainingCurrentAnnualLeave();

			BigDecimal totalLeaveDays = BigDecimal.ZERO;
			if (sessionLeaveApplicationList != null && !sessionLeaveApplicationList.isEmpty()) {
				for (LeaveApplicationDto item : sessionLeaveApplicationList) {
					if (item.getLeaveType().equalsIgnoreCase(LeaveType.ANNUAL.getType())) {
						BigDecimal applicationLeaveHours = calculateLeaveHours(item.getStartTime(), item.getEndTime());
						BigDecimal applicationLeaveDays = applicationLeaveHours.divide(BigDecimal.valueOf(8), 4,
								RoundingMode.DOWN);
						totalLeaveDays.add(applicationLeaveDays);
					}
				}
			}
			totalLeaveDays = totalLeaveDays.add(leaveDays);

			// 確認年假剩餘天數，如果年假剩餘天數不足，拋出錯誤訊息
			if ((previouAnnualLeaveBalance.add(currentAnnualLeaveBalance)).compareTo(totalLeaveDays) < 0) {
				return new BasicRes(ResMessage.INSUFFICIENT_ANNUAL_LEAVE_BALANCE.getCode(), //
						ResMessage.INSUFFICIENT_ANNUAL_LEAVE_BALANCE.getMessage());
			}
		}
		// 確認當前有沒有其他有薪病假的請假申請，如果有就計算申請中的天數與本次申請的天數總和不能超過員工剩餘天數
		if (req.getLeaveType().equalsIgnoreCase(LeaveType.PAID_SICK_LEAVE.getType())) {
			BigDecimal totalLeaveDays = BigDecimal.ZERO;
			if (sessionLeaveApplicationList != null && !sessionLeaveApplicationList.isEmpty()) {
				for (LeaveApplicationDto item : sessionLeaveApplicationList) {
					if (item.getLeaveType().equalsIgnoreCase(LeaveType.PAID_SICK_LEAVE.getType())) {
						BigDecimal applicationLeaveHours = calculateLeaveHours(item.getStartTime(), item.getEndTime());
						BigDecimal applicationLeaveDays = applicationLeaveHours.divide(BigDecimal.valueOf(8), 4,
								RoundingMode.DOWN);
						totalLeaveDays.add(applicationLeaveDays);
					}
				}
			}
			totalLeaveDays = totalLeaveDays.add(leaveDays);

			// 確認有薪病假剩餘天數，如果有薪病假剩餘天數不足，拋出錯誤訊息
			BigDecimal remainingPaidSickLeave = sessionEmployee.getRemainingPaidSickLeave();
			if (remainingPaidSickLeave.compareTo(leaveDays) < 0) {
				return new BasicRes(ResMessage.INSUFFICIENT_PAID_SICK_LEAVE_BALANCE.getCode(), //
						ResMessage.INSUFFICIENT_PAID_SICK_LEAVE_BALANCE.getMessage());
			}
		}

		// 透過 session 判斷申請人的 grade ，再決定要交由誰審查
		String approvalPendingRole = "";
		boolean submitUp = true;
		if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
			// 判斷申請人的部門，來決定要交由誰審核
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
			approvalPendingRole = DepartmentAndPosition.BOSS.getPosition();
			submitUp = false;
		}
		// 寫入假單申請的資料庫
		leaveApplicationDao.insertLeaveApplication(//
				sessionEmployee.getId(), sessionEmployee.getName(), req.getStartTime(), //
				req.getEndTime(), req.getLeaveType(), req.getCertificate(), //
				LocalDateTime.now(), req.getReason(), LeaveStatus.PENDING_REVIEW.getStatus(), //
				approvalPendingRole, submitUp, req.getCertificateFileType());

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	/**
	 * function2. 審核假單
	 */
	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes approveLeave(ApproveLeaveReq req) throws Exception {

		/**
		 * 確認雙方權限<br>
		 * 審查人用 session 查<br>
		 * 被審查人用 employeeDao 查 如果權限不足，則拋出錯誤訊息
		 */
		LeaveApplicationDto leave = leaveApplicationDao.selectByLeaveId(req.getLeaveId());
		if (leave == null) {
			return new BasicRes(ResMessage.APPLICATION_NOT_FOUND.getCode(), //
					ResMessage.APPLICATION_NOT_FOUND.getMessage());
		}

		EmployeeDto sessionEmployee = getSessionEmployee();
		EmployeeDto leaveApplyer = employeeDao.selectByEmployeeId(leave.getEmployerId());

		// 若審核人是一般員工，又不是人資部門，則拋出權限不足錯誤
		if (isEmployeeNotInHRDepartment(sessionEmployee)) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 無法審查自己的申請
		if (sessionEmployee.getId() == leaveApplyer.getId()) {
			return new BasicRes(ResMessage.CANNOT_APPROVE_SELF_APPLICATION.getCode(), //
					ResMessage.CANNOT_APPROVE_SELF_APPLICATION.getMessage());
		}

		// 如果審核人為人資部門一般員工，以及請假申請人的職位大於審查人，則拋出權限不足錯誤
		if (!leaveApplyer.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())//
				&& sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 判斷如果 status 不符合制定的狀態，則拋出錯誤
		if (!LeaveStatus.checkAllStatus(req.getStatus())) {
			return new BasicRes(ResMessage.STATUS_MISMATCH.getCode(), //
					ResMessage.STATUS_MISMATCH.getMessage());
		}

		// 判斷如果 status 為審核中，則視為沒改到結果，並拋出錯誤
		if (req.getStatus().equalsIgnoreCase(LeaveStatus.PENDING_REVIEW.getStatus())) {
			return new BasicRes(ResMessage.REVIEW_STATUS_NOT_CHANGED.getCode(), //
					ResMessage.REVIEW_STATUS_NOT_CHANGED.getMessage());
		}

		// 如果審查結果為待補文件，則先不寫入 record 之中，編輯後繼續留在 application 裡面
		if (req.getStatus().equalsIgnoreCase(LeaveStatus.PENDING_SUPPLEMENT.getStatus())) {

			leaveApplicationDao.updateStatusByLeaveId(req.getLeaveId(), req.getStatus());

			return new BasicRes(ResMessage.SUCCESS.getCode(), //
					ResMessage.SUCCESS.getMessage());
		}

		// 如果審查結果為 通過、未通過、取消申請，則寫入 record 表之中，並刪除 application 中的資料
		LeaveRecordDto res = new LeaveRecordDto();

		res.setLeaveApplicationId(req.getLeaveId());
		res.setEmployeeName(leaveApplyer.getName());
		res.setEmployeeId(leave.getEmployerId());
		res.setLeaveType(leave.getLeaveType());
		res.setStartTime(leave.getStartTime());
		res.setEndTime(leave.getEndTime());
		res.setReason(leave.getReason());
		res.setCertificate(leave.getCertificate());
		res.setApplyDateTime(leave.getApplyDateTime());
		res.setApprovalPendingRole(leave.getApprovalPendingRole());
		res.setStatus(req.getStatus());
		res.setRejectionReason(req.getRejectionReason());
		res.setApproved(true);
		res.setApprovedDateTime(LocalDateTime.now());
		res.setApproverId(sessionEmployee.getId());
		try {
			// 如果審查結果為取消申請，則寫入 record 後，刪除 application 中的資料
			if (req.getStatus().equalsIgnoreCase(LeaveStatus.CANCEL_APPLICATION.getStatus())) {

				List<LeaveRecordDto> leaveRecordList = leaveRecordDao.selectByEmployeeIdAndApplyDateTime(//
						leave.getEmployerId(), leave.getApplyDateTime());
				List<Integer> targetLeaveIdList = new ArrayList<>();

				if (leaveRecordList != null && !leaveRecordList.isEmpty()) {
					for (LeaveRecordDto item : leaveRecordList) {
						targetLeaveIdList.add(item.getLeaveApplicationId());
					}
				}

				res.setSubmitUp(false);
				leaveRecordDao.addRecord(res);
				leaveApplicationDao.deleteLeaveApplicationByLeaveId(req.getLeaveId());
				return new BasicRes(ResMessage.SUCCESS.getCode(), //
						ResMessage.SUCCESS.getMessage());
			}

			if (req.getStatus().equalsIgnoreCase(LeaveStatus.REJECTED.getStatus())) {
				// 檢查如果申請遭到駁回，則一定要有拒絕理由
				if (req.getRejectionReason() == null || req.getRejectionReason().isBlank()) {
					return new BasicRes(ResMessage.REJECTION_REASON_REQUIRED.getCode(), //
							ResMessage.REJECTION_REASON_REQUIRED.getMessage());
				}

				// 檢查拒絕理由需符合規定，否則會拋出錯誤
				if (!RejectionReason.checkAllReason(req.getRejectionReason())) {
					return new BasicRes(ResMessage.REJECTION_REASON_ERROR.getCode(), //
							ResMessage.REJECTION_REASON_ERROR.getMessage());
				}

				res.setApproved(false);
				res.setSubmitUp(false);
				leaveRecordDao.addRecord(res);
				leaveApplicationDao.deleteLeaveApplicationByLeaveId(req.getLeaveId());
				return new BasicRes(ResMessage.SUCCESS.getCode(), //
						ResMessage.SUCCESS.getMessage());
			}

			boolean submitUp = true;
			/**
			 * 確認是否還要往上審，如果需要就開一個新的假單申請<br>
			 * 如果請假申請人是主管，則直接視為不需要往上審，因為這邊已經是審核API了，只有老闆有權限可以審，沒有更上一層的人了<br>
			 */
			if (!leaveApplyer.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				submitUp = false;
			}

			// 這邊判斷如果審查人是人資主管的話，就不需要再往上審，因為人資主管已經是最後一關了
			if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())
					&& !sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				submitUp = false;
			}
			res.setSubmitUp(submitUp);

			// 如果需要往上送審，確認送審對象，並新增一筆假單申請資料
			if (submitUp) {
				String approvalPendingRole = "";

				if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())) {
					approvalPendingRole = DepartmentAndPosition.HR_EMPLOYEE.getPosition();
				} else if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())
						&& sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
					approvalPendingRole = DepartmentAndPosition.HR_MANAGER.getPosition();
				}

				leaveApplicationDao.insertLeaveApplication(//
						leave.getEmployerId(), leaveApplyer.getName(), leave.getStartTime(), //
						leave.getEndTime(), leave.getLeaveType(), leave.getCertificate(), //
						leave.getApplyDateTime(), leave.getReason(), LeaveStatus.PENDING_REVIEW.getStatus(), //
						approvalPendingRole, submitUp, leave.getCertificateFileType());
			}
			/**
			 * 若申請結果通過，並且不需要往上送審，則視為請假成功<br>
			 * 更新員工假別剩餘天數<br>
			 * 由於已經在請假申請時先排除掉天數不足的問題，這邊直接覆蓋掉資料就可以了
			 */
			if (req.getStatus().equalsIgnoreCase(LeaveStatus.APPROVED.getStatus()) && !submitUp) {
				BigDecimal previouAnnualLeaveBalance = leaveApplyer.getRemainingPreviousAnnualLeave();
				BigDecimal currentAnnualLeaveBalance = leaveApplyer.getRemainingCurrentAnnualLeave();
				BigDecimal remainingPaidSickLeave = leaveApplyer.getRemainingPaidSickLeave();

				BigDecimal leaveHours = calculateLeaveHours(leave.getStartTime(), leave.getEndTime());
				BigDecimal leaveDays = leaveHours.divide(BigDecimal.valueOf(8), 4, RoundingMode.DOWN);

				if (leave.getLeaveType().equalsIgnoreCase(LeaveType.ANNUAL.getType())) {

					if (previouAnnualLeaveBalance.compareTo(BigDecimal.ZERO) > 0) {
						if (previouAnnualLeaveBalance.compareTo(leaveDays) >= 0) {
							// 前年餘額足夠扣
							previouAnnualLeaveBalance = previouAnnualLeaveBalance.subtract(leaveDays);
							leaveDays = BigDecimal.ZERO;
						} else {
							// 前年不夠扣，先扣完它，剩下的從今年扣
							leaveDays = leaveDays.subtract(previouAnnualLeaveBalance);
							previouAnnualLeaveBalance = BigDecimal.ZERO;
						}
					}

					// 再從今年度扣剩下的
					if (leaveDays.compareTo(BigDecimal.ZERO) > 0) {
						if (currentAnnualLeaveBalance.compareTo(leaveDays) >= 0) {
							currentAnnualLeaveBalance = currentAnnualLeaveBalance.subtract(leaveDays);
							leaveDays = BigDecimal.ZERO;
						}
					}
				}

				if (leave.getLeaveType().equalsIgnoreCase(LeaveType.PAID_SICK_LEAVE.getType())) {
					remainingPaidSickLeave = remainingPaidSickLeave.subtract(leaveDays);
				}

				leaveApplyer.setRemainingPreviousAnnualLeave(previouAnnualLeaveBalance);
				leaveApplyer.setRemainingCurrentAnnualLeave(currentAnnualLeaveBalance);
				leaveApplyer.setRemainingPaidSickLeave(remainingPaidSickLeave);
				employeeDao.updateRemainingLeave(leaveApplyer);
			}

			leaveRecordDao.addRecord(res);
			leaveApplicationDao.deleteLeaveApplicationByLeaveId(req.getLeaveId());
		} catch (

		Exception e) {
			throw e;
		}

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	/**
	 * function3. 員工補證明文件
	 */
	@Override
	public BasicRes updateLeaveApplication(UpdateApplicationReq req) {

		LeaveApplicationDto leaveApplication = leaveApplicationDao.selectByLeaveId(req.getLeaveId());

		if (leaveApplication == null) {
			return new BasicRes(ResMessage.APPLICATION_NOT_FOUND.getCode(), //
					ResMessage.APPLICATION_NOT_FOUND.getMessage());
		}

		// 確認當前 status 為待補文件才有辦法更新
		if (!leaveApplication.getStatus().equalsIgnoreCase(LeaveStatus.PENDING_SUPPLEMENT.getStatus())) {
			return new BasicRes(ResMessage.INVALID_LEAVE_STATUS.getCode(), //
					ResMessage.INVALID_LEAVE_STATUS.getMessage());
		}

		// 確認必須為本人才有辦法更新假單
		EmployeeDto sessionEmployee = getSessionEmployee();

		if (sessionEmployee.getId() != leaveApplication.getEmployerId()) {
			return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		int updateSuccessCount = leaveApplicationDao.updateByLeaveId(//
				req.getLeaveId(), req.getCertificate(), LeaveStatus.PENDING_REVIEW.getStatus(), //
				req.getCertificateFileType());
		if (updateSuccessCount != 1) {
			return new BasicRes(ResMessage.FAILED.getCode(), ResMessage.FAILED.getMessage());
		}
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	/**
	 * function4. 搜尋假單紀錄
	 */
	@Transactional(rollbackOn = Exception.class)
	@Override
	public SearchLeaveRes searchLeaveRecord(LeaveSearchReq req) throws Exception {
		EmployeeDto sessionEmployee = getSessionEmployee();
		EmployeeDto target = employeeDao.selectByEmployeeId(req.getEmployeeId());

		// 確認是否為本人，若查詢對象並非本人，則僅有人資部門以及主管以上級別才有辦法查詢
		if (sessionEmployee.getId() != req.getEmployeeId()
				&& sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())
				&& !sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())) {
			return new SearchLeaveRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());

		}
		// 如果不是老闆或人資部門，無法跨部門查詢
		if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())
				&& !sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())
				&& !target.getDepartment().equalsIgnoreCase(sessionEmployee.getDepartment())) {
			return new SearchLeaveRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 若查詢者不為老闆，不能查詢老闆的資料
		if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())
				&& target.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
			return new SearchLeaveRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		// 確認前端帶入的時間，如果開始時間在結束時間之後，拋出錯誤訊息
		if (req.getStartTime() != null && req.getEndTime() != null) {
			if (req.getStartTime().isAfter(req.getEndTime())) {
				return new SearchLeaveRes(ResMessage.INVALID_TIME_RANGE.getCode(), //
						ResMessage.INVALID_TIME_RANGE.getMessage());
			}
		}

		// 確認前端帶入的時間，如果沒帶就設定一個很早和很晚的時間
		LocalDateTime startTime = LocalDateTime.MIN;
		LocalDateTime endTime = LocalDateTime.MAX;
		if (req.getStartTime() != null) {
			startTime = req.getStartTime().atTime(0, 0, 0);
		}

		if (req.getEndTime() != null) {
			endTime = req.getEndTime().atTime(23, 59, 59);
		}

		try {
			List<LeaveApplicationDto> leaveApplicationList = leaveApplicationDao
					.selectByEmployeeIdAndDate(req.getEmployeeId(), startTime, endTime);

			List<LeaveRecordDto> leaveRecordList = leaveRecordDao.selectByEmployeeIdAndDate(req.getEmployeeId(),
					startTime, endTime);

			return new SearchLeaveRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), //
					leaveApplicationList, leaveRecordList);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * function5. 取消休假申請
	 */
	@Transactional(rollbackOn = Exception.class)
	@Override
	public BasicRes cancelLeaveApplication(List<Integer> leaveIdList) throws Exception {

		if (leaveIdList == null || leaveIdList.isEmpty()) {
			return new BasicRes(ResMessage.PARAM_ID_LIST_ERROR.getCode(), //
					ResMessage.PARAM_ID_LIST_ERROR.getMessage());
		}

		// 檢查更動的對象是否為同一人
		List<LeaveRecordDto> leaveRecordList = leaveRecordDao.selectByLeaveIdList(leaveIdList);
		List<LeaveApplicationDto> applicationList = leaveApplicationDao.selectByLeaveIdList(leaveIdList);

		// 確保更動假單不為空
		if ((leaveRecordList == null || leaveRecordList.isEmpty())//
				&& (applicationList == null || applicationList.isEmpty())) {
			return new BasicRes(ResMessage.RECORD_NOT_FOUND.getCode(), //
					ResMessage.RECORD_NOT_FOUND.getMessage());
		}

		// 如果使用者帶入的值其中有幾筆為空資料，則拋出錯誤
		if ((leaveRecordList.size() + applicationList.size()) != leaveIdList.size()) {
			return new BasicRes(ResMessage.RECORD_NOT_FOUND.getCode(), //
					ResMessage.RECORD_NOT_FOUND.getMessage());
		}

		int applicantId = 0;
		// 當請假歷史紀錄不為空時檢查
		if (leaveRecordList != null && !leaveRecordList.isEmpty()) {
			// 取出第一筆資料確認變更對象
			int firstEmployeeId = leaveRecordList.get(0).getEmployeeId();
			applicantId = firstEmployeeId;
			// 取出第一筆申請時間，後續方便確認全部的假單不分審查階段是否為同一筆
			LocalDateTime firstLeaveApplyTime = leaveRecordList.get(0).getApplyDateTime();

			for (LeaveRecordDto item : leaveRecordList) {
				int employeeId = item.getEmployeeId();
				LocalDateTime leaveApplyTime = item.getApplyDateTime();

				if (employeeId != firstEmployeeId) {
					return new BasicRes(ResMessage.MUST_BE_SAME_EMPLOYEE.getCode(), //
							ResMessage.MUST_BE_SAME_EMPLOYEE.getMessage());
				}

				if (!leaveApplyTime.equals(firstLeaveApplyTime)) {
					return new BasicRes(ResMessage.MUST_BE_SAME_LEAVE.getCode(), //
							ResMessage.MUST_BE_SAME_LEAVE.getMessage());
				}
			}
		}

		// 當請假申請不為空時檢查
		if (applicationList != null && !applicationList.isEmpty()) {
			// 取出第一筆資料確認變更對象
			int firstEmployeeId = applicationList.get(0).getEmployerId();
			applicantId = firstEmployeeId;
			// 取出第一筆申請時間，後續方便確認全部的假單不分審查階段是否為同一筆
			LocalDateTime firstLeaveApplyTime = applicationList.get(0).getApplyDateTime();

			for (LeaveApplicationDto item : applicationList) {
				int employeeId = item.getEmployerId();
				LocalDateTime leaveApplyTime = item.getApplyDateTime();

				if (employeeId != firstEmployeeId) {
					return new BasicRes(ResMessage.MUST_BE_SAME_EMPLOYEE.getCode(), //
							ResMessage.MUST_BE_SAME_EMPLOYEE.getMessage());
				}

				if (!leaveApplyTime.equals(firstLeaveApplyTime)) {
					return new BasicRes(ResMessage.MUST_BE_SAME_LEAVE.getCode(), //
							ResMessage.MUST_BE_SAME_LEAVE.getMessage());
				}
			}
		}
		if ((applicationList != null && !applicationList.isEmpty()) && leaveRecordList != null
				&& !leaveRecordList.isEmpty()) {
			if (applicationList.get(0).getEmployerId() != leaveRecordList.get(0).getEmployeeId()) {
				return new BasicRes(ResMessage.MUST_BE_SAME_LEAVE.getCode(), //
						ResMessage.MUST_BE_SAME_LEAVE.getMessage());
			}
		}

		// 檢查權限，若非本人，僅有人資主管及老闆有權限使用此api
		EmployeeDto sessionEmployee = getSessionEmployee();
		if (sessionEmployee.getId() != applicantId) {
			EmployeeDto target = employeeDao.selectByEmployeeId(applicantId);

			if (!sessionIsManagerOrBoss(sessionEmployee) || !sessionIsHrOrBoss(sessionEmployee)) {
				return new BasicRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
						ResMessage.GRADE_INSUFFICIENT.getMessage());
			}

			// 如果變更者為人資主管，無權取消老闆假單
			if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())
					&& target.getPosition().equalsIgnoreCase(PositionType.BOSS.getPositionName())) {
				return new BasicRes(ResMessage.RECORD_NOT_FOUND.getCode(), //
						ResMessage.RECORD_NOT_FOUND.getMessage());
			}

		}

		try {
			leaveRecordDao.updateRecordToCancel(LeaveStatus.CANCEL_APPLICATION.getStatus(), //
					false, LocalDateTime.now(), sessionEmployee.getId(), leaveIdList);

			// 確認有沒有假單還在審核中，如果有就將審核中的假單移動至假單歷史紀錄裡面，並刪除審核中的假單
			List<LeaveApplicationDto> leaveApplicationList = leaveApplicationDao.selectByLeaveIdList(leaveIdList);
			if (leaveApplicationList != null && !leaveApplicationList.isEmpty()) {
				LeaveApplicationDto leave = leaveApplicationList.get(0);

				LeaveRecordDto res = new LeaveRecordDto();

				res.setLeaveApplicationId(leave.getLeaveId());
				res.setEmployeeId(leave.getEmployerId());
				res.setLeaveType(leave.getLeaveType());
				res.setStartTime(leave.getStartTime());
				res.setEndTime(leave.getEndTime());
				res.setReason(leave.getReason());
				res.setCertificate(leave.getCertificate());
				res.setApplyDateTime(leave.getApplyDateTime());
				res.setApprovalPendingRole(leave.getApprovalPendingRole());
				res.setStatus(LeaveStatus.CANCEL_APPLICATION.getStatus());
				res.setRejectionReason(LeaveStatus.CANCEL_APPLICATION.getStatus());
				res.setApproved(false);
				res.setApprovedDateTime(LocalDateTime.now());
				res.setApproverId(sessionEmployee.getId());
				res.setSubmitUp(false);

				leaveRecordDao.addRecord(res);
				leaveApplicationDao.deleteLeaveApplicationByLeaveId(leave.getLeaveId());
			}
			return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * function6. 確認尚未審核的請假申請
	 */
	@Override
	public CheckApplicationRes checkLeaveApplication() {

		EmployeeDto sessionEmployee = getSessionEmployee();

		List<LeaveApplicationDto> leaveApplicationList = leaveApplicationDao.selectAll();
		List<LeaveApplicationDto> forFrontList = new ArrayList<>();

		for (LeaveApplicationDto item : leaveApplicationList) {

			if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.GENERAL_AFFAIRS.getDepartmentName())
					&& !sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				if (item.getApprovalPendingRole()//
						.equalsIgnoreCase(DepartmentAndPosition.GENERAL_MANAGER.getPosition())) {
					forFrontList.add(item);
				}
			} else if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.ACCOUNTANT.getDepartmentName())
					&& !sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				if (item.getApprovalPendingRole()//
						.equalsIgnoreCase(DepartmentAndPosition.ACCOUNTING_MANAGER.getPosition())) {
					forFrontList.add(item);
				}
			} else if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())
					&& sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				if (item.getApprovalPendingRole().//
						equalsIgnoreCase(DepartmentAndPosition.HR_EMPLOYEE.getPosition())) {
					forFrontList.add(item);
				}
			} else if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())
					&& !sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				if (item.getApprovalPendingRole().//
						equalsIgnoreCase(DepartmentAndPosition.HR_MANAGER.getPosition())) {
					forFrontList.add(item);
				}
			} else if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
				if (item.getApprovalPendingRole().//
						equalsIgnoreCase(DepartmentAndPosition.BOSS.getPosition())) {
					forFrontList.add(item);
				}
			} else {
				return new CheckApplicationRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
						ResMessage.GRADE_INSUFFICIENT.getMessage(), forFrontList);
			}
		}
		return new CheckApplicationRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), forFrontList);
	}

	/**
	 * function7. 查詢尚有去年年假剩餘天數以及有薪病假剩餘天數的員工<br>
	 * 若為一般員工及不為人資部門，則拋出錯誤<br>
	 * 人資員工僅能查看職位同為員工的資料<br>
	 * 除了人資主管以及老闆以外，主管僅能查詢自己部門員工的剩餘年假與有薪病假
	 */
	@Override
	public RemainingPreviousAnnualLeaveRes checkRemainingPreviousAnnualLeaveList() {
		EmployeeDto sessionEmployee = getSessionEmployee();
		if (isEmployeeNotInHRDepartment(sessionEmployee)) {
			return new RemainingPreviousAnnualLeaveRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}

		List<EmployeeDto> employeeList = employeeDao.getAllEmployees();

		List<RemainingPreviousAnnualLeaveVo> reminderList = new ArrayList<>();

		if (employeeList != null && !employeeList.isEmpty()) {
			for (EmployeeDto item : employeeList) {
				BigDecimal remainingAnnualLeave = item.getRemainingPreviousAnnualLeave();
				BigDecimal remainingPaidSickLeave = item.getRemainingPaidSickLeave();

				if ((remainingAnnualLeave == null || remainingAnnualLeave.compareTo(BigDecimal.ZERO) <= 0)
						&& (remainingPaidSickLeave == null || remainingPaidSickLeave.compareTo(BigDecimal.ZERO) <= 0)) {
					continue;
				}

				boolean targetIsEmployee = item.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName());

				if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
					if (targetIsEmployee) {
						RemainingPreviousAnnualLeaveVo vo = inesertToReminder(item);
						reminderList.add(vo);
					}
				} else {
					if (!sessionIsHrOrBoss(sessionEmployee)) {
						if (item.getDepartment().equalsIgnoreCase(sessionEmployee.getDepartment())) {
							RemainingPreviousAnnualLeaveVo vo = inesertToReminder(item);
							reminderList.add(vo);
						}
					} else {
						RemainingPreviousAnnualLeaveVo vo = inesertToReminder(item);
						reminderList.add(vo);
					}

				}
			}
		}

		return new RemainingPreviousAnnualLeaveRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), reminderList);
	}

	/**
	 * function8. 帶入員工編號、年度及月份取得該員工請假成功紀錄
	 */
	@Override
	public CheckRecordRes getApprovedLeaveRecordsByEmployeeIdAndYYMM(GetApprovedReq req) {
		EmployeeDto sessionEmployee = getSessionEmployee();
		EmployeeDto target = employeeDao.selectByEmployeeId(req.getEmployeeId());

		/**
		 * 若非本人<br>
		 * (1)登入帳號為一般部門員工，拋出錯誤<br>
		 * (2)登入帳號為一般員工，並且查詢對象為主管職以上職位，拋出錯誤
		 */
		if (sessionEmployee.getId() != req.getEmployeeId()) {
			if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.GENERAL_AFFAIRS.getDepartmentName())) {
				if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
					return new CheckRecordRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
							ResMessage.GRADE_INSUFFICIENT.getMessage());
				}
				if (!target.getDepartment().equalsIgnoreCase(sessionEmployee.getDepartment())) {
					return new CheckRecordRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
							ResMessage.GRADE_INSUFFICIENT.getMessage());
				}
			}

			if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())
					&& !target.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				return new CheckRecordRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
						ResMessage.GRADE_INSUFFICIENT.getMessage());
			}

		}

		YearMonth yearMonth = YearMonth.of(req.getYear(), req.getMonth());
		int lastDay = yearMonth.lengthOfMonth(); // 根據年份與月份自動取得最後一天

		LocalDateTime startTime = LocalDateTime.of(req.getYear(), req.getMonth(), 1, 0, 0, 0);
		LocalDateTime endTime = LocalDateTime.of(req.getYear(), req.getMonth(), lastDay, 23, 59, 59);

		List<LeaveRecordDto> successRecordList = leaveRecordDao.selectApprovedRecordByEmployeeIdAndYYMM(//
				req.getEmployeeId(), startTime, endTime);

		return new CheckRecordRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), successRecordList);
	}

	/**
	 * function9. 透過年度與月份確認所有員工該月的請假成功紀錄<br>
	 * 一般部門無法使用此 API
	 * 
	 */
	@Override
	public CheckRecordRes getAllApprovedLeaveRecordsByYYMM(int year, int month) {
		// 若輸入資料有誤，則拋出錯誤
		if (year < 1) {
			return new CheckRecordRes(ResMessage.PARAM_YEAR_ERROR.getCode(), //
					ResMessage.PARAM_YEAR_ERROR.getMessage());
		}
		if (month < 1 || month > 12) {
			return new CheckRecordRes(ResMessage.PARAM_MONTH_ERROR.getCode(), //
					ResMessage.PARAM_MONTH_ERROR.getMessage());
		}

		EmployeeDto sessionEmployee = getSessionEmployee();

		if (sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.GENERAL_AFFAIRS.getDepartmentName())) {
			return new CheckRecordRes(ResMessage.GRADE_INSUFFICIENT.getCode(), //
					ResMessage.GRADE_INSUFFICIENT.getMessage());
		}
		YearMonth yearMonth = YearMonth.of(year, month);
		// 若是查詢開始月份大於今日月份，則拋出錯誤
		if (yearMonth.isAfter(YearMonth.of(LocalDate.now().getYear(), LocalDate.now().getMonth()))) {
			return new CheckRecordRes(ResMessage.CANNOT_QUERY_FUTURE_MONTH.getCode(), //
					ResMessage.CANNOT_QUERY_FUTURE_MONTH.getMessage());
		}
		int lastDay = yearMonth.lengthOfMonth(); // 根據年份與月份自動取得最後一天

		LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0, 0);
		LocalDateTime endTime = LocalDateTime.of(year, month, lastDay, 23, 59, 59);

		List<LeaveRecordDto> monthlyAllApprovedLeaveRecordList = leaveRecordDao
				.selectAllMonthlyApprovedRecordByYYMM(startTime, endTime);
		List<LeaveRecordDto> filteredList = new ArrayList<>();

		if (monthlyAllApprovedLeaveRecordList == null || monthlyAllApprovedLeaveRecordList.isEmpty()) {
			return new CheckRecordRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), filteredList);
		}

		for (LeaveRecordDto item : monthlyAllApprovedLeaveRecordList) {
			EmployeeDto target = employeeDao.selectByEmployeeId(item.getEmployeeId());
			if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())
					&& target.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				filteredList.add(item);
			}
			if (!sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
				filteredList.add(item);
			}

		}
		return new CheckRecordRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), filteredList);
	}

	/**
	 * Scheduled1. 計算所有人的年假剩餘天數<br>
	 * 每日 00:05 執行
	 */
	@Scheduled(cron = "0 5 0 * * *")
	public void annualCalculator() {
		List<EmployeeDto> employeeList = employeeDao.getAllEmployees();

		LocalDate today = LocalDate.now();

		for (EmployeeDto item : employeeList) {
			LocalDate entryDate = item.getEntryDate();

			if (entryDate.isAfter(today)) {
				continue;
			}

			long totalYears = entryDate.until(today).getYears();
			long totalMonths = entryDate.until(today).getMonths();

			int annualLeaveDays = getAnnualLeaveDays(totalYears, totalMonths);

			if (totalYears == 1 && entryDate.getMonthValue() <= 6 && entryDate.getDayOfMonth() <= 30) {
				annualLeaveDays = 4;
			}

			BigDecimal newAnnualLeave = item.getRemainingCurrentAnnualLeave().add(BigDecimal.valueOf(annualLeaveDays));

			item.setRemainingCurrentAnnualLeave(newAnnualLeave);

			employeeDao.updateRemainingLeave(item);
		}
	}

	/**
	 * Scheduled2. 確認所有人的前年剩餘年假與有薪病假天數<br>
	 * 每年的 11/1 0:00 執行<br>
	 * 發出信件，如果不請就強制轉薪，不會寄給老闆
	 */
	@Scheduled(cron = "0 0 0 1 11 *")
	public void remindRemainingAnnual() {
		List<EmployeeDto> employeeList = employeeDao.getAllEmployees();

		for (EmployeeDto item : employeeList) {
			if (!item.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
				BigDecimal previousAnnualLeave = item.getRemainingPreviousAnnualLeave();
				BigDecimal remainingPaidSickLeave = item.getRemainingPaidSickLeave();
				if (previousAnnualLeave.compareTo(BigDecimal.ZERO) > 0
						|| remainingPaidSickLeave.compareTo(BigDecimal.ZERO) > 0) {

					String remainingAnnualBody = String.format(//
							"您好，\n\n" + //
									"提醒您，您目前尚有剩餘的前年度假期：\n\n" + //
									"年假： %s 天\n" + //
									"有薪病假： %s 天\n\n" + //
									"尚未休假完畢，\n" + //
									"若在今年 12/31 前未休假完畢，\n" + //
									"將強制將前年度剩餘的年假轉換成薪水，\n" + //
									"並重置有薪病假天數。\n" + //
									"請在期限前休假完畢。\n\n" + //
									"謝謝您。",
							previousAnnualLeave.toString(), remainingPaidSickLeave.toString()); //
					emailServiceImpl.sendVerificationCode(item.getEmail(), //
							"【人資部通知】前年度剩餘假期提醒", remainingAnnualBody);

				}
			}
		}

//		cachedReminderList = reminderList;

//		RemainingPreviousAnnualLeaveRes res = new RemainingPreviousAnnualLeaveRes(ResMessage.SUCCESS.getCode(),
//				ResMessage.SUCCESS.getMessage(), reminderList);

//		sseController.sendToClients(res);

	}

	/**
	 * Scheduled3. 更新所有人剩餘年假天數<br>
	 * 每年的 1/10 0:00 執行<br>
	 * 將今年剩餘的年假寫入去年剩餘年假中<br>
	 * 並清空去年年假剩餘天數
	 */
	@Scheduled(cron = "0 0 0 10 1 *")
	public void annualUpdate() {
		List<EmployeeDto> employeeList = employeeDao.getAllEmployees();

		for (EmployeeDto item : employeeList) {
			BigDecimal remainingAnnualLeave = item.getRemainingCurrentAnnualLeave();
			item.setRemainingPreviousAnnualLeave(remainingAnnualLeave);
			item.setRemainingCurrentAnnualLeave(BigDecimal.ZERO); // 清空今年剩餘年假
			item.setRemainingPaidSickLeave(BigDecimal.valueOf(5));

//			cachedReminderList = null;
			employeeDao.updateRemainingLeave(item);

		}
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

	private boolean sessionIsManagerOrBoss(EmployeeDto sessionEmployee) {

		if (sessionEmployee == null
				|| sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())) {
			return false;
		}
		return true;

	}

	private boolean sessionIsHrOrBoss(EmployeeDto sessionEmployee) {

		if (!sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())
				&& !sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.BOSS.getDepartmentName())) {
			return false;
		}
		return true;
	}

	private boolean isEmployeeNotInHRDepartment(EmployeeDto sessionEmployee) {
		// 如果是「員工職位」且部門不是「人資部門」，則返回 true
		if (sessionEmployee.getPosition().equalsIgnoreCase(PositionType.EMPLOYEE.getPositionName())
				&& !sessionEmployee.getDepartment().equalsIgnoreCase(DepartmentType.HR.getDepartmentName())) {
			return true;
		}
		return false; // 否則返回 false
	}

	private RemainingPreviousAnnualLeaveVo inesertToReminder(EmployeeDto dto) {
		RemainingPreviousAnnualLeaveVo vo = new RemainingPreviousAnnualLeaveVo();

		vo.setEmployeeId(dto.getId());
		vo.setEmployeeName(dto.getName());
		vo.setEmployeeDepartment(dto.getDepartment());
		vo.setRemainingPreviousAnnualLeave(dto.getRemainingPreviousAnnualLeave());
		vo.setRemainingPaidSickLeave(dto.getRemainingPaidSickLeave());

		return vo;

	}

	private BigDecimal calculateLeaveHours(LocalDateTime start, LocalDateTime end) {
		CompanyInfoDto companyInfo = companyInfoDao.checkCompanyInfo();

		List<AnnualHolidayDto> annualList = annualHolidayDao.getAllAnnualHoliday();

		final LocalTime WORK_START = companyInfo.getWorkStartTime();
		final LocalTime LUNCH_START = companyInfo.getLunchStartTime();
		final LocalTime LUNCH_END = companyInfo.getLunchEndTime();
		// 午休時間（分鐘）
		final long DAILY_WORK_MINUTES = 8 * 60; // 8 小時工作日
		// 午休分鐘數 = lunch_end_time - lunch_start_time
		long lunchBreakMinutes = Duration.between(LUNCH_START, LUNCH_END).toMinutes();

		if (start.isAfter(end)) {
			return BigDecimal.ZERO;
		}
		LocalDateTime cursor = start;
		BigDecimal totalEffectiveMinutes = BigDecimal.ZERO;

		while (!cursor.toLocalDate().isAfter(end.toLocalDate())) {
			LocalDate currentDate = cursor.toLocalDate();

			// 檢查是否是公司規定的假日
			boolean isHoliday = false;
			for (AnnualHolidayDto holiday : annualList) {
				if (holiday.getDate().equals(currentDate) && holiday.isHoliday()) {
					isHoliday = true;
					break; // 找到假日就停止搜尋
				}
			}

			// 如果是假日，跳過該天
			if (isHoliday) {
				cursor = cursor.plusDays(1).withHour(0).withMinute(0).withSecond(0);
				continue;
			}

			// 計算當天的工作起迄時間（排除非工作日）
			if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
				// 如果是假日，檢查當天是否為補班日
				boolean isMakeUpDay = false;
				for (AnnualHolidayDto makeUpDay : annualList) {
					if (makeUpDay.getDate().equals(currentDate) && !makeUpDay.isHoliday()) {
						isMakeUpDay = true;
						break; // 找到補班日，結束搜尋
					}
				}
				// 如果不是補班日，跳過這一天
				if (!isMakeUpDay) {
					cursor = cursor.plusDays(1).withHour(0).withMinute(0).withSecond(0);
					continue;
				}

			}

			// 當天的工作時間段
			LocalDateTime workStartDateTime = LocalDateTime.of(currentDate, WORK_START);
			LocalDateTime lunchStartDateTime = LocalDateTime.of(currentDate, LUNCH_START);
			LocalDateTime lunchEndDateTime = LocalDateTime.of(currentDate, LUNCH_END);
			LocalDateTime workEndDateTime = workStartDateTime.plusMinutes(DAILY_WORK_MINUTES + lunchBreakMinutes);

			// 計算當天請假的起始與結束時間(限制在工作時間內)
			LocalDateTime dayStart = cursor.isAfter(workStartDateTime) ? cursor : workStartDateTime;
			LocalDateTime dayEnd = end.isBefore(workEndDateTime) && !end.toLocalDate().isAfter(currentDate) ? end
					: workEndDateTime;

			if (!dayStart.isBefore(dayEnd)) {
				// 如果該天的開始時間晚於結束時間，跳過
				cursor = cursor.plusDays(1).withHour(0).withMinute(0).withSecond(0);
				continue;
			}

			// 計算當天總分鐘數
			long minutes = Duration.between(dayStart, dayEnd).toMinutes();

			// 扣除午休時間（如果請假時間包含午休）
			if (!dayStart.isAfter(lunchEndDateTime) && !dayEnd.isBefore(lunchStartDateTime)) {
				LocalDateTime overlapStart = dayStart.isAfter(lunchStartDateTime) ? dayStart : lunchStartDateTime;
				LocalDateTime overlapEnd = dayEnd.isBefore(lunchEndDateTime) ? dayEnd : lunchEndDateTime;

				if (overlapEnd.isAfter(overlapStart)) {
					long lunchOverlapMinutes = Duration.between(overlapStart, overlapEnd).toMinutes();
					minutes -= lunchOverlapMinutes;
				}
			}

			totalEffectiveMinutes = totalEffectiveMinutes.add(BigDecimal.valueOf(minutes));

			// 移動到下一天的起始時間
			cursor = LocalDateTime.of(currentDate.plusDays(1), WORK_START);
		}

		// 將分鐘轉為小時（除以60）
		BigDecimal totalHours = totalEffectiveMinutes.divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);
		return totalHours.max(BigDecimal.ZERO);
	}

	private int getAnnualLeaveDays(long years, long months) {
		if (years == 0 && months >= 6)
			return 3;
		if (years == 1)
			return 7;
		if (years == 2)
			return 10;
		if (years == 3)
			return 14;
		if (years == 4)
			return 14;
		if (years == 5)
			return 15;
		if (years >= 6 && years <= 9)
			return 15;
		if (years == 10)
			return 16;
		if (years == 11)
			return 17;
		if (years == 12)
			return 18;
		if (years == 13)
			return 19;
		if (years == 14)
			return 20;
		if (years == 15)
			return 21;
		if (years == 16)
			return 22;
		if (years == 17)
			return 23;
		if (years == 18)
			return 24;
		if (years == 19)
			return 25;
		if (years == 20)
			return 26;
		if (years == 21)
			return 27;
		if (years == 22)
			return 28;
		if (years == 23)
			return 29;
		if (years >= 24)
			return 30;

		return 0;
	}

}