import { ChangeDetectorRef, Component, HostListener, inject } from '@angular/core';
import { FunctionCardComponent } from '../shared/function-card/function-card.component';
import { LeaveFlowComponent } from '../shared/leave-flow/leave-flow.component';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { GeneralComponent } from '../dialog/general/general.component';
import { AddClockRecordComponent } from '../dialog/add-clock-record/add-clock-record.component';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { ClockNotifyComponent } from '../dialog/clock-notify/clock-notify.component';
import { TakeLeaveService } from '../service/leave/take-leave.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../services/auth.service';
import { Role } from '../models/role.enum';
import { ClockApiService } from '../service/api/clock-api.service';
import { TimeFormatService } from '../service/time-format.service';
import { LeaveApiService } from '../service/api/leave-api.service';

@Component({
  standalone: true,
  selector: 'app-home',
  imports: [
    FunctionCardComponent,
    LeaveFlowComponent,
    MatIconModule,
    MatBadgeModule,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  constructor(
    private _snackBar: MatSnackBar,
    private router: Router,
    private dialog: MatDialog,
    private authService: AuthService,
    private clockApi: ClockApiService,
    private auth: AuthService,
    private time: TimeFormatService,
    private leaveApi: LeaveApiService,
    private cdr: ChangeDetectorRef
  ) { }

  clockNotifyDialog: any;
  //當前時間變數
  currentTime: string = '';
  currentDateStr: string = '';
  currentTimeStr: string = '';
  private timer: any;
  clockRecordDataInit: any;
  clockInTime: string = '';
  clockInTimeStr: string = '';
  clockOutTime: string = '';
  clockOutTimeStr: string = '';
  today: string = this.getToday();
  //使用者資訊
  userInfo: any;
  userId: number = 0;

  //打卡異常
  notificationCount: number = 0;
  notificationList: any;
  detailNotificationList: any;
  notificationDate: string = '';

  ngOnInit() {
    this.updateTime();
    this.timer = setInterval(() => {
      this.updateTime();
    }, 1000);

    this.auth.loginStatusChanged.subscribe((status) => {
      if (status) {
        this.initUserData()
      } else {
        this.userInfo = null;
        this.userId = 0;
      }
    });

    if (this.auth.isLoggedIn()) {
      this.initUserData()
    }
  }

  //初始化資料
  initUserData() {
    this.userInfo = this.auth.getUserInfo();
    this.userId = this.userInfo.id;
    //取得打卡時間
    this.getClockTime();
    //載入打卡異常通知
    this.loadNotification();
    //設定功能卡片
    this.setupFunctionCards();
  }

  ngOnDestroy() {
    clearInterval(this.timer);
  }

  getToday() {
    const now = new Date();
    return now.toISOString().split('T')[0];
  }

  updateTime() {
    const now = new Date();
    this.currentDateStr = now.toLocaleDateString('zh-TW', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    });

    this.currentTimeStr = now.toLocaleTimeString('zh-TW', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false,
    });

    this.currentTime = `${this.currentDateStr} ${this.currentTimeStr}`;
  }

  /**載入打卡異常資料*/
  loadNotification() {
    this.clockApi.getPersonalNotification(this.userId).subscribe({
      next: (res: any) => {
        if (res.code !== 200) {
          this.notificationCount = 0;
          console.warn('無打卡異常通知或回傳錯誤', res);
        } else {

          this.notificationList = res.exceptionList;
          this.detailNotificationList = this.notificationList.flatMap((exception: any) => {
            return exception.reasonList.map((reason: any) => ({
              employeeId: exception.employeeId,
              employeeName: exception.employeeName,
              department: exception.department,
              clockIn: exception.clockIn,
              clockOut: exception.clockOut,
              reason: reason
            }));
          });
          this.notificationCount = this.detailNotificationList.length;
        }
      },
      error: (err: any) => {
        console.error('載入打卡異常通知失敗', err);
        this.notificationCount = 0;
      },
    });
  }



  /**打卡異常顯示*/
  openNotificationDialog(triggerBtn: HTMLElement) {
    const rect = triggerBtn.getBoundingClientRect();

    this.clockNotifyDialog = this.dialog.open(ClockNotifyComponent, {
      data: {
        notifications: this.detailNotificationList,
        date: this.detailNotificationList[0].clockIn
          ? this.time.formatISODateTimeString(this.detailNotificationList[0].clockIn, false)
          : ''
      },
      panelClass: 'notify-dialog-panel',
      position: {
        top: `${rect.top + window.scrollY - 10}px`,
        left: `${rect.left + window.scrollX - 330}px`,
      },
    });
  }


  // 監聽視窗大小變化，關閉通知對話框
  @HostListener('window:resize')
  onResize() {
    if (this.clockNotifyDialog) {
      this.clockNotifyDialog.close();
    }
  }

  clockIn() {
    const now = new Date();
    const clockTime = now.toLocaleTimeString('zh-TW', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false,
    });
    const today = this.getToday();
    const TimeToBackEnd = `${today}T${clockTime}`;

    this.clockApi.clockIn(TimeToBackEnd).subscribe({
      next: (res: any) => {        
        if (res.code !== 200) {
          this._snackBar.open('打卡失敗，請稍後再試', '關閉', {
            duration: 2000,
          });
          return;
        }
        this.getClockTime();
      },
      error: (err: any) => {
        this._snackBar.open('打卡失敗，請稍後再試', '關閉', {
          duration: 2000,
        });
        console.error('打卡失敗', err);
      },
    });
  }

  clockOut() {
    const now = new Date();
    const clockTime = now.toLocaleTimeString('zh-TW', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false,
    });
    const today = this.getToday();
    const TimeToBackEnd = `${today}T${clockTime}`;

    this.clockApi.clockOut(TimeToBackEnd).subscribe({
      next: (res: any) => {
        if (res.code !== 200) {
          this._snackBar.open('打卡失敗，請稍後再試', '關閉', {
            duration: 2000,
          });
          return;
        }
        this.getClockTime();
      },
      error: (err: any) => {
        this._snackBar.open('打卡失敗，請稍後再試', '關閉', {
          duration: 2000,
        });
        console.error('打卡失敗', err);
      },
    });
  }

  /**取得打卡時間 */
  getClockTime() {
    if (!this.userInfo || !this.userInfo.id) {
      console.error('userInfo 尚未初始化，無法取得打卡時間');
      return;
    }
    const body = {
      employeeId: this.userId,
      startTime: this.today,
      endTime: this.today,
    }
    this.clockApi.getClockTime(body).subscribe({
      next: (res: any) => {
        if (res.code !== 200) {
          this._snackBar.open('系統異常無法取得今日打卡記錄', '關閉', {
            duration: 2000,
          });
          return;
        }
        this.clockInTime = res.attendanceRecordList[0]?.clockIn;
        this.clockInTimeStr = this.clockInTime ? this.clockInTime.split("T")[1] : '';
        this.clockOutTime = res.attendanceRecordList[0]?.clockOut;
        this.clockOutTimeStr = this.clockOutTime ? this.clockOutTime.split("T")[1] : '';
      },
      error: (err: any) => {
        this._snackBar.open('取得打卡時間失敗', '關閉', {
          duration: 2000,
        });
        console.error('取得打卡時間失敗', err);
      },
    });
  }

  openClockRecord() {

    this.dialog.open(AddClockRecordComponent, {
      disableClose: false,
    });
  }

  /**
   * 根據使用者角色回傳功能卡片列表
   */
  finalFunctionList: any[] = [];
  /**所有卡片 */
  allFunctionCards = [
    {
      key: 'companyInfo',
      title: "公司資訊",
      description: "查看公司詳細資訊",
      icon: "work",
      route: "/company-info",
    },
    {
      key: 'personalInfo',
      title: "個人資訊",
      description: "查看個人詳細資訊",
      icon: "person",
      route: "",
    },
    {
      key: 'takeLeave',
      title: "請假系統",
      description: "請假申請與歷史記錄",
      icon: "event",
      route: "/take-leave",
    },
    {
      key: 'employeeList',
      title: "員工清單",
      description: "查詢所有員工資訊",
      icon: "people",
      route: "/employeeList",
    },
    {
      key: 'employeeCreate',
      title: "建立員工",
      description: "新增新進員工資料",
      icon: "add",
      route: "/employeeCreate",
    },
    {
      key: 'leaveReview',
      title: "假單審核",
      description: "審核員工請假申請",
      icon: "check_circle",
      route: "/leave-review",
      badgeCount: 0
    },
    {
      key: "companyInfoEdit",
      title: "編輯公司資訊",
      description: "查看公司詳細資訊",
      icon: "build",
      route: "/company-info/edit",
    },
    {
      key: "reinstatement",
      title: "員工復職",
      description: "將離職員工復職",
      icon: "cached",
      route: "/reinstatement",
    },
    {
      key: "salaryManagement",
      title: "薪資管理",
      description: "試算、新增員工的薪資",
      icon: "attach_money",
      route: "/salaryManagement",
    }
  ]

  /**每個角色對應的卡片 */
  roleFunctionMap = {
    [Role.Boss]: ['personalInfo', 'takeLeave', 'employeeList', 'employeeCreate', 'leaveReview', 'companyInfoEdit'],
    [Role.HRManager]: ['personalInfo', 'takeLeave', 'employeeList', 'employeeCreate', 'reinstatement', 'leaveReview'],
    [Role.HREmployee]: ['personalInfo', 'takeLeave', 'employeeList', 'employeeCreate', 'leaveReview'],
    [Role.AcctManager]: ['personalInfo', 'takeLeave', 'employeeList', 'salaryManagement'],
    [Role.AcctEmployee]: ['personalInfo', 'takeLeave', 'employeeList', 'salaryManagement'],
    [Role.GAManager]: ['personalInfo', 'takeLeave', 'employeeList', 'leaveReview'],
    [Role.GAEmployee]: ['personalInfo', 'takeLeave'],
  };

  /**設定功能卡片 */
  setupFunctionCards() {
    const role = this.auth.getUserRole();
    const employee = this.auth.getUserInfo();
    if (!role || !employee) return;

    const allowKeys = this.roleFunctionMap[role] || [];

    this.finalFunctionList = this.allFunctionCards
      .filter(card => typeof card.key === 'string' && allowKeys.includes(card.key))
      .map(card => {
        if (card.key === 'personalInfo') {
          return { ...card, route: `/employeeDetail/${employee.id}` };
        }
        return { ...card };
      });

    this.getPendingLeaves();
  }


  pendingLeaves: any;
  pendingLeavesNumber!: number;
  getPendingLeaves() {
    this.leaveApi.checkLeaveApplication().subscribe({
      next: (res) => {
        this.pendingLeaves = (res.leaveApplicationList ?? []).filter((pending: any) => {
          return pending.status.toLowerCase() === 'pending review';
        });
        this.pendingLeavesNumber =this.pendingLeaves.length;
        this.finalFunctionList = this.finalFunctionList.map(card => {
          if (card.key === 'leaveReview') {
            return { ...card, badgeCount: this.pendingLeavesNumber };
          }
          return { ...card };
        });
      },
      error: (err) => {
        console.error('取得待審核假單失敗:', err);
        this._snackBar.open('取得待審核假單失敗，請稍後再試', '關閉', {
          duration: 2000,
        });
      },
    })
  }

  navigateTo(route: string) {
    this.router.navigate([route]);
  }

  backToHome() {
    this.router.navigate(["/home"]);
  }

}
