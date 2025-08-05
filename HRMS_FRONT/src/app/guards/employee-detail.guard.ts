import { Injectable } from "@angular/core";
import { CanActivate, ActivatedRouteSnapshot, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";
import { EmployeeService } from "../services/employee-data.service";

@Injectable({ providedIn: "root" })
export class EmployeeDetailAccessGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router,
    private employeeService: EmployeeService
  ) { }

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const user = this.authService.getUserInfo();
    if (!user) {
      this.router.navigate(["/login"]);
      return false;
    }

    const targetId = Number(route.paramMap.get("id"));
    const targetEmp = this.employeeService.getById(targetId);

    if (!targetEmp) {
      this.router.navigate(["/home"]);
      return false;
    }

    // 總經理可看全部
    if (user.grade == 50) return true;

    // 人資主管、員工都可看全部
    if (user.department == "人資部門") return true;

    // 一般部門主管可看一般部門所有人
    // if (
    //   user.department == "一般部門" &&
    //   user.grade >= 30 &&
    //   targetEmp.department == "一般部門"
    // ) return true;

    // 會計部門主管可看全部
    if (user.department == "會計部門" && user.grade >= 30) return true;

    // 會計部門員工只能看一般員工 (grade < 30)
    // if (
    //   user.department == "會計部門" &&
    //   user.grade < 30 &&
    //   targetEmp.grade < 30
    // ) return true;

    // 員工可以看自己
    // if (user.id == targetEmp.id) return true;

    // 其餘一律導回首頁
    this.router.navigate(["/home"]);
    return false;
  }
}
