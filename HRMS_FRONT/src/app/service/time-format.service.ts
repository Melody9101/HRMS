import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class TimeFormatService {
  /**整理僅有日期的字串(前端顯示用) */
  formatDateToString(date: Date): string {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0'); // 月份從 0 開始
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`; // 例如：2025-06-09
  }

  /**整理包含時、分的字串(前端顯示用)*/
  formatTimeToString(date: Date): string {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0'); // 月份從 0 開始
    const day = date.getDate().toString().padStart(2, '0');
    const hour = date.getHours().toString().padStart(2, '0');
    const min = date.getMinutes().toString().padStart(2, '0');
    return `${year}-${month}-${day} ${hour}:${min}`; // 例如：2025-06-09
  }

  /**整理後端帶回的資料格式包含時間(前端顯示用) */
  formatISODateTimeString(isoString: string, showTime: boolean = true): string {
    const date = new Date(isoString);
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hour = date.getHours().toString().padStart(2, '0');
    const min = date.getMinutes().toString().padStart(2, '0');
    if (!showTime) {
      return `${year}-${month}-${day} `;
    }
    return `${year}-${month}-${day} ${hour}:${min}`;
  }





  /**整理要帶給後端的時間格式 */
  formatSendBackEnd(date: Date, hour: string, minute: string) {
    return `${this.formatDateToString(date)}T${hour}:${minute}:00`;
  }
  hourOpt: string[] = [];
  minOpt: string[] = [];

  /**取得給後端的當前時間 */
  getNowSendBackEnd(): string {
    const now = new Date();
    const hour = now.getHours().toString().padStart(2, '0');
    const minute = now.getMinutes().toString().padStart(2, '0');
    return this.formatSendBackEnd(now, hour, minute);
  }

  /**改假別單位(從天=>天、時) */
 formatDaysToDayHour(dayValue: number): string {
  const days = Math.floor(dayValue); // 直接取整天
  const remainDay = dayValue - days; // 剩下的小數部分

  const hours = Math.round(remainDay * 8 * 10) / 10; // 小數天 × 8 (每天8小時)，再保留一位小數

  let result = '';
  if (days > 0) {
    result += `${days} 天`;
  }
  if (hours > 0) {
    if (result) {
      result += ' ';
    }
    result += `${hours} 小時`;
  }

  if (!result) {
    result = '0 小時';
  }

  return result;
}

  
  /**生成時間選項 */
  generateTimeOptions() {
    // 小時 00 - 23
    this.hourOpt = Array.from({ length: 24 }, (_, i) =>
      i.toString().padStart(2, '0')
    );

    // 分鐘 00, 10, 20
    this.minOpt = Array.from({ length: 2 }, (_, i) =>
      (i * 30).toString().padStart(2, '0')
    );
  }




  constructor() { }
}
