export interface CompanyInfo {
  id: number;
  name: string;
  taxIdNumber: number;
  ownerName: string;
  phone: string;
  email: string;
  address: string;
  website: string;
  // 後端是 LocalDate，前端是 string
  establishmentDate: string;
  capitalAmount: number;
  employeeCount: number;
  status: string;
  // 後端是 LocalDate，前端是 string
  createAt: string;
  updateAt: string;
  // 後端是 LocalDate，前端是 string
  workStartTime: string;
  lunchStartTime: string;
  lunchEndTime: string;
  timezone: string;
}
