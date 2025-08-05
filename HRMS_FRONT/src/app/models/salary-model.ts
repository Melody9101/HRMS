import { LeaveRecord } from './leave-record.model';

export interface MonthlySalaryDetail {
  employeeId: number;
  year: number;
  month: number;
  baseSalary: number;
  totalDeductionForMonth: number;
  bonus: number;
  previousAnnualLeaveConvertedPay: number;
  remainingPreviousAnnualLeave: number;
  finalCalculatedSalary: number;

  allApprovedMonthlyLeaveRecords: LeaveRecord[];
  allMonthlyAttendanceRecords: any[];
  abnormalAttendanceRecords: any[];
}
