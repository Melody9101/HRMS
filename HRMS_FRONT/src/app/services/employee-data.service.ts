import { Injectable } from "@angular/core";
import { Employee } from "../models/employee.model";
import { HttpClientService } from "./http-client.service";
import { Observable, map } from "rxjs";
import { AddEmployeeReq } from "../models/add-employee-req.model";

@Injectable({ providedIn: "root" })

export class EmployeeService {

  constructor(
    private http: HttpClientService
  ) { }

  /**
   * 取得所有在職員工清單
   */
  getAll(): Observable<Employee[]> {
    // 請求所有員工資料
    return this.http.getApi("http://localhost:8080/HRMS/getAllEmployees")
      .pipe(
        // 處理回傳的資料格式，只取出employeeList陣列
        map((response: any) => {
          // 如果回傳資料裡有employeeList就取出，否則給空陣列
          let employees: Employee[] = [];
          let list = response.employeeList || [];
          for (let i = 0; i < list.length; i++) {
            employees.push(list[i]);
          }
          return employees;
        })
      );
  }

  /**
   * 取得同部門的員工清單（僅供主管使用）
   */
  getDepartmentEmployeesList(): Observable<Employee[]> {
    return this.http.getApi("http://localhost:8080/HRMS/getDepartmentEmployeesList")
      .pipe(
        map((response: any) => {
          return response.employeeBasicInfoVoList || []
        })
      );
  }

  /**
   * 藉由帳號密碼查詢員工（未來可能移除改用 login）
   */
  findByAccount(account: string, password: string): Observable<Employee | null> {
    return this.http.postApi("http://localhost:8080/HRMS/login", {
      account,
      password
    }).pipe(
      map((response: any) => response.code == 200 ? response.data : null)
    );
  }

  /**
   * 根據 ID 查詢員工
   */
  getById(id: number): Observable<Employee | undefined> {
    return this.http.postApi("http://localhost:8080/HRMS/selectByEmployeeIdList?idList=" + id, {}).pipe(
      map((response: any) => response.employeeList?.[0])
    );
  }

  /**
   * 新增員工
   */
  addEmployee(employee: AddEmployeeReq): Observable<any> {
    return this.http.postApi("http://localhost:8080/HRMS/addEmployee", employee);
  }

  /**
   * 更新員工基本資料
   */
  updateEmployeeBasicInfo(employee: any): Observable<any> {
    return this.http.postApi("http://localhost:8080/HRMS/updateEmployeeBasicInfo", employee);
  }

  /**
   * 更新員工基本資料(職稱、職等及薪資)
   */
  updateEmployeeJob(jobInfo: {
    id: number;
    department: string;
    position: string;
    grade: number;
  }): Observable<any> {
    return this.http.postApi("http://localhost:8080/HRMS/updateEmployeeJob", jobInfo);
  }

  /**
   * 更新留職停薪的起訖日期及原因
   */
  updateEmployeeUnpaidLeaveById(unpaidLeaveInfo: {
    id: number;
    unpaidLeaveStartDate: string;
    unpaidLeaveEndDate: string;
    unpaidLeaveReason: string;
  }) {
    return this.http.postApi("http://localhost:8080/HRMS/putEmployeeUnpaidLeave", unpaidLeaveInfo);
  }

  /**
   * 將員工調整為離職
   */
  resignEmployee(responseignInfo: {
    id: number;
    resignationDate: string;
    resignationReason: string;
  }): Observable<any> {
    return this.http.postApi("http://localhost:8080/HRMS/responseignEmployee", responseignInfo);
  }

  /**
   * 查詢已離職的員工
   */
  searchDepartedEmployee(req: {
    email: string;
    phone: string;
  }): Observable<Employee | null> {
    return this.http.postApi("http://localhost:8080/HRMS/searchDepartedEmployee", req).pipe(
      map((response: any) => response.employeeInfo ?? null)
    );
  }

  /**
   * 復職員工
   */
  reinstatementEmployee(req: {
    employeeId: number;
    department: string;
    email: string;
    phone: string;
    grade: number;
    salaries: number;
    position: string;
  }): Observable<any> {
    return this.http.postApi("http://localhost:8080/HRMS/reinstatementEmployee", req);
  }

  /**
   * 試算薪資
   */
  calculateMonthlySalary(req: {
    employeeId: number;
    year: number;
    month: number;
    bonus: number;
  }): Observable<any> {
    return this.http.postApi(
      "http://localhost:8080/HRMS/calculateMonthlySalaryByEmployeeIdYYMMBonus",
      null,
      {
        params: {
          employeeId: req.employeeId,
          year: req.year,
          month: req.month,
          bonus: req.bonus
        }
      }
    );
  }

  /**
   * 新增薪資單
   */
  addSalaries(req: {
    employeeId: number;
    year: number;
    month: number;
    salary: number;
    bonus: number;
  }): Observable<any> {
    return this.http.postApi("http://localhost:8080/HRMS/addSalaries", req);
  }

  /**
   * 根據員工 ID、年份、月份查詢薪資（多筆）
   */
  getMySalaryByYearMonth(req: {
    year: number;
    month: number;
  }): Observable<any> {
    const query = `year=${req.year}&month=${req.month}`;
    return this.http.postApi(`http://localhost:8080/HRMS/getSalaryByEmployeeIdYYMM?${query}`, {});
  }

  getAllSalariesOrByEmployeeIdList(req: {
    idList: number[];
    year: number;
    month: number;
  }): Observable<any> {
    const query = `idList=${req.idList.join(",")}&year=${req.year}&month=${req.month}`;
    return this.http.postApi(`http://localhost:8080/HRMS/getAllSalariesOrByEmployeeIdList?${query}`, {});
  }

  updateSalariesById(req: {
    id: number;
    employeeId: number;
    salary: number;
    bonus: number;
  }): Observable<any> {
    return this.http.postApi("http://localhost:8080/HRMS/updateSalariesById", req);
  }

  /**
   * 根據員工 ID、年份與月份查詢請假成功紀錄
   */
  getApprovedLeaveRecordsByEmployeeIdAndYYMM(req: {
    employeeId: number;
    year: number;
    month: number;
  }): Observable<any> {
    return this.http.postApi("http://localhost:8080/HRMS/getApprovedLeaveRecordsByEmployeeIdAndYYMM", req);
  }

}

