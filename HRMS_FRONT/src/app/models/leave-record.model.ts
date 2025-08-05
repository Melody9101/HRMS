export interface LeaveRecord {
  id: number;
  employeeId: number;
  employeeName: string;
  leaveType: string;
  startTime: string; // ISO字串
  endTime: string;
  reason: string;
  certificate?: string;
  applyDateTime: string;
  status: string;
  rejectionReason?: string;
  approved: boolean;
  approvedDateTime?: string;
}
