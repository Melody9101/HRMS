export interface LeaveBalanceRecord {
  leaveType: string;         // 假期名稱，例如 "特休"
  totalHours: number;        // 可休時數（總時數）
  usedHours: number;         // 已休時數
  remainingHours: number;    // 剩餘時數
}
