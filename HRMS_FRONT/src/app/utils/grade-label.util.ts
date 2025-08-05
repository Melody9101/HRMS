import { Employee } from "../models/employee.model";

/**
 * 根據員工的 grade 數值回傳對應的中文職稱
 * - 11: 總經理
 * - 6～10: 主管
 * - 1～5: 員工
 */
export function getGradeLabel(employee: Employee): string {
  if (employee.grade == 11) return "總經理";
  if (employee.grade >= 6 && employee.grade <= 10) return "主管";
  if (employee.grade >= 1 && employee.grade <= 5) return "員工";
  return "未知職等";
}

