import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { EmployeeListComponent } from './employee-list/employee-list.component';
import { EmployeeCreateComponent } from './employee-create/employee-create.component';
import { EmployeeDetailComponent } from './employee-detail/employee-detail.component';
import { EmployeeEditComponent } from './employee-edit/employee-edit.component';
import { AuthGuard } from './guards/auth.guard';
import { EmployeeDetailAccessGuard } from "./guards/employee-detail.guard";
import { EmployeeListGuard } from "./guards/employee-list.guard";
import { EmployeeCreateGuard } from "./guards/employee-create.guard";
import { TakeLeaveComponent } from './take-leave/take-leave.component';
import { LeaveReviewComponent } from './leave-review/leave-review.component';
import { ForgotPasswordComponent } from './login/forgot-password/forgot-password.component';
import { ReinstatementComponent } from './reinstatement/reinstatement.component';
import { leaveReviewGuardsGuard } from './guards/leave-review-guards.guard';
import { SalaryManagementComponent } from './salary-management/salary-management.component';
import { CompanyInfoComponent } from './company-info/company-info.component';
import { CompanyInfoEditComponent } from './company-info-edit/company-info-edit.component';


// AuthGuard、EmployeeListGuard、EmployeeDetailAccessGuard皆為限制使用者的權限(無法輸入網址前往特定網址)
export const routes: Routes = [




  // 暫時先不加權限，等測試完再一個一個加回去
  // { path: "login", component: LoginComponent },
  // { path: "home", component: HomeComponent, canActivate: [AuthGuard] },
  // { path: "employeeList", component: EmployeeListComponent, canActivate: [AuthGuard, EmployeeListGuard] },
  // { path: "employeeCreate", component: EmployeeCreateComponent, canActivate: [AuthGuard, EmployeeCreateGuard] },
  // { path: "employeeDetail/:id", component: EmployeeDetailComponent, canActivate: [AuthGuard, EmployeeDetailAccessGuard] },
  // { path: "employeeEdit/:id", component: EmployeeEditComponent, canActivate: [AuthGuard] },
  //  { path: 'take-leave', component: TakeLeaveComponent, canActivate: [AuthGuard], },
  //   { path: 'leave-review', component: LeaveReviewComponent,canActivate: [AuthGuard,leaveReviewGuardsGuard] },

  { path: 'take-leave', component: TakeLeaveComponent },
  { path: 'leave-review', component: LeaveReviewComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },

  { path: "login", component: LoginComponent },
  { path: "home", component: HomeComponent },
  { path: "employeeList", component: EmployeeListComponent },
  { path: "employeeCreate", component: EmployeeCreateComponent },
  { path: "employeeDetail/:id", component: EmployeeDetailComponent },
  { path: "employeeEdit/:id", component: EmployeeEditComponent },
  { path: "reinstatement", component: ReinstatementComponent },
  { path: "salaryManagement", component: SalaryManagementComponent },
  { path: "salaryManagement/:id", component: SalaryManagementComponent },
  { path: "company-info", component: CompanyInfoComponent },
  { path: 'company-info/edit', component: CompanyInfoEditComponent },



  { path: "", redirectTo: "/login", pathMatch: "full" },
];
