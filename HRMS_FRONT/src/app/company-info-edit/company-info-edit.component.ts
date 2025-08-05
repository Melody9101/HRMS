import { Component, Inject, inject } from "@angular/core";
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import { CommonModule } from "@angular/common";

// Material
import { MatButtonModule } from "@angular/material/button";
import { MatInputModule } from "@angular/material/input";
import { MatIconModule } from "@angular/material/icon";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";

// Services & Models
import { CompanyInfoService } from "../services/company-info.service";
import { TIMEZONES_ZH } from "../shared/constants/timezone-options-zh";
import { GeneralComponent } from "../dialog/general/general.component";
import { SnackbarService } from "../snackbar/snackbar.service";

@Component({
  selector: "app-company-info-edit",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatInputModule,
    MatIconModule,
    MatDialogModule,
  ],
  templateUrl: "./company-info-edit.component.html",
  styleUrl: "./company-info-edit.component.scss"
})
export class CompanyInfoEditComponent {

  timezoneOptions = TIMEZONES_ZH;

  readonly dialog = inject(MatDialog);
  readonly router = inject(Router);
  readonly companyService = inject(CompanyInfoService);

  formGroup = new FormGroup({
    name: new FormControl("", Validators.required),
    taxIdNumber: new FormControl(0, [Validators.required, Validators.pattern(/^\d{8}$/)]),
    ownerName: new FormControl("", Validators.required),
    phone: new FormControl("", Validators.required),
    email: new FormControl("", [Validators.required, Validators.email]),
    address: new FormControl("", Validators.required),
    website: new FormControl(""),
    establishmentDate: new FormControl("", Validators.required),
    capitalAmount: new FormControl(0),
    employeeCount: new FormControl(0),
    status: new FormControl("", Validators.required),
    workStartTime: new FormControl("", Validators.required),
    lunchStartTime: new FormControl("", Validators.required),
    lunchEndTime: new FormControl("", Validators.required),
    timezone: new FormControl("", Validators.required),
  });

  constructor(
    private snackbar: SnackbarService,
  ) { }

  // 從後端帶入公司資訊
  ngOnInit(): void {
    this.companyService.getCompanyInfo().subscribe({
      next: (res) => {
        this.formGroup.patchValue(res.companyInfo);
      },
      error: (error) => {
        console.error("載入公司資料失敗", error);
        alert("載入失敗，請稍後再試");
      },
    });
  }

  /**
   * 按下送出的按鈕，跳出確認的dialog，送出後把資料存入資料庫
   */
  submit(): void {
    if (this.formGroup.invalid) {
      Object.entries(this.formGroup.controls).forEach(([key, control]) => {
        if (control.invalid) {
        }
      });
      this.formGroup.markAllAsTouched();
      return;
    }

    const dialogRef = this.dialog.open(GeneralComponent, {
      data: {
        title: "確定要更新公司資訊嗎？",
        showCancel: true
      },
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        const updateData = this.formGroup.getRawValue() as {
          name: string;
          taxIdNumber: number;
          ownerName: string;
          phone: string;
          email: string;
          address: string;
          website: string;
          establishmentDate: string;
          capitalAmount: number;
          employeeCount: number;
          status: string;
          workStartTime: string;
          lunchStartTime: string;
          lunchEndTime: string;
          timezone: string;
        };
        this.companyService.updateCompanyInfo(updateData).subscribe({
          next: () => {
            // 成功提示並導回公司資訊頁
            this.snackbar.success("公司資訊已成功更新");
            this.router.navigate(["/company-info"]);
          },
          error: () => {
            // 失敗提示
            this.snackbar.error("更新失敗，請稍後再試");
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
   * 小導覽條-公司資訊
   */
  backToCompanyInfo() {
    this.router.navigate(["/company-info"]);
  }

  /**
   * 小導覽條-編輯公司資訊
   */
  backToCompanyInfoEdit() {
    this.router.navigate(["/company-info-Edit"]);
  }
}
