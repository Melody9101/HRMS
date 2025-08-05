import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CompanyInfo } from '../models/company-info.model';
import { HttpClientService } from './http-client.service';
import { UpdateCompanyInfoReq } from '../models/update-company-info-req.model';

@Injectable({ providedIn: 'root' })
export class CompanyInfoService {

  constructor(
    private http: HttpClientService
  ) { }

  /**
   * 查詢公司資訊
   */
  getCompanyInfo(): Observable<{
    code: number;
    message: string;
    companyInfo: CompanyInfo;
  }> {
    return this.http.getApi<{
      code: number;
      message: string;
      companyInfo: CompanyInfo;
    }>('http://localhost:8080/HRMS/checkCompanyInfo');
  }


  /**
   * 更新公司資訊(只有Boss可用)
   */
  updateCompanyInfo(data: UpdateCompanyInfoReq): Observable<any> {
    return this.http.postApi('http://localhost:8080/HRMS/updateCompanyInfo', data);
  }
}
