import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ForgotPasswordApiService {
  baseUrl = 'http://localhost:8080/HRMS';
  constructor(private http: HttpClient) { }




  /**寄送驗證信 */
  sendVerificationLetter(email: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/sendVerificationLetter`, null, {
      params: { email },
      withCredentials: true,
    });
  }

  /**確認驗證碼 */
  checkVerification(email: string, code: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/checkVerification`, null, {
      params: { email, code },
      withCredentials: true,
    });
  }

  /**修改密碼 */
  updatePwdByEmail(body: { email: string, newPassword: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/updatePwdByEmail`, body, {
      withCredentials: true,
    });
  }
}



