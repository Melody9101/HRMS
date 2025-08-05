import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { EmployeeService } from '../services/employee-data.service';
import { MatTableModule } from '@angular/material/table';
import { Employee } from '../models/employee.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { ReinstatementDialogComponent } from '../dialog/reinstatement-dialog/reinstatement-dialog.component';

@Component({
  standalone: true,
  selector: 'app-reinstatement',
  imports: [
    FormsModule,
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatDividerModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
  ],
  templateUrl: './reinstatement.component.html',
  styleUrl: './reinstatement.component.scss'
})
export class ReinstatementComponent {

  searchEmployeeEmail: string = "";
  searchEmployeePhone: string = "";

  departedEmployee: Employee | null = null;

  constructor(
    private router: Router,
    private employeeService: EmployeeService,
    private dialog: MatDialog
  ) { }

  /**
   * 查詢離職員工
   */
  searchDepartedEmployee() {
    if (!this.searchEmployeeEmail || !this.searchEmployeePhone) {
      alert("請輸入 Email 與電話");
      return;
    }

    this.employeeService
      .searchDepartedEmployee({
        email: this.searchEmployeeEmail,
        phone: this.searchEmployeePhone
      })
      .subscribe({
        next: (employee) => {
          this.departedEmployee = employee;
          if (!employee) alert("查無離職員工資料");
        },
        error: () => alert("系統錯誤，請稍後再試")
      });
  }

  /**
   * 確認復職
   */
  confirmReinstatement() {
    if (!this.departedEmployee) return;

    const dialogRef = this.dialog.open(ReinstatementDialogComponent, {
      width: '400px',
      disableClose: true,
      data: {} // 若未來需要預設值可加在這
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const { salaries, reinstatementDate } = result;

        const req = {
          employeeId: this.departedEmployee!.id,
          department: this.departedEmployee!.department,
          email: this.departedEmployee!.email,
          phone: this.departedEmployee!.phone,
          grade: this.departedEmployee!.grade,
          salaries: salaries,
          position: this.departedEmployee!.position,
          reinstatementDate: reinstatementDate
        };

        this.employeeService.reinstatementEmployee(req).subscribe({
          next: () => {
            alert("復職成功");
            this.router.navigate(['/employeeList']);
          },
          error: (error) => {
            console.error(error);
            alert("復職失敗，請確認資料或稍後再試");
          }
        });
      }
    });
  }

  getDepartmentName(department: string): string {
    switch (department) {
      case "Boss": return "總經理";
      case "HR": return "人資部門";
      case "Acct": return "會計部門";
      case "GA": return "一般部門";
      default: return department;
    }
  }

  getGradeLabel(employee: Employee): string {
    if (employee.position == "Boss") return "總經理";
    if (employee.position == "Manager") return "主管";
    if (employee.position == "Employee") return "員工";
    return "未知";
  }

  /**
  * 小導覽條-首頁
  */
  backToHome() {
    this.router.navigate(["/home"]);
  }

  /**
  * 小導覽條-員工復職
  */
  backToReinstatement() {
    this.router.navigate(["/reinstatement"]);
  }
}
