// Angular 核心
import { Component, signal, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

// Angular Forms
import { FormsModule, ReactiveFormsModule, FormControl, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';

// Angular Material
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

// Angular Material 日期格式與適配器
import { DateAdapter, MAT_DATE_FORMATS, provideNativeDateAdapter } from '@angular/material/core';
import { MomentDateAdapter } from '@angular/material-moment-adapter';

// RxJS
import { forkJoin } from 'rxjs';

// 專案內部檔案
import { GeneralComponent } from '../dialog/general/general.component';
import { Employee } from '../models/employee.model';
import { EmployeeService } from '../services/employee-data.service';
import { AuthService } from '../services/auth.service';
import { SnackbarService } from '../snackbar/snackbar.service';

// 自訂的日期格式設定
export const MY_FORMATS = {
  parse: {
    // 使用者輸入的日期格式
    dateInput: "YYYY/MM/DD",
  },
  display: {
    // 表單欄位中顯示的格式
    dateInput: "YYYY/MM/DD",
    // 月曆上方的月份與年份顯示格式
    monthYearLabel: "MMM YYYY",
  },
};

// 宣告部門的型別
interface Department {
  id: string;
  name: string;
}

// 宣告職稱的型別
interface Position {
  id: string;
  name: string;
}

// 宣告性別的型別
interface Gender {
  id: number;
  name: string;
}

// 宣告是否在職的型別
interface IsEmployed {
  id: number;
  name: string;
}

@Component({
  selector: 'app-employee-edit',
  standalone: true,
  providers: [
    provideNativeDateAdapter(),
    // 使用 Moment 作為日期 adapter
    { provide: DateAdapter, useClass: MomentDateAdapter },
    // 套用你自訂的日期格式
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS },
  ],
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    MatDatepickerModule,
    MatDialogModule,
  ],
  templateUrl: './employee-edit.component.html',
  styleUrl: './employee-edit.component.scss'
})
export class EmployeeEditComponent {

  // ====== 顯示與屬性 ======

  // 控制密碼欄位是否隱藏
  readonly hide = signal(true);
  // 儲存錯誤訊息
  readonly errorMessage = signal("");
  // 宣告 employee 屬性，用來儲存從資料中找到的員工資料
  employee!: Employee;

  // ====== 表單控制相關(含驗證的規則) ======

  departmentControl = new FormControl<Department | null>(null, Validators.required);
  positionControl = new FormControl<{ id: string, name: string } | null>(null);
  gradeControl = new FormControl<number | null>(null, [Validators.required, Validators.max(11), Validators.min(1)]);
  nameControl = new FormControl("", Validators.required);
  genderControl = new FormControl<Gender | null>(null, Validators.required);
  phoneControl = new FormControl("", [Validators.required, Validators.pattern(/^09\d{8}$/)]);
  emailControl = new FormControl("", [Validators.required, Validators.pattern(/^[\w.-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)]);
  entryDateControl = new FormControl<string | null>(null, Validators.required);
  salariesControl = new FormControl<number | null>(null, [Validators.required, Validators.min(28590)]);
  isEmployedControl = new FormControl<IsEmployed | null>(null, Validators.required);
  resignationDateControl = new FormControl<string | null>(null);
  resignationReasonControl = new FormControl("", Validators.required);
  unpaidLeaveStartDateControl = new FormControl<string | null>(null);
  unpaidLeaveEndDateControl = new FormControl<string | null>(null);
  unpaidLeaveReasonControl = new FormControl("", Validators.required);

  /**
   * 控制如果沒有填寫，將會跳出紅字提醒
   */
  formGroup = new FormGroup({
    department: this.departmentControl,
    position: this.positionControl,
    grade: this.gradeControl,
    name: this.nameControl,
    gender: this.genderControl,
    phone: this.phoneControl,
    email: this.emailControl,
    entryDate: this.entryDateControl,
    salaries: this.salariesControl,
    isEmployed: this.isEmployedControl,
    resignationDate: this.resignationDateControl,
    resignationReason: this.resignationReasonControl,
    unpaidLeaveStartDate: this.unpaidLeaveStartDateControl,
    unpaidLeaveEndDate: this.unpaidLeaveEndDateControl,
    unpaidLeaveReason: this.unpaidLeaveReasonControl,
  }, {
    validators: this.positionGradeValidator.bind(this)
  });

  // ====== 下拉選單與常數 ======

  // 部門下拉選單
  departments: Department[] = [
    { id: "Boss", name: "總經理" },
    { id: "HR", name: "人資部門" },
    { id: "Acct", name: "會計部門" },
    { id: "GA", name: "一般部門" },
  ];

  // 職稱下拉選單
  positions: Position[] = [
    { id: "Boss", name: "總經理" },
    { id: "Manager", name: "主管" },
    { id: "Employee", name: "員工" },
  ];

  // 性別下拉選單
  genders: Gender[] = [
    { id: 1, name: "男" },
    { id: 2, name: "女" },
  ];

  // 在職狀態下拉選單
  isEmployed: IsEmployed[] = [
    { id: 1, name: "在職" },
    { id: 2, name: "已離職" },
  ]

  bossGrade = 11;
  managerGrades: number[] = [10, 9, 8, 7, 6];
  employeeGrades: number[] = [5, 4, 3, 2, 1];
  // 選中的職等選項
  availableGrades: number[] = [];

  // 建構式
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private employeeService: EmployeeService,
    private authService: AuthService,
    private snackbar: SnackbarService,
  ) { }

  // ====== 權限判斷 ======
  get isHREmployee(): boolean {
    let user = this.authService.getUserInfo();
    return user?.department == "HR" && user?.grade <= 5;
  }

  get isHRManager(): boolean {
    let user = this.authService.getUserInfo();
    return user?.department == "HR" && user?.grade <= 10;
  }

  // ====== 初始化 ======
  ngOnInit() {
    // 從URL中取得ID
    let id = this.route.snapshot.paramMap.get("id");
    if (id) {
      this.employeeService.getById(Number(id)).subscribe({
        next: (employee) => {
          if (!employee) {
            alert("查無此員工資料");
            this.router.navigate(["/home"]);
            return;
          }
          if (!this.authService.canEditEmployee(employee)) {
            alert("你沒有權限編輯此員工資料");
            this.router.navigate(["/home"]);
            return;
          }
          this.employee = employee;
          // 將員工資料填入表單中
          this.formGroup.patchValue({
            department: this.departments.find(department => department.id == employee.department) ?? null,
            position: this.positions.find(position => position.id == employee.position) ?? null,
            grade: employee.grade,
            name: employee.name,
            gender: this.genders.find(gender => gender.id == (employee.gender ? 1 : 2)) ?? null,
            phone: employee.phone,
            email: employee.email,
            entryDate: employee.entryDate,
            salaries: employee.salaries,
            isEmployed: this.isEmployed.find(employed => employed.id == (employee.employed ? 1 : 2)) ?? null,
            resignationDate: employee.resignationDate,
            resignationReason: employee.resignationReason,
            unpaidLeaveStartDate: employee.unpaidLeaveStartDate,
            unpaidLeaveEndDate: employee.unpaidLeaveEndDate,
            unpaidLeaveReason: employee.unpaidLeaveReason,
          });

          this.unpaidLeaveStartDateControl.valueChanges.subscribe(() => {
            this.updateUnpaidLeaveValidators();
          });

          let currentPosition = this.positions.find(position => position.id == employee.position);
          if (currentPosition) {
            if (currentPosition.id == "Boss") {
              this.availableGrades = [this.bossGrade];
            } else if (currentPosition.id == "Manager") {
              this.availableGrades = this.managerGrades;
            } else if (currentPosition.id == "Employee") {
              this.availableGrades = this.employeeGrades;
            }
          }
          this.positionControl.valueChanges.subscribe((position) => {
            if (!position) {
              this.availableGrades = [];
              return;
            }
            if (position.id == "Boss") {
              this.availableGrades = [this.bossGrade];
            } else if (position.id == "Manager") {
              this.availableGrades = this.managerGrades;
            } else if (position.id == "Employee") {
              this.availableGrades = this.employeeGrades;
            }
            // 如果當前 grade 不符合新的職稱，就清空它
            if (!this.availableGrades.includes(this.gradeControl.value!)) {
              this.gradeControl.setValue(null);
            }
          });

          this.updateResignationValidators();

          // 限制人資員工無法更改
          if (this.isHREmployee || this.isHRManager) {
            this.departmentControl.disable();
            this.positionControl.disable();
            this.gradeControl.disable();
            this.entryDateControl.disable();
            this.salariesControl.disable();
            this.isEmployedControl.disable();
            this.unpaidLeaveStartDateControl.disable();
          }

          if (this.isHREmployee) {
            this.isEmployedControl.disable();
            this.resignationDateControl.disable();
            this.resignationReasonControl.disable();
            this.unpaidLeaveStartDateControl.disable();
            this.unpaidLeaveEndDateControl.disable();
            this.unpaidLeaveReasonControl.disable();
          }
        },
        error: (error) => {
          console.error("取得員工資料失敗", error);
          alert("系統錯誤，無法讀取員工資料");
          this.router.navigate(["/home"]);
        }
      });
    }

    // 監聽在職狀態的變化
    this.isEmployedControl.valueChanges.subscribe(() => {
      this.updateResignationValidators();
    });

    // 監聽「停薪起」欄位，如果有填寫才出現後面欄位
    this.unpaidLeaveStartDateControl.valueChanges.subscribe(value => {
      // !!為轉成布林值
      let hasStartDate = !!value;

      if (hasStartDate) {
        this.unpaidLeaveEndDateControl.addValidators(Validators.required);
        this.unpaidLeaveReasonControl.addValidators(Validators.required);
      } else {
        this.unpaidLeaveEndDateControl.clearValidators();
        this.unpaidLeaveEndDateControl.setValue(null);
        this.unpaidLeaveReasonControl.clearValidators();
        this.unpaidLeaveReasonControl.setValue("");
      }
      this.unpaidLeaveEndDateControl.updateValueAndValidity();
      this.unpaidLeaveReasonControl.updateValueAndValidity();
    });
  }

  // ====== 驗證規則與欄位切換邏輯 ======

  // 根據在職狀態切換離職驗證欄位
  updateResignationValidators() {
    let isLeft = this.isEmployedControl.value?.name == "已離職";

    if (isLeft) {
      this.resignationDateControl.addValidators(Validators.required);
      this.resignationReasonControl.addValidators(Validators.required);
    } else {
      this.resignationDateControl.clearValidators();
      this.resignationDateControl.setValue(null);
      this.resignationReasonControl.clearValidators();
      this.resignationReasonControl.setValue("");
      this.formGroup.get('resignationReason')?.setErrors(null);
    }
    this.resignationDateControl.updateValueAndValidity();
    this.resignationReasonControl.updateValueAndValidity();
  }

  updateUnpaidLeaveValidators() {
    let start = this.unpaidLeaveStartDateControl;
    let end = this.unpaidLeaveEndDateControl;
    let reason = this.unpaidLeaveReasonControl;

    if (start.value) {
      end.setValidators([Validators.required]);
      reason.setValidators([Validators.required]);
    } else {
      end.clearValidators();
      reason.clearValidators();
      end.setValue(null);
      reason.setValue(null);
      end.setErrors(null);
      reason.setErrors(null);
    }
    end.updateValueAndValidity();
    reason.updateValueAndValidity();
  }

  positionGradeValidator(group: AbstractControl): ValidationErrors | null {
    let position = group.get("position")?.value?.id;
    let grade = group.get("grade")?.value;
    if (!position || grade == null) return null;
    if (
      (position == "Boss" && grade != 11) ||
      (position == "Manager" && (grade <= 5 || grade >= 11)) ||
      (position == "Employee" && (grade <= 0 || grade >= 6))
    ) {
      return { positionGradeMismatch: true };
    }
    return null;
  }

  // ====== 顯示輔助函式 ======

  /**
   * 將部門中文名稱轉換為英文代號(用於搜尋篩選)
   */
  mapDepartmentNameToCode(name: string): string {
    switch (name) {
      case "總經理": return "Boss";
      case "人資部門": return "HR";
      case "會計部門": return "Acct";
      case "一般部門": return "GA";
      default: return name;
    }
  }

  // ====== 表單提交邏輯 ======

  // 使用 inject API 注入 MatDialog
  readonly dialog = inject(MatDialog);

  /**
   * 提交表單前跳出確認視窗，並且在按下確認後才會跳轉畫面
   */
  submit() {
    // 確保每個欄位驗證失敗時能觸發錯誤提示
    Object.keys(this.formGroup.controls).forEach(key => {
      let control = this.formGroup.get(key);
      if (control && control.invalid) {
      }
    });

    // 若表單整體無效，標示錯誤並中止送出
    if (this.formGroup.invalid) {
      this.formGroup.markAllAsTouched();
      return;
    }

    // 確認的dialog
    let dialogRef = this.dialog.open(GeneralComponent, {
      data: {
        title: "確定要更新資料嗎？",
        showCancel: true
      },
      disableClose: true,
    });

    // 等待使用者關閉 Dialog 後的回傳結果
    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result == true) {
        let formValue = this.formGroup.getRawValue();

        // 權限判斷（人資員工不得修改職稱、職等、薪資）
        let currentUser = this.authService.getUserInfo();
        let isHRStaff = currentUser?.department == "HR" && currentUser?.grade <= 5;
        if (!isHRStaff) {
          this.snackbar.error("您沒有權限修改職稱、職等或薪資資訊");
          return;
        }

        // 基本資料準備
        let basicInfo = {
          id: this.employee.id,
          name: formValue.name!,
          email: formValue.email!,
          phone: formValue.phone!,
          gender: formValue.gender!.id == 1,
        };

        // 職務資料準備
        let jobInfo = {
          id: this.employee.id,
          department: this.mapDepartmentNameToCode(formValue.department!.name),
          position: formValue.position!.id,
          grade: formValue.grade!,
          salaries: formValue.salaries!,
        };

        // 建立請求清單
        let updateRequests: any = {
          basic: this.employeeService.updateEmployeeBasicInfo(basicInfo),
          job: this.employeeService.updateEmployeeJob(jobInfo),
        };

        // 有填停薪資訊才加進更新清單
        if (formValue.unpaidLeaveStartDate) {
          updateRequests.unpaidLeave = this.employeeService.updateEmployeeUnpaidLeaveById({
            id: this.employee.id,
            unpaidLeaveStartDate: formValue.unpaidLeaveStartDate!,
            unpaidLeaveEndDate: formValue.unpaidLeaveEndDate!,
            unpaidLeaveReason: formValue.unpaidLeaveReason!,
          });
        }

        // 有填離職資訊才加進更新清單
        if (formValue.resignationDate) {
          updateRequests.resignation = this.employeeService.resignEmployee({
            id: this.employee.id,
            resignationDate: formValue.resignationDate!,
            resignationReason: formValue.resignationReason!,
          });
        }

        // 執行所有更新（forkJoin可同時處理多個API）
        forkJoin(updateRequests).subscribe({
          next: () => {
            this.snackbar.success("更新成功");
            this.router.navigate(["/employeeList"]);
          },
          error: (error) => {
            console.error("更新失敗", error);
            this.snackbar.error("更新失敗，請稍後再試");
          }
        });
      }
    });
  }

  // ====== 小導覽功能 ======

  /**
  * 小導覽條-首頁
  */
  backToHome() {
    this.router.navigate(["/home"]);
  }

  /**
   * 小導覽條-員工表單
   */
  backToEmployeeList() {
    this.router.navigate(["/employeeList"]);
  }

  /**
   * 小導覽條-返回員工詳細資訊(管理員)
   */
  backToEmployeeDetail() {
    this.router.navigate(["/employeeDetail", this.employee.id]);
  }

  /**
   * 小導覽條-返回編輯員工資訊
   */
  backToEmployeeEdit() {
    this.router.navigate(["/employeeEdit/"]);
  }
}
