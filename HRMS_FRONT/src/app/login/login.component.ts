import { ChangeDetectorRef, Component } from '@angular/core';
import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../services/auth.service';
import { LoginApiService } from '../service/api/login-api.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatIconModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  formValue: any;
  loginForm: any;
  submitted: boolean = false;
  filedBlur: { [key: string]: boolean } = {};
  filedFocus: { [key: string]: boolean } = {};
  hidePassword: boolean = true;

  constructor(
    private router: Router,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef,
    private authService: AuthService,
    private loginApi: LoginApiService
  ) {
    this.loginForm = this.fb.group({
      account: ['', [Validators.required]],
      password: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    // 如果已經登入，就自動跳轉到首頁或其他頁面
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/home']);
    }
  }

  get formControls() {
    return this.loginForm.controls;
  }

  //顯示錯誤訊息
  onFieldBlur(field: string) {
    this.filedBlur[field] = true;
    this.filedFocus[field] = false;
  }

  isFocused(field: string) {
    this.filedFocus[field] = true;
  }

  showFieldError(field: string): boolean {
    const control = this.loginForm.get(field);
    return !!(
      control &&
      control.invalid &&
      (control.touched || this.filedBlur[field])
    );
  }

  loginError: string = '';
  isLogin: boolean = false;
  showError: boolean = false;
  showErrorMessage(message: string) {
    this.showError = false;
    this.loginError = '';
    this.cdr.detectChanges(); // 先強迫清空

    setTimeout(() => {
      this.loginError = message;
      this.showError = true;
      this.cdr.detectChanges(); // 再強迫更新
    }, 50);
  }

  //密碼顯示控制
  changeVisibility() {
    this.hidePassword = !this.hidePassword;
  }

  login() {
    const email = this.formControls['account'].value;
    const password = this.formControls['password'].value;

    if (!email || !password) {
      this.showErrorMessage('請確認欄位是否填寫完畢');
      return;
    }

    this.loginApi.login({
      account: email,
      password: password,
    }).subscribe({
      next: (res: any) => {
        if (res.code != 200) {
          this.showErrorMessage('登入失敗，請再試一次');
          return;
        }
        this.authService.login(email, password);
        this.router.navigate(['/home']);
      },
      error: (err: any) => {
        console.error("登入失敗", err);
        this.showErrorMessage("請輸入正確的帳號密碼");
      }
    });

  }
}
