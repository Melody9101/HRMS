import { Component, inject, Inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { TakeLeaveService } from '../../../service/leave/take-leave.service';
import { TimeFormatService } from '../../../service/time-format.service';
import { CommonModule } from '@angular/common';
import { MatSnackBar } from '@angular/material/snack-bar';
import { GeneralComponent } from '../../general/general.component';
import { LeaveApiService } from '../../../service/api/leave-api.service';

@Component({
  selector: 'app-supplement-certificate',
  imports: [MatDividerModule, CommonModule],
  templateUrl: './supplement-certificate.component.html',
  styleUrl: './supplement-certificate.component.scss',
})
export class SupplementCertificateComponent {
  displayedColumns: string[] = ['label', 'content'];

  constructor(
    @Inject(MAT_DIALOG_DATA) public element: any,
    public dialogRef: MatDialogRef<SupplementCertificateComponent>,
    private dialog: MatDialog,
    private _snackBar: MatSnackBar,
    public takeLeave: TakeLeaveService,
    public time: TimeFormatService,
    private leaveApi: LeaveApiService
  ) { }


  certificate!: File | null;
  fileErrorHint!: string | null;
  resetFile() {
    this.certificate = null;
    this.takeLeave.clearPreviewUrl();
  }

  handleFileUpload(event: Event) {
    const input = event.target as HTMLInputElement;

    //檢查是否有檔案
    if (!input.files || input.files.length === 0) {
      this.fileErrorHint = null;
      this.resetFile();
      return;
    }

    //檢查檔案大小
    const file = input.files[0];
    const fileSizeMB = file.size / (1024 * 1024);
    if (fileSizeMB > 2) {
      this.fileErrorHint = '檔案大小不可超過 2MB';
      this.resetFile();
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
      this.resetFile();
      return;
    }

    //通過驗證
    this.fileErrorHint = null;
    this.certificate = file;
  }

  base64Certificate!:string;
  fileType!:string;

  onCancel() {
    this.dialogRef.close({ status: 'cancel' });
    this._snackBar.open('補件已取消', '確定', { duration: 1000 });
  }

  async onConfirm(element: any) {

    if (!this.certificate) {
      this.dialog.open(GeneralComponent, {
        data: {
          message: '請上傳證明文件',
          title: '提示',
          showCancel: false,
        },
        disableClose: true,
      });
      return;
    } else {
      try {
        this.base64Certificate = (await this.takeLeave.convertFileToBase64(this.certificate)).base64;
        this.fileType = (await this.takeLeave.convertFileToBase64(this.certificate)).fileType;
      } catch (error) {
        console.error('轉換證明文件失敗：', error);
        this._snackBar.open(
          '轉換證明文件失敗，請確認檔案格式是否正確',
          '關閉',
          { duration: 3000 }
        );
      }
    }

    const body =
      { leaveId: element.leaveId, certificate: this.base64Certificate, certificateFileType: this.fileType }

    this.leaveApi.updateLeaveApplicationCertificate(body).subscribe({
      next: (res: any) => {
        if (res.code !== 200) {
          this._snackBar.open('系統異常，補件失敗', '關閉', {
            duration: 2000,
          });
          return;
        }
        this.dialogRef.close(true);
         this._snackBar.open('補件完成，狀態已更新為審查中', '關閉', {
            duration: 2000,
          });
      },
      error: (err: any) => {
        this._snackBar.open('補件失敗，請稍後再試', '關閉', {
          duration: 2000,
        });
        console.error('補件失敗', err);
        this.dialogRef.close(false);
      },
    })
  }

  ngOnDestroy(): void {
    this.takeLeave.clearPreviewUrl();
  }
}
