//前端表格顯示
export interface takeLeaveData {
  leaveId?: number,
  employerId?: number,
  employeeId?: number,
  employeeName: string,
  leaveType: string,
  startTime: Date,
  endTime: Date,
  reason: string,
  certificate?: string,
  certificateFileType?:string,
  applyDateTime: Date,
  status: string,
  approved: boolean,
  approvedDateTime?: Date,
  _restored?: boolean,
  approvalPendingRole?:string
}

//後端接收資料格式
export interface LeaveRequestBody {
  startTime: string,// ISO 格式字串
  endTime: string,
  leaveType: string,
  certificate?: string,//Base64編碼
  certificateFileType?: string,
  reason: string,
}


            