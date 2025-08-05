import { Injectable } from "@angular/core";
import { CanActivate, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";

@Injectable({
  providedIn: "root"
})
export class EmployeeCreateGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) { }

  canActivate(): boolean {
    const emp = this.authService.getUserInfo();
    if (!emp) return false;

    const isGM = emp.grade == 50;
    const isHRManager = emp.grade >= 30 && emp.grade < 50 && emp.department == "人資部門";
    const isHREmployee = emp.grade >= 5 && emp.grade < 30 && emp.department == "人資部門";

    const allowed = isGM || isHRManager || isHREmployee;
    if (!allowed) this.router.navigate(["/home"]);
    return allowed;
  }
}
