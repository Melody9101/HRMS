export interface GetAllSalariesOrByEmployeeIdListRes {
  salaries: {
    id: number;
    employeeId: number;
    year: number;
    month: number;
    salary: number;
    bonus: number;
  }[];
}
