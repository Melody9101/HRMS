import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ClockApiService {
  baseUrl = 'http://localhost:8080/HRMS';
  constructor(private http: HttpClient) {}

  clockIn(clockInTime: string): Observable<any> {
    console.log('打卡時間',clockInTime);
    return this.http.post(`${this.baseUrl}/clockIn`, null, {
      params: { clockInTime },
      withCredentials: true,
    });
    
    
  }

  clockOut(clockOutTime: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/clockOut`, null, {
      params: { clockOutTime },
      withCredentials: true,
    });
  }

  getClockTime(body:{employeeId:number,startTime:string,endTime:string}): Observable<any>{
     return this.http.post(`${this.baseUrl}/getAttendanceByEmployeeIdAndDate`, body, {
      withCredentials: true,
    });
  }

  getPersonalNotification(employeeId: number): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/checkAttendanceExceptionListByEmployeeId`,
      null,
      {
        params: { employeeId },
        withCredentials: true,
      }
    );
  }
}
