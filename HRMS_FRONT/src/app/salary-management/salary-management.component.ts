import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from "@angular/core";
import { FormControl, FormGroup, Validators, ReactiveFormsModule, FormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import { CommonModule } from "@angular/common";
import { MatTabsModule } from "@angular/material/tabs";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";

import { EmployeeService } from "../services/employee-data.service";
import { Employee } from "../models/employee.model";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";

import { inject } from '@angular/core';

import { firstValueFrom } from 'rxjs';
import { SnackbarService } from "../snackbar/snackbar.service";
import { GeneralComponent } from "../dialog/general/general.component";
import { MonthlySalaryDetail } from "../models/salary-model";
import { LeaveRecord } from "../models/leave-record.model";

@Component({
  selector: "app-salary-management",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatTabsModule,
    MatIconModule,
    MatButtonModule,
    MatDialogModule,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: "./salary-management.component.html",
  styleUrls: ["./salary-management.component.scss"]
})
export class SalaryManagementComponent implements OnInit {

  // 表單欄位與資料屬性
  employeeName: string = "";
  baseSalary: number = 0;
  selectedEmployee: Employee | null = null;
  selectedEmployeeId: number | null = null;
  employeeId: number | null = null;

  // 員工與下拉選單資料
  employees: Employee[] = [];
  years: number[] = [];
  months: number[] = [];

  // 計算薪資結果
  calculatedSalary: number | null = null;
  calculatedSalaryDetail: MonthlySalaryDetail | null = null;

  // UI控制狀態
  isSubmitting = false;
  expandedRecord: LeaveRecord | null = null;
  showLeaveRecords: boolean = false;

  // 薪資試算的控制
  formGroupCalc = new FormGroup({
    year: new FormControl<number | null>(null, [Validators.required]),
    month: new FormControl<number | null>(null, [Validators.required]),
    bonus: new FormControl<number>(0, [Validators.min(0)])
  });

  // 使用inject API注入MatDialog
  readonly dialog = inject(MatDialog);

  // 建構式
  constructor(
    private employeeService: EmployeeService,
    private router: Router,
    private snackbar: SnackbarService,
    private cd: ChangeDetectorRef
  ) { }

  // 初始化
  ngOnInit(): void {
    // 載入員工清單
    this.employeeService.getAll().subscribe(data => {
      this.employees = data;
    });

    // 初始化年月選單
    let currentYear = new Date().getFullYear();
    this.years = Array.from({ length: currentYear - 2009 }, (_, i) => i + 2010);
    this.months = Array.from({ length: 12 }, (_, i) => i + 1);
  }

  // ====== 表單與員工選擇邏輯 ======

  onEmployeeChange(): void {
    let selected = this.employees.find(employee => employee.id == this.selectedEmployeeId);
    if (selected) {
      this.updateSelectedEmployee(selected);
      this.selectedEmployee = selected;
    } else {
      this.selectedEmployee = null;
      this.employeeId = null;
      this.employeeName = '';
      this.baseSalary = 0;
    }
  }

  updateSelectedEmployee(employee: Employee): void {
    this.employeeId = employee.id;
    this.employeeName = employee.name;
    this.baseSalary = employee.salaries;
  }

  /**
   * 薪資試算
   */
  calculate() {
    let formValue = this.formGroupCalc.value;

    this.employeeService.calculateMonthlySalary({
      employeeId: this.selectedEmployeeId!,
      year: formValue.year!,
      month: formValue.month!,
      bonus: formValue.bonus ?? 0
    }).subscribe({
      next: (res) => {
        // 顯示總額
        this.calculatedSalary = res.monthlySalaryDetails.finalCalculatedSalary;
        // 顯示細節用
        this.calculatedSalaryDetail = res.monthlySalaryDetails;
        this.cd.detectChanges();

      },
      error: (error) => {
        console.error("薪資試算失敗", error);
      }
    });
  }

  async confirm() {
    if (this.formGroupCalc.invalid || !this.selectedEmployee) {
      this.formGroupCalc.markAllAsTouched();
      return;
    }

    const dialogRef = this.dialog.open(GeneralComponent, {
      data: {
        title: "確定要建立薪資資料嗎？",
        showCancel: true
      },
      disableClose: true
    });

    const result = await firstValueFrom(dialogRef.afterClosed());
    if (!result) return;

    this.isSubmitting = true;
    const { year, month, bonus } = this.formGroupCalc.value;

    this.employeeService.addSalaries({
      employeeId: this.selectedEmployee.id,
      year: year!,
      month: month!,
      salary: this.selectedEmployee.salaries,
      bonus: bonus ?? 0
    }).subscribe({
      next: (res) => {
        this.isSubmitting = false;
        if (res.code == 200) {
          this.snackbar.success('新增薪資成功');
          this.router.navigate(["/employeeList"]);
        } else {
          this.snackbar.error('新增薪資失敗');
        }
      },
      error: (error) => {
        this.snackbar.error('新增薪資時發生錯誤');
        console.error(error);
        this.isSubmitting = false;
      }
    });
  }

  // ====== 頁面導向功能 ======

  /**
   * 小導覽條: 返回首頁
   */
  backToHome() {
    this.router.navigate(["/home"]);
  }

  /**
   * 小導覽條: 返回員工清單
   */
  backToEmployeeList() {
    this.router.navigate(["/employeeList"]);
  }

  /**
   * 小導覽條: 返回薪資管理
   */
  backToSalaryManagement() {
    this.router.navigate(["/salaryManagement"]);
  }
}
