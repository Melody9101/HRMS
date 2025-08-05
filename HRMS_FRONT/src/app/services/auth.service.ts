import { Injectable } from "@angular/core";
import { HttpClientService } from "./http-client.service";
import { Employee } from "../models/employee.model";
import { Role } from "../models/role.enum";
import { BehaviorSubject, Observable, of } from "rxjs";
import { map, catchError } from "rxjs/operators";

@Injectable({ providedIn: "root" })
export class AuthService {

  /**即時取得登入狀態 */
  loginStatusChanged = new BehaviorSubject<boolean>(false);


  private readonly USER_KEY = "user";
  currentUser: Employee | null = null;

  constructor(private http: HttpClientService) {
    const saved = localStorage.getItem(this.USER_KEY);
    if (saved) {
      this.currentUser = JSON.parse(saved);
    }
  }

  /**
   * 使用帳密登入，後端驗證成功會建立 session
   */
  login(account: string, password: string): Promise<boolean> {
    return new Promise((resolve) => {
      const loginData = { account, password };

      this.http.postApi("http://localhost:8080/HRMS/login", loginData).subscribe({
        next: () => {
          // 登入成功後，呼叫 checkLogin 抓登入者資料
          this.http.getApi("http://localhost:8080/HRMS/checkLogin").subscribe({
            next: (res: any) => {
              if (res && res.employeeInfo) {
                this.currentUser = res.employeeInfo;
                localStorage.setItem(this.USER_KEY, JSON.stringify(res.employeeInfo));
                resolve(true);
                this.loginStatusChanged.next(true);
              } else {
                resolve(false);
              }
            },
            error: () => resolve(false)
          });
        },
        error: () => resolve(false)
      });
    });
  }

  /**
   * 登入成功後，從後端確認目前登入者是誰，並儲存至 localStorage
   */
  checkLogin(): Observable<Employee | null> {
    return this.http.getApi('http://localhost:8080/HRMS/checkLogin').pipe(
      map((res: any) => {
        if (res.code == 200 && res.employee) {
          localStorage.setItem(this.USER_KEY, JSON.stringify(res.employee));
          this.currentUser = res.employee;
          return res.employee;
        }
        return null;
      }),
      catchError(() => of(null))
    );
  }

  /**
   * 登出並清除前端的使用者資訊
   */
  logout(): void {
    this.http.getApi('http://localhost:8080/HRMS/logout').subscribe();
    this.currentUser = null;
    localStorage.removeItem(this.USER_KEY);
    this.loginStatusChanged.next(false);
  }

  /**
   * 從 localStorage 取得使用者資訊
   */
  getUserInfo(): Employee | null {
    const saved = localStorage.getItem(this.USER_KEY);
    return saved ? JSON.parse(saved) : null;
  }

  /**
   * 判斷是否已登入（localStorage 有登入者）
   */
  isLoggedIn(): boolean {
    return !!this.getUserInfo();
  }

  /**
   * 判斷使用者角色（根據職等與部門）
   */
  getUserRole(): Role | null {
    const user = this.getUserInfo();
    if (!user) return null;

    const { grade, department } = user;

    if (grade == 11) return Role.Boss;

    if (department == "HR") {
      if (grade >= 6 && grade <= 10) return Role.HRManager;
      if (grade >= 1 && grade <= 5) return Role.HREmployee;
    }

    if (department == "Acct") {
      if (grade >= 6 && grade <= 10) return Role.AcctManager;
      if (grade >= 1 && grade <= 5) return Role.AcctEmployee;
    }

    if (department == "GA") {
      if (grade >= 6 && grade <= 10) return Role.GAManager;
      if (grade >= 1 && grade <= 5) return Role.GAEmployee;
    }

    return null;
  }

  getUserGrade(): number {
    const userJson = localStorage.getItem('user');
    if (!userJson) return -1;
    try {
      const user = JSON.parse(userJson);
      return user.grade ?? -1;
    } catch {
      return -1;
    }
  }


  /**
   * 判斷登入者是否可以編輯某位員工
   */
  canEditEmployee(target: Employee): boolean {
    const role = this.getUserRole();
    const user = this.getUserInfo();
    if (!role || !user) return false;

    // 不可編輯自己
    if (user.id == target.id) return false;

    const isTargetBoss = target.grade == 11;

    // 總經理可以編輯其他人
    if (role == Role.Boss) return true;

    // 人資主管：可以編輯所有非總經理
    if (role == Role.HRManager) return !isTargetBoss;

    // 人資員工：只能編輯一般員工
    if (role == Role.HREmployee) return target.grade <= 5;

    // 其他部門無權編輯
    return false;
  }

}
