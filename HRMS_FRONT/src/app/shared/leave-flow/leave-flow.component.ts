import { Component, inject, Input } from '@angular/core';
import { TakeLeaveService } from '../../service/leave/take-leave.service';
import { TimeFormatService } from '../../service/time-format.service';
import { CommonModule } from '@angular/common';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';

@Component({
  standalone: true,
  selector: 'app-leave-flow',
  imports: [
    CommonModule,
    MatChipsModule
  ],
  templateUrl: './leave-flow.component.html',
  styleUrl: './leave-flow.component.scss',
})
export class LeaveFlowComponent {
  constructor(
    public leave: TakeLeaveService,
    public time: TimeFormatService,
    public takeLeave: TakeLeaveService,
    private _snackBar: MatSnackBar,
    private auth: AuthService
  ) { }
  /**開啟補件dialog */
  openSupplementDialog(element: any) {
    this.takeLeave
      .openSupplement(element)
      .afterClosed()
      .subscribe((result) => {
        if (result?.status === 'success') {
          element.status = 'pending review';
          element.certificate = result?.certificate
          this._snackBar.open('補交完成，狀態已更新為審查中', '關閉', {
            duration: 2000,
          });
        }
      });
  }
  recentLeave!: any;
  ngOnInit(): void {
    this.auth.loginStatusChanged.subscribe((status) => {
      if (status) {
        this.loadRecentLeave();
      } 
    });

    if (this.auth.isLoggedIn()) {
      this.loadRecentLeave();
    }
    
  }

   loadRecentLeave() {
    this.takeLeave.fetchLeavesForFlow().subscribe({
      next: () => {
        const recentLeave = this.takeLeave.getRecentLeave();
        this.recentLeave = recentLeave;
      },
      error: (err) => {
        console.error('抓假單失敗', err);
      },
    });
  }

}
