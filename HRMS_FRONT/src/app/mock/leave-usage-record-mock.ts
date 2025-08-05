import { LeaveUsageRecord } from '../models/leave-usage-record.model';

export const LEAVE_USAGE_RECORDS: LeaveUsageRecord[] = [
  // 員工 ID 23
  { employeeId: 23, leaveType: "annual", usedHours: 16 },
  { employeeId: 23, leaveType: "personal", usedHours: 8 },

  // 員工 ID 24
  { employeeId: 24, leaveType: "annual", usedHours: 24 },
  { employeeId: 24, leaveType: "sick", usedHours: 12 },

  // 員工 ID 25
  { employeeId: 25, leaveType: "paidSickLeave", usedHours: 6 },
  { employeeId: 25, leaveType: "menstrual", usedHours: 2 },

  // 員工 ID 26
  { employeeId: 26, leaveType: "official", usedHours: 4 },

  // 員工 ID 27
  { employeeId: 27, leaveType: "other", usedHours: 7 },

  // 員工 ID 28
  { employeeId: 28, leaveType: "marriage", usedHours: 8 },

  // 員工 ID 29
  { employeeId: 29, leaveType: "sick", usedHours: 20 },
];
