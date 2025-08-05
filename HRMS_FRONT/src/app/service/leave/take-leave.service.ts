import { takeLeaveData } from './../../models/take-leave-model';
import { inject, Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { LeaveDetailComponent } from '../../dialog/leave/leave-detail/leave-detail.component';
import { SupplementCertificateComponent } from '../../dialog/leave/supplement-certificate/supplement-certificate.component';
import { GeneralComponent } from '../../dialog/general/general.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BehaviorSubject, Observable, Subject, tap } from 'rxjs';
import { LeaveApiService } from '../api/leave-api.service';
import { TimeFormatService } from '../time-format.service';
import { AuthService } from '../../services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class TakeLeaveService {
  constructor(
    private dialog: MatDialog,
    private _snackBar: MatSnackBar,
    private leaveApi: LeaveApiService,
    private time: TimeFormatService,
    private auth: AuthService
  ) { }

  //假別
  leaveTypes = [
    { value: 'annual', viewValue: '年假', disabled: false },
    { value: 'paid sick leave', viewValue: '有薪病假', disabled: false },
    { value: 'sick', viewValue: '病假', disabled: false },
    { value: 'personal', viewValue: '事假', disabled: false },
    { value: 'marriage', viewValue: '婚假', disabled: false },
    { value: 'bereavement', viewValue: '喪假', disabled: false },
    { value: 'maternity', viewValue: '產假', disabled: false },
    { value: 'paternity', viewValue: '陪產假', disabled: false },
    { value: 'official', viewValue: '公假', disabled: false },
    { value: 'menstrual', viewValue: '生理假', disabled: false },
    { value: 'other', viewValue: '其他', disabled: false },
  ];

  formatLeaveType(type: string): string {
    //物件表先以全小寫定義
    const leaveTypeMap: { [key: string]: string } = {
      annual: '年假',
      'paid sick leave': '有薪病假',
      sick: '病假',
      personal: '事假',
      marriage: '婚假',
      bereavement: '喪假',
      maternity: '產假',
      paternity: '陪產假',
      official: '公假',
      menstrual: '生理假',
      other: '其他',
    };

    //將收到的資料轉為小寫.toLowerCase 再以小寫去對照物件表
    const lowerType = type.toLowerCase();
    return leaveTypeMap[lowerType] || '其他';
  }

  //請假狀態
  leavestatus = [
    { value: 'approved', viewValue: '已通過' },
    { value: 'rejected', viewValue: '未通過' },
    { value: 'sick', viewValue: '病假' },
    { value: 'pending review', viewValue: '審查中' },
    { value: 'pending supplement', viewValue: '待補件' },
    { value: 'cancel application', viewValue: '取消申請' },
  ];


  formatStatus(status: string) {
    const statusMap: { [key: string]: string } = {
      approved: '已通過',
      rejected: '已駁回',
      'pending review': '審查中',
      'pending supplement': '待補件',
      'cancel application': '已取消',
    };

    const lowerType = status.toLowerCase();
    return statusMap[lowerType] || '其他';
  }

  //駁回理由
  rejectOptions = [
    { value: 'Missing certificate', viewValue: '缺少證明文件' },
    { value: 'Invalid certificate', viewValue: '無效的請假證明' },
    { value: 'Application date error', viewValue: '申請日期有誤' },
    { value: 'Leave type error', viewValue: '假別有誤' },
    { value: 'Reason is not match type', viewValue: '請假理由與假別不符' },
    { value: 'Insufficient days remaining', viewValue: '剩餘假期不足' },
    { value: 'Advance application required', viewValue: '需要提前申請' },
    { value: 'Duplicate leave request', viewValue: '假單重複申請' },
    { value: 'Leave period conflicts with schedule', viewValue: '請假期間與排程衝突' },
    { value: 'Other', viewValue: '其他' },
  ]

  formatRejectOption(reason: string) {
    const rejectOptionMap: { [key: string]: string } = {
      'missing certificate': '缺少證明文件',
      'invalid certificate': '無效的請假證明',
      'application date error': '申請日期有誤',
      'leave type error': '假別有誤',
      'reason is not match type': '請假理由與假別不符',
      'insufficient days remaining':'剩餘假期不足',
     'advance application required':'需要提前申請',
      'duplicate leave request':'假單重複申請',
      'leave period conflicts with schedule':'請假期間與排程衝突',
      'other':'其他'
    };

    const lowerType = reason.toLowerCase();
    return rejectOptionMap[lowerType] || '其他';
  }


  formatRoles(role:string){
    const rolesMap: { [key: string]: string } = {
      'ga manager': '一般部門主管',
      'ac manager': '會計部門主管',
      'hr manager': '人資部門主管',
      'hr employee':'人資部門員工',
    };

    const lowerType = role.toLowerCase();
    return rolesMap[lowerType] || '其他';
  }

  /**用狀態更換不同class */
  getStatusClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'approved':
        return 'approved';
      case 'rejected':
        return 'rejected';
      case 'pending review':
        return 'pending';
      case 'pending supplement':
        return 'supplement';
      case 'cancel application':
        return 'cancelled';
      default:
        return '';
    }
  }


  private previewUrlMap = new WeakMap<File, string>();
  private previewFiles: Set<File> = new Set();
  /**證明文件連結 */
  generatePreviewUrl(file: File | null): string | null {
    if (!file) return null;

    if (!this.previewUrlMap.has(file)) {
      const url = URL.createObjectURL(file);
      this.previewUrlMap.set(file, url);
      this.previewFiles.add(file);
    }
    return this.previewUrlMap.get(file) || null;
  }


  /** 將後端回傳的資料組合成url */
  previewUrlMapFromBase64 = new Map<string, string>();
  generatePreviewUrlByBase64(base64: string | null, fileType: string): string | null {
    if (!base64) return null;

    if (!this.previewUrlMapFromBase64.has(base64)) {
      // 把 base64 轉成二進位資料
      const byteCharacters = atob(base64);
      const byteArray = new Uint8Array(byteCharacters.length);
      for (let i = 0; i < byteCharacters.length; i++) {
        byteArray[i] = byteCharacters.charCodeAt(i);
      }

      const blob = new Blob([byteArray], { type: fileType });
      const url = URL.createObjectURL(blob);

      this.previewUrlMapFromBase64.set(base64, url);
    }
    return this.previewUrlMapFromBase64.get(base64) || null;
  }



  clearPreviewUrl() {
    this.previewFiles.forEach((file) => {
      const url = this.previewUrlMap.get(file);
      if (url) {
        URL.revokeObjectURL(url);
      }
    });
    this.previewUrlMap = new WeakMap();
    this.previewFiles.clear();
  }

  /**轉換證明文件成base64編碼 */
  convertFileToBase64(file: File): Promise<{ base64: string; fileType: string }> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        const base64String = (reader.result as string).split(',')[1];
        resolve({ base64: base64String, fileType: file.type })
      };
      reader.onerror = (error) => {
        reject(error);
      };
      reader.readAsDataURL(file);
    });
  }

  /**顯示假單詳情 */
  openDetail(element: any) {
    this.dialog.open(LeaveDetailComponent, {
      data: { element: element },
      disableClose: false,
    });
  }

  /**顯示補件畫面 */
  openSupplement(
    element: any
  ): MatDialogRef<SupplementCertificateComponent, SupplementResult> {
    return this.dialog.open(SupplementCertificateComponent, {
      data: element,
      disableClose: true,
    });
  }

  leaveList$ = new BehaviorSubject<takeLeaveData[]>([]);
  leaveUpdated$ = new Subject<void>();

  /** 更新假單資訊*/
  fetchLeaves(startTime?: string, endTime?: string) {
    const today = new Date();
    // 設定查詢範圍為今天前後30天
    const defaultStart = this.time.formatDateToString(
      new Date(today.getFullYear(), today.getMonth() - 1, today.getDate())
    );
    const defaultEnd = this.time.formatDateToString(
      new Date(today.getFullYear(), today.getMonth() + 1, today.getDate())
    );


    const body = {
      employeeId: this.auth.getUserInfo()?.id,
      startTime: startTime || defaultStart,
      endTime: endTime || defaultEnd,
    };
    this.leaveApi.fetchLeaves(body).subscribe({
      next: (res) => {
        const applications = res.leaveApplicationList ?? [];
        const records = (res.leaveRecordList ?? []).filter((record: any) => {
          return !record.submitUp;
        });
        const combinedData = [...applications, ...records];
        const statusOrder = {
          'pending review': 1,
          'pending supplement': 2,
          'approved': 3,
          'rejected': 4,
          'cancel application': 5,
        };

        combinedData.sort((a: { status?: string }, b: { status?: string }) => {
          const sa = statusOrder[(a.status || '').toLowerCase() as keyof typeof statusOrder] ?? 99;
          const sb = statusOrder[(b.status || '').toLowerCase() as keyof typeof statusOrder] ?? 99;
          return sa - sb;
        });

        this.leaveList$.next(combinedData);
      },
      error: (err) => {
        console.error('取得假單資料失敗', err);
      },
    });
  }
  fetchLeavesForFlow(startTime?: string, endTime?: string): Observable<any> {
    const today = new Date();
    const defaultStart = this.time.formatDateToString(
      new Date(today.getFullYear(), today.getMonth() - 1, today.getDate())
    );
    const defaultEnd = this.time.formatDateToString(
      new Date(today.getFullYear(), today.getMonth() + 1, today.getDate())
    );

    const body = {
      employeeId: this.auth.getUserInfo()?.id,
      startTime: startTime || defaultStart,
      endTime: endTime || defaultEnd,
    };

    return this.leaveApi.fetchLeaves(body).pipe(
      tap((res) => {
        const applications = res.leaveApplicationList ?? [];
        const records = (res.leaveRecordList ?? []).filter(
          (record: any) => !record.submitUp
        );
        const combinedData = [...applications, ...records];

        this.leaveList$.next(combinedData);
      })
    );
  }


  /**取得請假清單 */
  getLeaves$() {
    return this.leaveList$.asObservable();
  }

  /**新增假單 */
  addLeave(newLeave: takeLeaveData) {
    const current = this.leaveList$.getValue();
    this.leaveList$.next([...current, newLeave]);
  }

  /**取得進行中的假單 */
  getRecentLeave() {
    const now = new Date();
    const oneWeekAgo = new Date();
    oneWeekAgo.setDate(now.getDate() - 7);

    const leaveList = this.leaveList$.getValue() ?? [];

    // 優先抓「正在審核中」的
    const reviewing = leaveList
      .filter((leave) =>
        ['pending review', 'pending supplement'].includes(
          leave.status?.toLowerCase() ?? ''
        )
      )
      .sort(
        (a, b) =>
          new Date(b.applyDateTime ?? 0).getTime() -
          new Date(a.applyDateTime ?? 0).getTime()
      );

    if (reviewing.length > 0) return reviewing[0];

    // 找一周內「已通過或已駁回」的假單
    const recentDecide = leaveList
      .filter(
        (leave) =>
          ['approved', 'rejected'].includes(leave.status?.toLowerCase() ?? '') &&
          leave.approvedDateTime &&
          new Date(leave.approvedDateTime) >= oneWeekAgo
      )
      .sort(
        (a, b) =>
          new Date(b.approvedDateTime ?? 0).getTime() -
          new Date(a.approvedDateTime ?? 0).getTime()
      );
    return recentDecide[0] ?? null;
  }

}

export type SupplementResult =
  | { status: 'success'; certificate: File }
  | { status: 'cancel' }
  | undefined;
