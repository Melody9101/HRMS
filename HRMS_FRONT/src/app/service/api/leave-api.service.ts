import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { takeLeaveData } from '../../models/take-leave-model';

@Injectable({
  providedIn: 'root'
})
export class LeaveApiService {
  baseUrl = 'http://localhost:8080/HRMS';
  constructor(private http: HttpClient) { }


  //請假系統
  applyLeave(body: { startTime: string; endTime: string; leaveType: string; certificate?: string; reason: string; certificateFileType?: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/applyLeave`, body, {
      withCredentials: true,
    });
  }

  fetchLeaves(body: { employeeId?: number; startTime: string; endTime: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/searchLeaveRecordByEmployeeIdAndDate`, body, {
      withCredentials: true,
    });
  }

  cancelLeave(leaveIdList: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/cancelLeaveApplication`, null, {
      params: { leaveIdList },
      withCredentials: true,
    });
  }


  //請假審核系統
  /**取得待審核假單 */
  checkLeaveApplication(): Observable<any> {
    return this.http.get(`${this.baseUrl}/checkLeaveApplication`, {
      withCredentials: true,
    });
  }

  /**審核行為 */
  approveLeave(body: { leaveId: number, status: string, rejectionReason?: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/approveLeave`, body, {
      withCredentials: true,
    });
  }


  /**補件 */
  updateLeaveApplicationCertificate(body: { leaveId: number, certificate: string; certificateFileType: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/updateLeaveApplicationCertificate`, body, {
      withCredentials: true,
    });
  }

}
