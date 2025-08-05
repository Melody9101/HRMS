import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { CommonModule } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router } from '@angular/router';
import { ForgotPasswordApiService } from '../../service/api/forgot-password-api.service';
import { MatSnackBar } from '@angular/material/snack-bar';
@Component({
  selector: 'app-forgot-password',
  imports: [
    MatButtonModule,
    MatStepperModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatTooltipModule,
    CommonModule
  ],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss',
})
export class ForgotPasswordComponent {
  emailFormGroup: any;
  codeFormGroup: any;
  passwordFormGroup: any;
  constructor(private _formBuilder: FormBuilder, private router: Router, private forgotPwdApi: ForgotPasswordApiService, private _snackBar: MatSnackBar) {
    //表單內容 
    this.emailFormGroup = this._formBuilder.group({
      email: [
        '',
        [
          Validators.required,
          Validators.email,
          Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/),
        ],
      ],
    });
    this.codeFormGroup = this._formBuilder.group({
      code: [
        '',
        [
          Validators.required,
          Validators.pattern(/^[A-Za-z\d]{8}$/),
        ],
      ],
    });
    this.passwordFormGroup = this._formBuilder.group(
      {
        password: [
          '',
          [
            Validators.required,
            Validators.pattern(
              /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,30}$/
            ),
          ],
        ],
        confirmPassword: ['', [Validators.required]],
      },
      {
        validators: this.checkConfirmPassword.bind(this),
      }
    );
  }

  @ViewChild('stepper') stepper!: MatStepper;


  get formControls() {
    return {
      email: this.emailFormGroup?.get('email'),
      code: this.codeFormGroup?.get('code'),
      password: this.passwordFormGroup?.get('password'),
      confirmPassword: this.passwordFormGroup?.get('confirmPassword'),
    };
  }

  /**發送驗證信 */
  fillInEmail: string = ''
  hasEmail: boolean = false;
  verificationSend:boolean = false;
  sendVerificationLetter() {
    this.fillInEmail = this.formControls.email.value;
    // 先切下一步
    this.stepper.next();
    this.verificationSend=false;
    this.forgotPwdApi.sendVerificationLetter(this.formControls.email.value).subscribe({
      next: (res: any) => {
        if (res.code === 200) {          
          this.hasEmail = true;
          this.verificationSend=true;
          this.startCodeCoolDown();
        }

        if (res.message === "Not Found!!") {
          this._snackBar.open('請確認與當時創建的帳號是否一致', '確定', { duration: 3000 });
          this.hasEmail = false;
          this.stepper.previous();
          return;
        } else if (res.code !== 200) {
          this.stepper.previous();//檢測有錯回上頁
          return;
        }
      },
      error: (err: any) => {
        console.error('驗證信傳送失敗', err);
      },
    });
  }

  /**輸入驗證碼 */
  checkVerification() {
    if (this.codeFormGroup.invalid) {
      this.codeFormGroup.markAllAsTouched();
      return;
    }

    this.forgotPwdApi.checkVerification(this.fillInEmail, this.formControls.code.value).subscribe({
      next: (res: any) => {
        if (res.code === 200) {
          this.stepper.next();
        } else if (res.code !== 200) {
          this._snackBar.open('驗證碼錯誤，請確認大小寫是否相符', '確定', { duration: 3000 });
          return;
        }
      },
      error: (err: any) => {
        console.error('驗證信驗證失敗', err);
        this._snackBar.open('系統不明錯誤，請稍候再試', '確定', { duration: 3000 });
      },
    });
  }

  codeCoolDown: number = 10;
  codeCoolDownStarted: boolean = false;

  /**啟動寄送驗證信10秒冷卻 */
  startCodeCoolDown() {
  this.codeCoolDown = 10;
  this.codeCoolDownStarted = true;

  const updateCooldown = () => {
    if (this.codeCoolDown > 0) {
      this.codeCoolDown--;
    } else {
      clearInterval(interval);
    }
  };

  updateCooldown();

  const interval = setInterval(updateCooldown, 1000);
}


  /**重新發送驗證信 */
  resendDisable:boolean=false;
  resendCode() {
    this.resendDisable=true;
    this.verificationSend=false;
    if (this.codeCoolDown === 0) {
      this.forgotPwdApi.sendVerificationLetter(this.formControls.email.value).subscribe({
        next: (res: any) => {
          if (res.code === 200) {
            this.verificationSend=true;
            this.resendDisable=false;
            this.startCodeCoolDown();
          }

          if (res.message === "Not Found!!") {
            this._snackBar.open('請確認與當時創建的帳號是否一致', '確定', { duration: 3000 });
            return;
          } else if (res.code !== 200) {
            this._snackBar.open('系統發生錯誤', '確定', { duration: 3000 });
          return;
          }
        },
        error: (err: any) => {
          console.error('驗證信傳送失敗', err);
        },
      });
    }
  }




  hidePassword: boolean = true;
  hideConfirmPassword: boolean = true;
  /**更換密碼可視狀態 */
  changeVisibility(field: 'password' | 'confirmPassword') {
    if (field === 'password') {
      this.hidePassword = !this.hidePassword;
    } else if (field === 'confirmPassword') {
      this.hideConfirmPassword = !this.hideConfirmPassword;
    }
  }

  // 密碼規則檢查
  get password(): string {
    return this.formControls?.password?.value || '';
  }
  get passwordRulesStatus() {
    return {
      isValidLength: this.password.length >= 8 && this.password.length <= 30,
      hasUppercase: /[A-Z]/.test(this.password),
      hasLowercase: /[a-z]/.test(this.password),
      hasNumber: /[0-9]/.test(this.password),
      hasSpecialChar: /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(
        this.password
      ),
    };
  }

  // 密碼強度等級
  get passwordStrengthLevel(): 'low' | 'middle' | 'strong' {
    const rules = this.passwordRulesStatus;
    const passedRules = Object.values(rules).filter((v) => v).length;
    const lengthScore =
      this.password.length >= 15 ? 2 : this.password.length >= 10 ? 1 : 0;
    const totalScore = passedRules + lengthScore;
    if (totalScore <= 2) return 'low';
    if (totalScore <= 5) return 'middle';
    return 'strong';
  }

  get firstPasswordError(): string | null {
    const rules = this.passwordRulesStatus;

    if (!rules.isValidLength) return '密碼長度需介於 8～30 字元';
    if (!rules.hasUppercase) return '需包含至少一個大寫英文';
    if (!rules.hasLowercase) return '需包含至少一個小寫英文';
    if (!rules.hasNumber) return '需包含至少一個數字';
    if (!rules.hasSpecialChar) return '需包含至少一個特殊符號，如 @ # $ % ! 等';

    return null; // 若通過所有規則
  }

  /**檢查確認密碼是否一致 */
  checkConfirmPassword(group: AbstractControl): ValidationErrors | null {
    const password = group.get('password')?.value;
    const confirmPasswordControl = group.get('confirmPassword');

    if (!confirmPasswordControl) return null;

    if (password !== confirmPasswordControl.value) {
      confirmPasswordControl.setErrors({
        ...confirmPasswordControl.errors,
        notSame: true,
      });
      return { notSame: true };
    } else {
      // 安全移除 notSame 錯誤（保留其他錯誤）
      const errors = confirmPasswordControl.errors;
      if (errors) {
        delete errors['notSame'];
        if (Object.keys(errors).length === 0) {
          confirmPasswordControl.setErrors(null);
        } else {
          confirmPasswordControl.setErrors(errors);
        }
      }
      return null;
    }
  }

  /**修改密碼api */
  updatePwdByEmail(){
    if (this.passwordFormGroup.invalid) {
      this.passwordFormGroup.markAllAsTouched();
      return;
    }

    const body ={
      email:this.fillInEmail,
      newPassword:this.formControls.password.value
    }

    this.forgotPwdApi.updatePwdByEmail(body).subscribe({
      next: (res: any) => {
        if (res.code === 200) {
          this.stepper.next();
        } else if (res.code !== 200) {
          this._snackBar.open('修改密碼失敗', '確定', { duration: 3000 });
          return;
        }
      },
      error: (err: any) => {
        console.error('修改密碼失敗', err);
        this._snackBar.open('系統不明錯誤，請稍候再試', '確定', { duration: 3000 });
      },
    });
  }



  toLogin() {
    this.router.navigate(['/login']);
  }
}
