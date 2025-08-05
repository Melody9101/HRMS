// Angular 核心模組
import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";

// RxJS
import { forkJoin, Observable } from "rxjs";

// Angular Material
import { MatButtonModule } from "@angular/material/button";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { MatTableModule } from "@angular/material/table";
import { MatTabsModule } from "@angular/material/tabs";

// 專案內部模型與列舉
import { Employee } from "../models/employee.model";
import { LeaveRecord } from "../models/leave-record.model";
import { SalariesRecord } from "../models/salaries-record.model";
import { LeaveBalanceRecord } from "../models/leave-balance-record.model";
import { Role } from "../models/role.enum";

// 專案工具與服務
import { EmployeeService } from "../services/employee-data.service";
import { AuthService } from "../services/auth.service";
import { getGradeLabel } from "../utils/grade-label.util";

@Component({
  selector: "app-employee-detail",
  standalone: true,
  imports: [
    MatTabsModule,
    CommonModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    FormsModule,
    ReactiveFormsModule,
    MatSelectModule,
    MatTableModule,
  ],
  templateUrl: "./employee-detail.component.html",
  styleUrl: "./employee-detail.component.scss",
})
export class EmployeeDetailComponent {

  // 公用工具函式
  getGradeLabel = getGradeLabel;

  // ====== 畫面欄位設定 ======

  displayedColumns: string[] = ["leaveType", "totalHours", "usedHours", "remainingHours"];
  salariesDisplayedColumns: string[] = ["salariesMonth", "baseSalaries", "allowance", "leaveDeduction", "actualSalaries"];


  // ====== 資料模型屬性 ======

  employee!: Employee;
  leaveData: LeaveBalanceRecord[] = [];
  leaveRecords: LeaveRecord[] = [];
  salariesRecords: SalariesRecord[] = [];
  filteredSalaries: SalariesRecord[] = [];

  years: number[] = [];
  months: number[] = [];
  selectedYear: number | null = null;
  selectedMonth: number | null = null;

  // ====== 登入者與角色 ======

  user!: Employee;
  userRole!: Role;

  // 建構式
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private employeeService: EmployeeService,
    public authService: AuthService,
  ) {
    let id = Number(this.route.snapshot.paramMap.get("id"));
    this.employeeService.getById(id).subscribe((found) => {
      if (found) {
        this.employee = found;

        // 初始化登入者資訊與角色
        this.user = this.authService.getUserInfo()!;
        this.userRole = this.authService.getUserRole()!;

        // 權限擋控
        if (!this.canView()) {
          alert("你沒有權限查看此員工資料");
          this.router.navigate(["/home"]);
          return;
        }

        // 設定每種假別的「固定總時數」：特休 9 天、有薪病假 90 天（以小時計算）
        let totalHoursMap: Record<string, number> = {
          "前一年度特休": 9 * 8,     // 72 小時
          "本年度特休": 9 * 8,       // 72 小時
          "有薪病假": 90 * 8         // 720 小時
        };

        // 從 employee 拿剩餘時數（後端傳的是天數，我們要轉成小時）
        let remainingMap: Record<string, number> = {
          "前一年度特休": this.employee.remainingPreviousAnnualLeave * 8,
          "本年度特休": this.employee.remainingCurrentAnnualLeave * 8,
          "有薪病假": this.employee.remainingPaidSickLeave * 8
        };

        // 建立 leaveData 陣列
        this.leaveData = Object.keys(totalHoursMap).map((leaveType) => {
          let total = totalHoursMap[leaveType];
          let remaining = remainingMap[leaveType];
          let used = total - remaining;
          return {
            leaveType,
            totalHours: total,
            usedHours: used,
            remainingHours: remaining
          };
        });
        // 載入薪資資料
        this.loadSalaryData();
      } else {
        alert("查無此員工資料");
        this.router.navigate(["/employeeList"]);
      }
    });
  }

  // ====== 畫面初始化邏輯 ======

  ngOnInit() {
    // 初始化登入者資訊與角色
    this.user = this.authService.getUserInfo()!;
    this.userRole = this.authService.getUserRole()!;
    // 權限擋控邏輯（防止直接輸入網址）
    if (!this.canView()) {
      alert("你沒有權限查看此員工資料");
      this.router.navigate(["/home"]);
      return;
    }
    if (this.employee) {
      this.loadSalaryData();
    }
  }

  // ====== UI工具函式 ======

  convertHourToDayHour(hours: number): string {
    let fullDays = Math.floor(hours / 8);
    let remainingHours = Math.round((hours % 8) * 10) / 10;
    return `${fullDays}天${remainingHours}小時`;
  }

  getDepartmentName(code: string): string {
    let map: Record<string, string> = {
      Boss: "總經理",
      HR: "人資部門",
      Acct: "會計部門",
      GA: "一般部門",
    };
    return map[code] ?? code;
  }

  /**
   * 使用者選擇年份或月份時，根據選擇值篩選出對應薪資資料
   */
  onSelectionChange() {
    this.filteredSalaries = [];

    // 只選年份，並顯示整年的薪資條
    if (this.selectedYear && !this.selectedMonth) {
      let prefix = `${this.selectedYear}/`;
      this.filteredSalaries = this.salariesRecords.filter(salaries =>
        salaries.salariesMonth.startsWith(prefix)
      );

      // 選年份和月份，並顯示單月的薪資條
    } else if (this.selectedYear && this.selectedMonth) {
      let monthStr = String(this.selectedMonth).padStart(2, "0");
      let formatted = `${this.selectedYear}/${monthStr}`;
      this.filteredSalaries = this.salariesRecords.filter(salaries =>
        salaries.salariesMonth == formatted
      );

      // 沒選年份但有選月份，顯示每一年的某月薪資
    } else if (!this.selectedYear && this.selectedMonth) {
      let monthStr = String(this.selectedMonth).padStart(2, "0");
      this.filteredSalaries = this.salariesRecords.filter(salaries =>
        salaries.salariesMonth.endsWith(`/${monthStr}`)
      );

      // 全部都沒選 → 顯示所有薪資
    } else {
      this.filteredSalaries = [...this.salariesRecords];
    }
  }

  // ====== 權限判斷 ======

  /**
   * 根據職等決定能否觀看該員工資料
   */
  canView(): boolean {
    if (!this.user || !this.userRole) return false;
    let isSelf = this.user.id == this.employee.id;
    if (isSelf) return true;
    if (this.userRole == Role.Boss) return true;
    if (this.userRole == Role.HRManager) return true;
    if (this.userRole == Role.HREmployee) return this.employee.grade <= 5;
    if (this.userRole == Role.AcctManager) return true;
    if (this.userRole == Role.AcctEmployee) return true;
    if (this.userRole == Role.GAManager) return this.employee.department == "一般部門";
    return false;
  }

  /**
   * 根據職等決定能否觀看該員工清單
   */
  canViewEmployeeList(): boolean {
    return (
      this.userRole == Role.Boss ||
      this.userRole == Role.HRManager ||
      this.userRole == Role.HREmployee ||
      this.userRole == Role.GAManager ||
      this.userRole == Role.AcctEmployee ||
      this.userRole == Role.AcctManager
    );
  }

  /**
   * 根據職等決定能否觀看該員工假期
   */
  canViewLeave(): boolean {
    if (!this.userRole || !this.user) return false;
    let isSelf = this.user.id == this.employee.id;
    if (isSelf) return true;
    if (
      this.userRole == Role.Boss ||
      this.userRole == Role.HRManager ||
      this.userRole == Role.HREmployee ||
      this.userRole == Role.AcctManager
    ) return true;
    if (this.userRole == Role.AcctEmployee) return this.employee.grade <= 5;
    return false;
  }

  /**
   * 根據職等決定能否觀看該員工薪資
   */
  canViewSalaries(): boolean {
    if (!this.userRole || !this.user) return false;
    let isSelf = this.user.id == this.employee.id;
    if (isSelf) return true;
    if (this.userRole == Role.Boss || this.userRole == Role.AcctManager) return true;
    if (this.userRole == Role.AcctEmployee) return this.employee.grade <= 5;
    return false;
  }

  // ====== 資料載入 ======

  loadSalaryData() {
    if (!this.employee?.id) return;

    let employeeId = this.employee.id;
    let currentYear = new Date().getFullYear();
    let startYear = 2023;
    let requestMap: Record<string, Observable<any>> = {};
    let isSelf = this.user.id == this.employee.id;

    for (let year = startYear; year <= currentYear; year++) {
      for (let month = 1; month <= 12; month++) {
        let key = `${year}_${month}`;
        if (isSelf) {
          requestMap[key] = this.employeeService.getMySalaryByYearMonth({ year, month });
        } else if (
          this.userRole == Role.Boss ||
          this.userRole == Role.AcctManager ||
          this.userRole == Role.AcctEmployee
        ) {
          requestMap[key] = this.employeeService.getAllSalariesOrByEmployeeIdList({
            idList: [employeeId],
            year,
            month
          });
        }
      }
    }

    forkJoin(requestMap).subscribe({
      next: (resultsMap) => {
        let allSalaries: any[] = [];
        for (let key in resultsMap) {
          let res = resultsMap[key];
          if (res?.salariesList?.length > 0) {
            allSalaries.push(...res.salariesList);
          }
          if (res?.salary) {
            allSalaries.push(res.salary);
          }
        }

        this.salariesRecords = allSalaries.map((item: any) => {
          let year = item.year;
          let month = String(item.month).padStart(2, "0");
          return {
            employeeId: this.employee.id,
            salariesYear: year,
            salariesMonth: `${year}/${month}`,
            baseSalaries: item.salary,
            allowance: item.bonus,
            actualSalaries: item.salary + item.bonus,
            leaveDeduction: item.leaveDeduction ?? 0,
          };
        });

        // 建立年份與月份下拉選單
        let yearSet = new Set<number>();
        let monthSet = new Set<number>();
        for (let record of this.salariesRecords) {
          let [yearStr, monthStr] = record.salariesMonth.split("/");
          yearSet.add(Number(yearStr));
          monthSet.add(Number(monthStr));
        }
        this.years = Array.from(yearSet).sort((a, b) => a - b);
        this.months = Array.from(monthSet).sort((a, b) => a - b);
        this.onSelectionChange();
      },
      error: (error) => {
        console.error("薪資資料查詢失敗", error);
      }
    });
  }

  // ====== 小導覽功能 ======

  /**
   * 小導覽條-返回首頁的按鈕
   */
  backToHome() {
    this.router.navigate(["/home"]);
  }

  /**
   * 小導覽條-返回員工清單
   */
  backToEmployeeList() {
    this.router.navigate(["/employeeList"]);
  }

  /**
   * 小導覽條-返回員工詳細資訊
   */
  backToEmployeeDetail() {
    this.router.navigate(["/employeeDetail/:id"]);
  }
}
