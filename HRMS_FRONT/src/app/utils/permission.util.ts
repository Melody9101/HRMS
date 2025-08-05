import { Employee } from "../models/employee.model";

export function getRole(employee: Employee): "總經理" | "人資主管" | "人資部門" | "一般部門主管" | "一般部門員工" {
  if (employee.grade == 50 && employee.department == "總經理") return "總經理";
  if (employee.grade >= 30 && employee.grade < 50 && employee.department == "人資部門") return "人資主管";
  if (employee.grade >= 5 && employee.grade < 30 && employee.department == "人資部門") return "人資部門";
  if (employee.grade >= 30 && employee.grade < 50 && employee.department == "一般部門") return "一般部門主管";
  if (employee.grade >= 5 && employee.grade < 30 && employee.department == "一般部門") return "一般部門員工";
  // 萬一沒符合上面條件，預設回傳 一般部門員工
  return "一般部門員工";
}
