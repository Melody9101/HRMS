import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import {
  MatPaginator,
  MatPaginatorIntl,
  MatPaginatorModule,
} from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { takeLeaveData } from '../models/take-leave-model';
import { TakeLeaveService } from '../service/leave/take-leave.service';
import { TimeFormatService } from '../service/time-format.service';
import { LeaveTableService } from '../service/leave/leave-table.service';
import { Subject, takeUntil } from 'rxjs';
import { LeaveDetailComponent } from '../dialog/leave/leave-detail/leave-detail.component';
import { LeaveApiService } from '../service/api/leave-api.service';
import { Router } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-leave-review',
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatIconModule,
    MatTooltipModule,
  ],
  templateUrl: './leave-review.component.html',
  styleUrl: './leave-review.component.scss',
})
export class LeaveReviewComponent {
  displayedColumns: string[] = [
    'applierName',
    'applyDateTime',
    'startTime',
    'endTime',
    'leaveType',
    'status',
    'detail',
  ];
  dataSource!: MatTableDataSource<takeLeaveData>;
  constructor(
    private router: Router,
    private dialog: MatDialog,
    public takeLeave: TakeLeaveService,
    public time: TimeFormatService,
    public leaveTable: LeaveTableService,
    private _snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef,
    private leaveApi: LeaveApiService
  ) { }
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  // 宣告一個 Subject 作為結束通知器
  private destroy$ = new Subject<void>();
  ngOnInit() {
    this.getPendingLeaves();
    this.dataSource = new MatTableDataSource<takeLeaveData>([]);
    this.leaveTable.createPaginator();
    this.takeLeave.leaveUpdated$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.refreshTableData();
      });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }


  pendingLeaves: any[] = [];
  /**抓取待審假單 */
  getPendingLeaves() {
    this.leaveApi.checkLeaveApplication().subscribe({
      next: (res) => {
        this.pendingLeaves = (res.leaveApplicationList??[]).filter((pending:any)=>{
          return pending.status.toLowerCase()==='pending review';
        });
        this.refreshTableData();
      },
      error: (err) => {
        console.error('取得待審核假單失敗:', err);
        this._snackBar.open('取得待審核假單失敗，請稍後再試', '關閉', {
          duration: 2000,
        });
      },
    })
  }
  
  /**更新假單列表 */
  refreshTableData() {    
    this.dataSource.data = [...this.pendingLeaves];
  }

  /**顯示審核假單 */
  openReview(element: any) {
    const dialogRef = this.dialog.open(LeaveDetailComponent, {
      data: { element: element, isReview: true },
      disableClose: true,
    });
    dialogRef.afterClosed().subscribe((result) => {
      //關閉dialog後刷新資料
      this.getPendingLeaves();
    });
  }

  ngOnDestroy() {
    // 當元件要被銷毀時，發出通知，讓上面的訂閱自動取消
    this.destroy$.next();
    this.destroy$.complete();
  }


  //導覽條路徑
  backToHome() {
    this.router.navigate(["/home"]);
  }

  backToTakeLeave() {
    this.router.navigate(["/leave-review"]);
  }
}
