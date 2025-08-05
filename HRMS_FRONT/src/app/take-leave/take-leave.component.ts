import {
  ChangeDetectorRef,
  Component,
  inject,
  Inject,
  ViewChild,
} from '@angular/core';
import { Router } from '@angular/router';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import {
  MatPaginator,
  MatPaginatorIntl,
  MatPaginatorModule,
} from '@angular/material/paginator';
import { LeaveRequestBody, takeLeaveData } from '../models/take-leave-model';
import { MatIconModule } from '@angular/material/icon';
import { MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TakeLeaveService } from '../service/leave/take-leave.service';
import { TimeFormatService } from '../service/time-format.service';
import { CommonModule } from '@angular/common';
import { ApplyLeaveComponent } from '../dialog/leave/apply-leave/apply-leave.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { GeneralComponent } from '../dialog/general/general.component';
import { LeaveTableService } from '../service/leave/leave-table.service';
import { AuthService } from '../services/auth.service';
import { LeaveApiService } from '../service/api/leave-api.service';
import { take } from 'rxjs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MAT_DATE_LOCALE, provideNativeDateAdapter } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { FormGroup, FormControl, ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { MatSort, MatSortModule } from '@angular/material/sort';

@Component({
  selector: 'app-take-leave',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatSelectModule,
    ReactiveFormsModule,
    MatSortModule
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'zh' },
    provideNativeDateAdapter(),
  ],
  templateUrl: './take-leave.component.html',
  styleUrl: './take-leave.component.scss',
})
export class TakeLeaveComponent {
  baseColumns: string[] = [
    'applyDateTime',
    'startTime',
    'endTime',
    'leaveType',
    'status',
    'detail',]
  displayedColumns: string[] = [...this.baseColumns];
  dataSource!: MatTableDataSource<takeLeaveData>;
  startDate!: string;
  hasSupplement: boolean = false;
  hasReview: boolean = false;
  constructor(
    private router: Router,
    private _snackBar: MatSnackBar,
    private dialog: MatDialog,
    public takeLeave: TakeLeaveService,
    public time: TimeFormatService,
    public leaveTable: LeaveTableService,
    private auth: AuthService,
    private leaveApi: LeaveApiService,
  ) { }

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;


  //使用者資訊
  userInfo: any;
  userId: number = 0;
  userName: string = '';
  ngOnInit() {
    this.leaveTable.createPaginator();

    this.refreshTableData()
    //如果有登入的話抓使用者資料
    if (this.auth.isLoggedIn()) {
      this.userInfo = this.auth.getUserInfo();
      this.userId = this.userInfo.id;
      this.userName = this.userInfo.name;
    }
  }



  /**更新假單列表 */
  isLoading = false;
  refreshTableData(startTime?: string, endTime?: string) {
    this.isLoading = true;
    this.takeLeave.fetchLeaves(startTime, endTime);
    this.takeLeave
      .getLeaves$()
      .subscribe((leaves) => {
        this.isLoading = false;
        this.dataSource = new MatTableDataSource<takeLeaveData>(leaves);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.hasSupplement = this.dataSource.data.some(item => item.status.toLowerCase() === 'pending supplement');
        this.hasReview = this.dataSource.data.some(item => item.status.toLowerCase() === 'pending review');

        if (this.hasSupplement && this.hasReview) {
          this.displayedColumns = [...this.baseColumns, 'cancel', 'supplement'];
        } else if (this.hasSupplement) {
          this.displayedColumns = [...this.baseColumns, 'supplement'];
        } else if (this.hasReview) {
          this.displayedColumns = [...this.baseColumns, 'cancel'];
        } else {
          this.displayedColumns = [...this.baseColumns];
        }
      });

  }

  /**假單搜尋 */
  dateSearch = new FormGroup({
    startDate: new FormControl(),
    endDate: new FormControl(),
  });


  minEndDate: Date | null = null;
  /**日期防呆 */
  onStartDateChange() {
    const startDate = this.dateSearch.get('startDate')?.value;
    if (startDate) {
      this.minEndDate = startDate;

      const endDate = this.dateSearch.get('endDate')?.value;
      // 若結束日期小於開始日期，自動清空
      if (endDate && endDate < startDate) {
        this.dateSearch.get('endDate')?.reset();
      }
    }
  }

  /**取得搜尋日期 */
  onSearch() {
    const startDate = this.time.formatDateToString(this.dateSearch.get('startDate')?.value);
    const endDate = this.time.formatDateToString(this.dateSearch.get('endDate')?.value);
    this.refreshTableData(startDate, endDate)
  }


  /**組串api的資料 */
  async createLeaveRequestBody(data: {
    fullStartDateTime: string;
    fullEndDateTime: string;
    leaveType: string;
    certificate?: File | null;
    reason: string;
    certificateFileType: string
  }): Promise<LeaveRequestBody> {
    let base64Certificate = '';
    let fileType = ''
    if (data.certificate) {
      try {
        base64Certificate = (await this.takeLeave.convertFileToBase64(data.certificate)).base64;
        fileType = (await this.takeLeave.convertFileToBase64(data.certificate)).fileType;
      } catch (error) {
        console.error('轉換證明文件失敗：', error);
        this._snackBar.open(
          '轉換證明文件失敗，請確認檔案格式是否正確',
          '關閉',
          { duration: 3000 }
        );
      }
    }

    return {
      startTime: data.fullStartDateTime,
      endTime: data.fullEndDateTime,
      leaveType: data.leaveType,
      certificate: base64Certificate,
      reason: data.reason,
      certificateFileType: fileType
    };
  }

  /**開啟請假申請dialog*/
  applyLeave() {
    const dialogRef = this.dialog.open(ApplyLeaveComponent, {
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.createLeaveRequestBody(result).then((requestBody) => {
          //傳入表單的資料
          const newLeave: takeLeaveData = {
            employeeId: this.userId,
            employeeName: this.userInfo.name,
            leaveType: result.leaveType,
            startTime: result.fullStartDateTime,
            endTime: result.fullEndDateTime,
            reason: result.reason,
            certificate: result.certificate,
            applyDateTime: new Date(),
            status: 'pending review',
            approved: false,
          };

          //收到資料後串api
          this.leaveApi.applyLeave(requestBody).subscribe({
            next: (res) => {
              if (res.code === 200) {
                this._snackBar.open('請假申請成功', '關閉', {
                  duration: 3000,
                });
                this.takeLeave.addLeave(newLeave);
              } else {
                this._snackBar.open('請假申請失敗，請確認填寫資料', '關閉', {
                  duration: 3000,
                });
              }

              this.refreshTableData();
            },
            error: (err) => {
              console.error('請假申請失敗:', err);
              this._snackBar.open('請假申請失敗，請稍後再試', '關閉', {
                duration: 2000,
              });
            },
          });
        });
      }
    });
  }

  /**開啟補件dialog */
  openSupplementDialog(element: any) {
    this.takeLeave
      .openSupplement(element)
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.refreshTableData();
        }
      });
  }

  /**取消假單 */
  cancelLeave(element: any) {
    const confirm = this.dialog.open(GeneralComponent, {
      data: {
        message: '確定要取消此筆假單嗎?',
        title: '確認',
        showCancel: true,
      },
    });
    confirm.afterClosed().subscribe((result) => {
      if (result) {
        this.leaveApi.cancelLeave(element.leaveId).subscribe({
          next: (res) => {
            this._snackBar.open('已取消假單', '關閉', { duration: 2000 });
            this.refreshTableData();
          },
          error: (err) => {
            console.error('取消假單失敗', err);
            this._snackBar.open('取消失敗，請稍後再試', '關閉', { duration: 2000 });
          },
        });
      }
    });
  }

  //導覽條路徑
  backToHome() {
    this.router.navigate(["/home"]);
  }

  /**
   * 小導覽條-薪資管理
   */
  backToTakeLeave() {
    this.router.navigate(["/take-leave"]);
  }

}
