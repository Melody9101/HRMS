import { Component, inject, Inject } from '@angular/core';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-confirm-clock-record',
  imports: [MatDialogModule, MatButtonToggleModule],
  templateUrl: './confirm-clock-record.component.html',
  styleUrl: './confirm-clock-record.component.scss',
})
export class ConfirmClockRecordComponent {
  constructor(
    private _snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<ConfirmClockRecordComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      title?: string;
      clockType: string;
      clockDate: string;
      clockTime: string;
      showCancel?: boolean;
    }
  ) {
    // 把 data.showCancel 傳進來
    if (data.showCancel === false) {
      this.showCancel = false;
    }
  }
  showCancel: boolean = true;

  onCancel() {
    this.dialogRef.close(false);
    this._snackBar.open('補卡申請已取消', '確定', { duration: 1000 });
  }

  onConfirm() {
    this.dialogRef.close(true);
    this._snackBar.open('補卡申請已送出', '確定', { duration: 1000 });
  }
}
