import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { filter } from 'rxjs';

@Component({
  selector: 'app-nav-bar',
  standalone: true,
  imports: [
    RouterLinkActive,
    RouterLink
  ],
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.scss'
})
export class NavBarComponent {
  currentUrl = '';
  title = 'HRMS';
  userInfo: any;
  hideNav: boolean = false;

  constructor(private router: Router, public auth: AuthService) {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        this.currentUrl = event.urlAfterRedirects;
        this.hideNav = this.isAuthPage();
      });
  }


  logout() {
    // 清除登入資訊
    this.auth.logout();
    // 導回登入頁
    this.router.navigate(['/login']);
  }

  isAuthPage(): boolean {
    const authRoutes = ['/login', '/forgot-password'];
    return authRoutes.includes(this.currentUrl);
  }
}
