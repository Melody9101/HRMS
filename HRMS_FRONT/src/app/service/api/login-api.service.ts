import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LoginApiService {
  baseUrl = 'http://localhost:8080/HRMS';
  constructor(private http: HttpClient) { }

  login(body: { account: string; password: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/login`, body,{
      withCredentials: true, // 如果後端靠 cookie 管 session，這行一定要加
    });
  }
}

