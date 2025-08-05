export interface UpdateSalariesByIdReq {
  id: number;
  employeeId: number;
  year: number;
  month: number;
  salary: number;
  bonus: number;
}
