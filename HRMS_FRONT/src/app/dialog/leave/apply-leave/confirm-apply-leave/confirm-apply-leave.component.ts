import { Component, inject, Inject } from '@angular/core';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { TakeLeaveService } from '../../../../service/leave/take-leave.service';

@Component({
  selector: 'app-confirm-apply-leave',
  imports: [MatDialogModule, MatButtonToggleModule, MatDividerModule],
  templateUrl: './confirm-apply-leave.component.html',
  styleUrl: './confirm-apply-leave.component.scss',
})
export class ConfirmApplyLeaveComponent {
  constructor(
    private _snackBar :MatSnackBar,
    public dialogRef: MatDialogRef<ConfirmApplyLeaveComponent>,
    private dialog: MatDialog,
    public leave: TakeLeaveService,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      title?: string;
      leaveType: string;
      startTime: string;
      endTime: string;
      certificate?: File;
      reason: string;
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
  }

  onConfirm() {
    this.dialogRef.close(true);
  }

  ngOnDestroy(): void {
    this.leave.clearPreviewUrl();
  }
}
