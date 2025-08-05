import { Component, Inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { AddClockRecordComponent } from '../add-clock-record/add-clock-record.component';
@Component({
  selector: 'app-clock-notify',
  imports: [MatDividerModule],
  templateUrl: './clock-notify.component.html',
  styleUrl: './clock-notify.component.scss',
})
export class ClockNotifyComponent {
  constructor(
    public dialogRef: MatDialogRef<ClockNotifyComponent>,
    private dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: any = { notifications: [], date: '' },
  ) {}


  openClockRecordWithData(record: any) {
    this.dialog
      .open(AddClockRecordComponent, {
        data: {
          id: record.id,
          type: record.type,
          date: record.date,
          time: record.time,
          title: record.title,
        },
        disableClose: false,
      })
  }

  /**打卡詳情格式化顯示 */
  formatExceptionReason(reason: string) {
    const exceptionReasonsMap: { [key: string]: string } = {
      "missed clock in": '上班|未打卡',
      "missed clock out": '下班|未打下班卡',
      "late arrival": '上班|遲到',
      "leaving early": '下班|早退',
    };

    const lowerType = reason.toLowerCase();
    return exceptionReasonsMap[lowerType] || '其他';
  }

}
