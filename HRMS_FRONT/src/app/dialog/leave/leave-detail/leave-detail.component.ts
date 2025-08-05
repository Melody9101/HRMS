import { MatIconModule } from '@angular/material/icon';
import { Component, Inject, signal } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDividerModule } from '@angular/material/divider';
import { TakeLeaveService } from '../../../service/leave/take-leave.service';
import { TimeFormatService } from '../../../service/time-format.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { LeaveApiService } from '../../../service/api/leave-api.service';
import { GeneralComponent } from '../../general/general.component';
import { RejectReasonComponent } from '../reject-reason/reject-reason.component';

@Component({
  selector: 'app-leave-detail',
  imports: [MatIconModule, MatDividerModule, CommonModule, MatExpansionModule, MatIconModule],
  templateUrl: './leave-detail.component.html',
  styleUrl: './leave-detail.component.scss',
})
export class LeaveDetailComponent {
  displayedColumns: string[] = ['label', 'content'];
  dataSource: any;
  element!: any;
  isReview: boolean = false;
  certificateUrl: string | null = null;
  currentReviewer: string = '';
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { element: any; isReview?: boolean; certificateUrl?: string },
    public dialogRef: MatDialogRef<LeaveDetailComponent>,
    private dialog: MatDialog,
    public takeLeave: TakeLeaveService,
    public time: TimeFormatService,
    private _snackBar: MatSnackBar,
    private leaveApi: LeaveApiService
  ) {
    this.element = data.element;
    this.currentReviewer = this.element.approvalPendingRole;
    if (data.isReview === true) {
      this.isReview = true;
    }

    if (this.data.element.certificate) {
      this.certificateUrl = this.takeLeave.generatePreviewUrlByBase64(
        this.data.element.certificate,
        this.data.element.certificateFileType
      );
    }
  }

  ngOnInit() {
    this.initFlow();
  }

  //串接api
  reviewApi(body: { leaveId: any; status: string; rejectionReason?: string; }) {
    this.leaveApi.approveLeave(body).subscribe({
      next: (res: any) => {
        if (res.code !== 200) {
          this._snackBar.open('系統異常審核失敗', '關閉', {
            duration: 2000,
          });
          return;
        }
        this.closeDialog(this.element);
      },
      error: (err: any) => {
        this._snackBar.open('審核失敗，請稍後再試', '關閉', {
          duration: 2000,
        });
      },
    })
  }

  /**核准 */
  approveLeave(element: any) {
    const confirmDialog = this.dialog.open(GeneralComponent, {
      data: {
        message: `此動作送出後將無法復原，請再次確認。`,
        title: '確定要核准這張假單嗎？',
        showCancel: true,
      },
      disableClose: true,
    });

    confirmDialog.afterClosed().subscribe((result) => {
      if (result === true) {
        const body = {
          leaveId: element.leaveId,
          status: 'approved',
        }
        this.reviewApi(body);
        this.dialog.closeAll();
        this._snackBar.open('假單已核准', '關閉', {
          duration: 2000,
        });
      }
    })
  }

  /**駁回 */
  rejectLeave(element: any) {
    const rejectReasonDialog = this.dialog.open(RejectReasonComponent, {
      data: {
        title: '駁回原因',
        options: this.takeLeave.rejectOptions,
        element: element,
        switchStatus: 'rejected',
        parentDialog: this.dialogRef,
      },
      disableClose: true,
    });

    rejectReasonDialog.afterClosed().subscribe((res) => {
      const rejectReason = res.reason.value;
      if (res.result === true) {
        const confirmDialog = this.dialog.open(GeneralComponent, {
          data: {
            message: `此動作送出後將無法復原，請再次確認。`,
            title: `駁回理由為${res.reason.viewValue}`,
            showCancel: true,
          },
          disableClose: true,
        });

        confirmDialog.afterClosed().subscribe((result) => {
          if (result === true) {
            const body = {
              leaveId: element.leaveId,
              status: 'Rejected',
              rejectionReason: rejectReason
            }

            this.reviewApi(body);
            this.dialog.closeAll();
            this._snackBar.open('假單已駁回', '關閉', {
              duration: 2000,
            });
          }
        })
      }
    })
  }



  /**補件 */
  supplementLeave(element: any) {
    const confirmDialog = this.dialog.open(GeneralComponent, {
      data: {
        message: `送出後，假單會退回至申請人修改，請再次確認。`,
        title: '確定要請申請人補件嗎？',
        showCancel: true,
      },
      disableClose: true,
    });

    confirmDialog.afterClosed().subscribe((result) => {
      if (result === true) {
        const body = {
          leaveId: element.leaveId,
          status: 'pending supplement',
        }
        this.reviewApi(body);
        this.dialog.closeAll();
        this._snackBar.open('假單補件已回傳', '關閉', {
          duration: 2000,
        });
      }
    })
  }


  closeDialog(element: any) {
    this.dialogRef.close({ element: element });
  }

  //審核詳情
  readonly panelOpenState = signal(false);

  //審核節點圖
  stages = [
    'submitted',
    'ac manager',
    'ga manager',
    'hr employee',
    'hr manager'
  ];

  status: Array<'done' | 'now' | 'next'> = [];
  displayStages: string[] = [];

  stageMap: { [key: string]: string } = {
    'submitted': "提交申請",
    'ac manager': "部門主管",
    'ga manager': "部門主管",
    'hr employee': "人資員工",
    'hr manager': "人資主管"
  }

  /**取得節點圖當前狀態 */
  initFlow() {
    const currentIndex = this.stages
      .map(stage => stage.toLowerCase())
      .indexOf(this.currentReviewer.toLowerCase());

    this.status = this.stages.map((stage, i) => {
      if (i < currentIndex) {
        return 'done';
      } else if (i === currentIndex) {
        return 'now';
      } else {
        return 'next';
      }
    });
    console.log(this.status);

    const seen = new Set<string>();
    const newStatus: ('done' | 'now' | 'next')[] = [];
    this.displayStages = this.stages
      .map((stage, i) => {
        const stageName = this.stageMap[stage.toLowerCase()];
        if (!seen.has(stageName)) {
          seen.add(stageName);
          newStatus.push(this.status[i]);
          return stageName;
        }
        return null; // 標記丟掉
      })
      .filter(stageName => stageName !== null);
    this.status = newStatus;
    console.log("新狀態", this.status);

    console.log(this.displayStages);

  }
}

