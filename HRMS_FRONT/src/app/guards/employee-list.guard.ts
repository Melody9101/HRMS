import { Injectable } from "@angular/core";
import { CanActivate, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";
import { Role } from "../models/role.enum";

@Injectable({
  providedIn: "root"
})
export class EmployeeListGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) { }

  canActivate(): boolean {
    const role = this.authService.getUserRole();

    if (!role) {
      this.router.navigate(["/home"]);
      return false;
    }

    const allowedRoles = [
      // 總經理
      Role.Boss,
      // 人資主管
      Role.HRManager,
      // 人資部門員工
      Role.HREmployee,
      // 一般部門主管
      Role.GAManager,
      // 會計主管
      Role.AcctManager,
      // 會計員工
      Role.AcctEmployee
    ];

    const isAllowed = allowedRoles.includes(role);

    if (!isAllowed) {
      this.router.navigate(["/home"]);
    }

    return isAllowed;
  }
}
