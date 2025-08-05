export interface SalariesRecord {
  employeeId: number;                       // 對應的員工ID
  salariesYear: number;                     // 薪資年份
  salariesMonth: string;                    // 薪資月份
  baseSalaries: number;                     // 基本薪資
  allowance: number;                        // 津貼
  // overtimePay: number;                   // 加班費
  // bonus: number;                         // 獎金
  // laborInsuranceDeducted: number;        // 勞保
  // healthInsuranceDeducted: number;       // 健保
  leaveDeduction: number;                   // 請假扣款
  actualSalaries: number;                   // 實發金額
}
