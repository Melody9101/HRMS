import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class HttpClientService {

  constructor(private http: HttpClient) { }

  getApi<T>(url: string) {
    return this.http.get<T>(url, { withCredentials: true });
  }

  postApi(url: string, postData: any, options: any = {}) {
    return this.http.post(url, postData, {
      withCredentials: true,
      headers: { "Content-Type": "application/json" },
      // 合併傳進來的其他設定（像 params）
      ...options
    });
  }

  putApi(url: string, postData: any) {
    return this.http.put(url, postData, { withCredentials: true });
  }

  delApi(url: string) {
    return this.http.delete(url, { withCredentials: true });
  }
}
