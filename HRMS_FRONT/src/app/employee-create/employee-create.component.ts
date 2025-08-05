// Angular 核心功能
import { Component, signal, inject } from "@angular/core";
import { Router } from "@angular/router";

// Reactive Forms 與 Template Forms
import { FormsModule, ReactiveFormsModule, FormControl, FormGroup, Validators, AbstractControl, ValidationErrors } from "@angular/forms";

// Angular Material
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatDatepickerModule } from "@angular/material/datepicker";

// Material 日期格式與 Adapter
import { DateAdapter, MAT_DATE_FORMATS, provideNativeDateAdapter } from "@angular/material/core";
import { MomentDateAdapter } from "@angular/material-moment-adapter";

// Dialog 與自定義元件
import { MatDialog } from "@angular/material/dialog";
import { GeneralComponent } from "../dialog/general/general.component";

// 專案內部檔案
import { EmployeeService } from "../services/employee-data.service";
import { AddEmployeeReq } from '../models/add-employee-req.model';
import { SnackbarService } from "../snackbar/snackbar.service";


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

@Component({
  selector: "app-employee-create",
  standalone: true,
  providers: [
    // 使用內建日期解析器
    provideNativeDateAdapter(),
    // 使用 Moment 作為日期 adapter
    { provide: DateAdapter, useClass: MomentDateAdapter },
    // 套用自訂的日期格式
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
  ],
  templateUrl: "./employee-create.component.html",
  styleUrl: "./employee-create.component.scss"
})
export class EmployeeCreateComponent {

  // ====== 表單控制相關(含驗證的規則) ======

  departmentControl = new FormControl<Department | null>(null, Validators.required);
  positionControl = new FormControl<Position | null>(null, Validators.required);
  gradeControl = new FormControl<number | null>(null, [Validators.required, Validators.max(11), Validators.min(1)]);
  nameControl = new FormControl("", Validators.required);
  genderControl = new FormControl<Gender | null>(null, Validators.required);
  phoneControl = new FormControl("", [Validators.required, Validators.pattern(/^09\d{8}$/)]);
  emailControl = new FormControl("", [Validators.required, Validators.pattern(/^[\w.-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)]);
  entryDateControl = new FormControl<string | null>(null, Validators.required);
  salariesControl = new FormControl<number | null>(null, [Validators.required, Validators.min(28590)]);

  /**
   * 將表單欄位整合成一個 FormGroup。
   * - 控制如果沒有填寫，將會跳出紅字提醒
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
  }, {
    validators: this.positionGradeValidator.bind(this)
  });

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
    { id: 2, name: "女" }
  ];

  // 職稱對應的職等
  bossGrade = 11;
  managerGrades: number[] = [10, 9, 8, 7, 6];
  employeeGrades: number[] = [5, 4, 3, 2, 1];

  // 可選職等，依照職稱而變
  availableGrades: number[] = [];

  // 建構式
  constructor(
    private router: Router,
    private employeeService: EmployeeService,
    private snackbar: SnackbarService,
  ) { }

  // 初始化
  ngOnInit() {
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

      // 如果原本選的職等不符合現在的職稱範圍，就清空
      if (!this.availableGrades.includes(this.gradeControl.value!)) {
        this.gradeControl.setValue(null);
      }
    });
  }

  // 使用 inject API 注入 MatDialog
  readonly dialog = inject(MatDialog);

  /**
   * 自定驗證器，檢查職稱與職等是否對應正確
   */
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

  /**
   * 當使用者按下「送出」時執行：
   * - 驗證欄位是否都填寫正確
   * - 開啟確認視窗
   * - 若確認送出，就建立員工資料並回到列表頁
   */
  submit() {
    // 確認每個必填都已填寫，若沒有填寫則會跳出提示
    if (this.formGroup.invalid) {
      this.formGroup.markAllAsTouched();
      return;
    }
    let formValue = this.formGroup.value;
    let dialogRef = this.dialog.open(GeneralComponent, {
      data: {
        title: "確定要建立資料嗎？",
        showCancel: true
      },
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result == true) {
        let newEmployee: AddEmployeeReq = {
          department: formValue.department!.id,
          position: formValue.position!.id,
          grade: formValue.grade!,
          name: formValue.name!,
          gender: formValue.gender!.id == 1,
          phone: formValue.phone!,
          email: formValue.email!,
          entryDate: formValue.entryDate!,
          salaries: formValue.salaries!,
        };
        // 呼叫後端API新增員工
        this.employeeService.addEmployee(newEmployee).subscribe({
          next: (res) => {
            if (res.code == 409) {
              this.snackbar.error("該Email已存在！");
              return;
            }
            this.snackbar.success("建立成功！");
            this.router.navigate(["/employeeList"]);
          },
          error: (error) => {
            console.error("建立失敗", error);
            this.snackbar.error("建立失敗，請稍後再試");
          }
        });
      }
    });
  }

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
   * 小導覽條-新增員工
   */
  backToEmployeeCreate() {
    this.router.navigate(["/employeeCreate"]);
  }
}
