import { ChangeDetectionStrategy, Component } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import {
  MAT_DATE_LOCALE,
  MatNativeDateModule,
  provideNativeDateAdapter,
} from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatTimepickerModule } from '@angular/material/timepicker';
import {
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { ConfirmApplyLeaveComponent } from './confirm-apply-leave/confirm-apply-leave.component';
import { TimeFormatService } from '../../../service/time-format.service';
import { TakeLeaveService } from '../../../service/leave/take-leave.service';
import { GeneralComponent } from '../../general/general.component';
import { LeaveApiService } from '../../../service/api/leave-api.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-apply-leave',
  imports: [
    MatDialogModule,
    MatButtonToggleModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatIconModule,
    MatDatepickerModule,
    FormsModule,
    MatNativeDateModule,
    MatTimepickerModule,
    ReactiveFormsModule,
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'zh' },
    provideNativeDateAdapter(),
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './apply-leave.component.html',
  styleUrl: './apply-leave.component.scss',
})
export class ApplyLeaveComponent {
  getValue(controlName: string): AbstractControl | null {
    return this.leaveForm.get(controlName)?.value;
  }

  getControl(controlName: string): any {
    return this.leaveForm.get(controlName);
  }


  //日期日期
  minDate: Date = new Date(new Date().setDate(new Date().getDate() - 60));
  maxDate: Date = new Date(new Date().setDate(new Date().getDate() + 60));

  //時間
  constructor(
    public dialogRef: MatDialogRef<ApplyLeaveComponent>,
    private dialog: MatDialog,
    public time: TimeFormatService,
    private fb: FormBuilder,
    public leave: TakeLeaveService,
    private auth: AuthService
  ) { }

  startTime!: string;
  endTime!: string;

  updateSelectedTime() {
    this.startTime = `${this.getValue('startHour')}:${this.getValue(
      'startMinute'
    )}`;
    this.endTime = `${this.getValue('endHour')}:${this.getValue('endMinute')}`;
  }

  fileErrorHint!: string | null;
  previewUrl!: string | null;
  handleFileUpload(event: Event) {
    const input = event.target as HTMLInputElement;
    //檢查是否有檔案
    if (!input.files || input.files.length === 0) {
      this.fileErrorHint = null;
      this.getControl('certificate')?.setValue(null);
      this.previewUrl = null;
      return;
    }

    //檢查檔案大小
    const file = input.files[0];
    const fileSizeMB = file.size / (1024 * 1024);
    if (fileSizeMB > 0.3) {
      this.fileErrorHint = '檔案大小不可超過 300KB';
      this.getControl('certificate')?.setValue(null);
      this.previewUrl = null;
      return;
    }

    // 檢查檔案格式
    const allowedTypes = ['application/pdf', 'image/jpeg', 'image/png'];
    const allowedExtensions = ['.pdf', '.jpg', '.jpeg', '.png']; //避免副檔名錯誤的沒有被過濾
    const fileExtension = file.name
      .slice(file.name.lastIndexOf('.'))
      .toLowerCase();
    if (
      !allowedTypes.includes(file.type) ||
      !allowedExtensions.includes(fileExtension)
    ) {
      this.fileErrorHint = '僅接受 PDF、JPG、PNG 檔案';
      this.getControl('certificate')?.setValue(null);
      this.previewUrl = null;
      return;
    }


    this.fileErrorHint = null;
    this.getControl('certificate')?.setValue(file);
  }

  // 表單驗證
  leaveForm!: FormGroup;

  //使用者資料
  userInfo!: any;
  //假期天數資訊
  previousAnnualLeave!: number;
  currentAnnualLeave!: number;
  totalAnnualLeave!: number;
  paidSickLeave!: number;

  ngOnInit(): void {
    this.time.generateTimeOptions();
    this.leaveForm = this.fb.group(
      {
        leaveType: ['', Validators.required],
        startDate: [null, Validators.required],
        startHour: ['', Validators.required],
        startMinute: ['', Validators.required],
        endDate: [null, Validators.required],
        endHour: ['', Validators.required],
        endMinute: ['', Validators.required],
        reason: ['', [Validators.required, Validators.maxLength(200)]],
        certificate: [null],
        confirmedTime: [false],
      },
      {
        validators: this.dateTimeRangeValidator,
      }
    );

    this.userInfo = this.auth.getUserInfo();
    this.previousAnnualLeave = this.userInfo.remainingPreviousAnnualLeave;
    this.currentAnnualLeave = this.userInfo.remainingCurrentAnnualLeave;
    this.totalAnnualLeave = this.previousAnnualLeave + this.currentAnnualLeave;
    this.paidSickLeave = this.userInfo.remainingPaidSickLeave;
    //更新假別列表
    this.updateLeaveTypesWithRemainingDays()
  }

  /**在假別列表加入天數 */
  updateLeaveTypesWithRemainingDays() {
    this.leave.leaveTypes = this.leave.leaveTypes.map((type) => {
      if (type.value.toLowerCase() === 'annual') {
        return {
          ...type,
          viewValue: `年假（剩餘 ${this.time.formatDaysToDayHour(this.totalAnnualLeave)} ）`,
          disabled: this.totalAnnualLeave === 0
        };
      }
      if (type.value.toLowerCase() === 'paid sick leave') {
        return {
          ...type,
          viewValue: `有薪病假（剩餘 ${this.time.formatDaysToDayHour(this.paidSickLeave)} ）`,
          disabled: this.paidSickLeave === 0
        };
      }
      return type; // 其他假別不顯示
    });
  }



  /**起訖時間防呆(結束不得早於開始、請假時長不得低於0.5小時、請假時間與現在時間差 >= 2 小時) */
  dateTimeRangeValidator(formGroup: AbstractControl): ValidationErrors | null {
    const startDate = formGroup.get('startDate')?.value;
    const startHour = +formGroup.get('startHour')?.value;
    const startMinute = +formGroup.get('startMinute')?.value;
    const endDate = formGroup.get('endDate')?.value;
    const endHour = +formGroup.get('endHour')?.value;
    const endMinute = +formGroup.get('endMinute')?.value;

    if (
      !startDate ||
      !endDate ||
      isNaN(startHour) ||
      isNaN(startMinute) ||
      isNaN(endHour) ||
      isNaN(endMinute)
    ) {
      return null;
    }


    const start = new Date(startDate);
    start.setHours(startHour, startMinute, 0, 0);

    const end = new Date(endDate);
    end.setHours(endHour, endMinute, 0, 0);

    if (end.getTime() <= start.getTime()) {
      return { dateTimeInvalid: true };
    }

    const diffHours = (end.getTime() - start.getTime()) / (1000 * 60 * 60);
    if (diffHours < 0.5) {
      return { lessThanHalfHour: true };
    }

    return null;
  }

  /**提交前確認及錯誤提示 */
  checkError() {

    if (this.leaveForm.errors?.['dateTimeInvalid']) {
      this.dialog.open(GeneralComponent, {
        data: {
          message: '結束時間不可早於開始時間，請重新選擇',
          title: '時間錯誤',
          showCancel: false,
        },
      });
      return;
    }

    //請假時長低於半小時
    if (this.leaveForm.errors?.['lessThanHalfHour']) {
      this.dialog.open(GeneralComponent, {
        data: {
          message: '請假時長必須大於半小時，請確認時間',
          title: '時間錯誤',
          showCancel: false,
        },
      });
      return;
    }

    if (this.leaveForm.invalid) {
      this.leaveForm.markAllAsTouched();
      this.dialog.open(GeneralComponent, {
        data: {
          message: '尚有欄位未填寫，請補齊後送出',
          title: '提示',
          showCancel: false,
        },
      });
      return;
    }


    //請假時間早於現在太多

    const startTime = new Date(
      `${this.time.formatDateToString(this.getControl('startDate')?.value)}T${this.getControl('startHour')?.value
      }:${this.getControl('startMinute')?.value}:00`
    );
    const endTime = new Date(
      `${this.time.formatDateToString(this.getControl('endDate')?.value)}T${this.getControl('endHour')?.value
      }:${this.getControl('endMinute')?.value}:00`
    );

    const now = new Date();

    const isSameDay =
      startTime.getFullYear() === now.getFullYear() &&
      startTime.getMonth() === now.getMonth() &&
      startTime.getDate() === now.getDate();


    if (isSameDay) {

      const diffNowHours = (startTime.getTime() - now.getTime()) / (1000 * 60 * 60);
      const startTimeStr = this.time.formatTimeToString(startTime);
      const endTimeStr = this.time.formatTimeToString(endTime);

      if (diffNowHours <= -2 && !this.leaveForm.get('confirmedTime')?.value) {
        this.dialog.open(GeneralComponent, {
          data: {
            message: `<br><h5>請假時間：${startTimeStr}~${endTimeStr}</h5>`,
            title: '請再次確認請假日期與時間是否正確',
          },
        }).afterClosed()
          .subscribe(result => {
            if (result) {
              // 使用者選擇「是」後，記錄已確認
              this.leaveForm.get('confirmedTime')?.setValue(true);
              this.checkError();
            } else {
              // 取消就不送出
              return;
            }
          });
        return;
      };
    }
    //錯誤檢查完後呼叫提交
    this.onConfirm();
  }

  /**提交 */
  onConfirm() {
    //傳給後端的日期時間
    const StartDateTimeForBack = this.time.formatSendBackEnd(
      this.getControl('startDate')?.value,
      this.getControl('startHour')?.value,
      this.getControl('startMinute')?.value
    );
    const EndDateTimeForBack = this.time.formatSendBackEnd(
      this.getControl('endDate')?.value,
      this.getControl('endHour')?.value,
      this.getControl('endMinute')?.value
    );

    const applyDateTime = this.time.getNowSendBackEnd();

    const startTime = new Date(
      `${this.time.formatDateToString(this.getControl('startDate')?.value)}T${this.getControl('startHour')?.value
      }:${this.getControl('startMinute')?.value}:00`
    );
    const endTime = new Date(
      `${this.time.formatDateToString(this.getControl('endDate')?.value)}T${this.getControl('endHour')?.value
      }:${this.getControl('endMinute')?.value}:00`
    );


    const dialogData = {
      title: '請假申請資訊',
      leaveType: this.getValue('leaveType'),
      startTime: this.time.formatTimeToString(startTime),
      endTime: this.time.formatTimeToString(endTime),
      certificate: this.getValue('certificate'),
      reason: this.getValue('reason'),
      showCancel: true,
    };

    const confirmRef = this.dialog.open(ConfirmApplyLeaveComponent, {
      data: dialogData,
      disableClose: true,
      panelClass: 'no-animation-dialog',
    });

    const formValue = this.leaveForm.value;
    formValue.fullStartDateTime = StartDateTimeForBack;
    formValue.fullEndDateTime = EndDateTimeForBack;
    formValue.applyDateTime = applyDateTime;

    confirmRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.dialogRef.close(formValue);
        dialogData.certificate=null;
      }
    });
  }

  onCancel() {
    this.dialogRef.close(false);
  }
}
