import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {
  MAT_DATE_FORMATS,
  MAT_DATE_LOCALE,
  provideNativeDateAdapter,
} from '@angular/material/core';
import { FormsModule } from '@angular/forms';
import { MatNativeDateModule } from '@angular/material/core';
import { ConfirmClockRecordComponent } from './confirm-clock-record/confirm-clock-record.component';
import { TimeFormatService } from '../../service/time-format.service';

@Component({
  standalone: true,
  selector: 'app-add-clock-record',
  templateUrl: './add-clock-record.component.html',
  styleUrl: './add-clock-record.component.scss',
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
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'zh' },
    provideNativeDateAdapter(),
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddClockRecordComponent {
  //選擇打卡類型
  selectedClockType!: string;

  //選擇日期
  selectedDate!: Date;
  today: Date = new Date();
  formattedSelectedDate!: string;

  //選擇時間
  selectedHour: string = '';
  selectedMinute: string = '';
  selectedTime: string = '';

  minDate: Date = new Date(new Date().setDate(new Date().getDate() - 60));
  constructor(
    public dialogRef: MatDialogRef<AddClockRecordComponent>,
    private dialog: MatDialog,
    public time: TimeFormatService,
    @Inject(MAT_DIALOG_DATA)
    public data: { id?: number; type?: string; date?: string }
  ) { }

  ngOnInit() {
    this.time.generateTimeOptions();
    this.selectedClockType = this.data?.type || '';
    if (this.data?.date) {
      const [year, month, day] = this.data.date.split('/').map(Number);
      this.selectedDate = new Date(year, month - 1, day);
    }
  }



  updateSelectedTime() {
    this.selectedTime = `${this.selectedHour}:${this.selectedMinute}`;
  }

  onConfirm() {
    //傳給後端的日期時間
    const fullDateTime = this.time.formatSendBackEnd(this.selectedDate, this.selectedHour, this.selectedMinute);
    this.formattedSelectedDate = this.time.formatDateToString(this.selectedDate);
    this.dialogRef.close(true);
    this.dialog.open(ConfirmClockRecordComponent, {
      data: {
        title: '補卡資訊',
        clockType: this.selectedClockType,
        clockDate: this.formattedSelectedDate,
        clockTime: this.selectedTime,
      },
      disableClose: true,
    });
  }
  types = [
    { value: 'clock-in', viewValue: '上班' },
    { value: 'clock-out', viewValue: '下班' },
  ];


}
