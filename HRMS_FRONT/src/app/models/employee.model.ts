export interface LeaveBalance {
  // 年假(滿6個月3天，滿1年7天，滿2年10天，滿3年14天，滿5年15天，10年以上1年多1天，至多30天)
  remainingPreviousAnnualLeave: number;  //前一年度的年假
  remainingCurrentAnnualLeave: number;   //今年度的年假
  remainingPaidSickLeave: number;        // 有薪病假(1年不得超過30天，超過則改為無薪)
  sick: number;                          // 病假(2年內不得超過365天)
  personal: number;                      // 事假(1年內不得超過14天)
  marriage: number;                      // 婚假(8天)
  bereavement: number;                   // 喪假(父母配偶8天，祖父母及配偶父母6天，曾祖父母、兄弟姊妹及配偶祖父母3天)
  maternity: number;                     // 產假(8個星期)
  paternity: number;                     // 陪產假(7天)
  official: number;                      // 公假(看起來沒限制，暫時先給7天)
  menstrual: number;                     // 生理假(1個月可以請一次，一年可以總共請3次)
  other: number;                         // 其他(暫定給7天)
}

export interface Employee {
  id: number;
  department: string;
  position: string;
  grade: number;
  name: string;
  email: string;
  password?: string;
  phone: string;
  gender: boolean;
  entryDate: string;
  salaries: number;
  resignationDate?: string | null;
  resignationReason?: string | null;
  unpaidLeaveStartDate?: string | null;
  unpaidLeaveEndDate?: string | null;
  unpaidLeaveReason?: string | null;
  employed: boolean;
  remainingPreviousAnnualLeave: number;
  remainingCurrentAnnualLeave: number;
  remainingPaidSickLeave: number;
  leaveBalance: LeaveBalance;
}
