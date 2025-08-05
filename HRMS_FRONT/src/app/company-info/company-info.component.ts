import { Component, OnInit } from '@angular/core';
import { CompanyInfoService } from '../services/company-info.service';
import { CompanyInfo } from '../models/company-info.model';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { TIMEZONES_ZH } from '../shared/constants/timezone-options-zh';

@Component({
  selector: 'app-company-info',
  standalone: true,
  imports: [
    DatePipe,
  ],
  templateUrl: './company-info.component.html',
  styleUrl: './company-info.component.scss'
})
export class CompanyInfoComponent implements OnInit {

  timezoneOptions = TIMEZONES_ZH;

  companyInfo: CompanyInfo | null = null;

  constructor(
    private router: Router,
    private companyService: CompanyInfoService
  ) { }

  // 從後端取得公司資訊
  ngOnInit(): void {
    this.companyService.getCompanyInfo().subscribe({
      next: (res) => {
        this.companyInfo = res.companyInfo;
      },
      error: (error) => {
        console.error("查詢公司資訊失敗", error);
      },
    });
  }

  /**
   * 時區中文化
   */
  getTimezoneLabel(timezone: string): string {
    let match = TIMEZONES_ZH.find(tz => tz.value == timezone);
    if (!match) return timezone;
    return match.label.replace(/^\(UTC[^\)]+\)\s*/, "");
  }

  /**
   * 小導覽條-首頁
   */
  backToHome() {
    this.router.navigate(["/home"]);
  }

  /**
   * 小導覽條-公司資訊
   */
  backToCompanyInfo() {
    this.router.navigate(["/company-info"]);
  }
}
