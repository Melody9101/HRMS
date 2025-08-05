// Angular的基本模組與功能
import { CommonModule } from "@angular/common";
import { AfterViewInit, Component, ViewChild } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { Router } from "@angular/router";

// Angular Material
import { MatButtonModule } from "@angular/material/button";
import { MatDividerModule } from "@angular/material/divider";
import { MatIconModule } from "@angular/material/icon";
import { MatPaginator, MatPaginatorModule } from "@angular/material/paginator";
import { MatSort, MatSortModule } from "@angular/material/sort";
import { MatTableDataSource, MatTableModule } from "@angular/material/table";

// 自訂模型與工具
import { Employee } from "../models/employee.model";
import { getGradeLabel } from "../utils/grade-label.util";
import { getDepartmentLabel } from "../utils/department-label.util";
import { EmployeeService } from "../services/employee-data.service";
import { AuthService } from "../services/auth.service";
import { Role } from "../models/role.enum";

@Component({
  selector: "app-employee-list",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatDividerModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
  ],
  templateUrl: "./employee-list.component.html",
  styleUrl: "./employee-list.component.scss"
})
export class EmployeeListComponent implements AfterViewInit {

  // ====== 基本屬性區 ======

  // 部門篩選條件
  searchDepartment: string = "全部";
  // 員工編號搜尋關鍵字
  searchEmployeeId: string = "";
  // 員工姓名搜尋關鍵字
  searchEmployeeName: string = "";
  // 職等篩選條件
  searchgrade: number = -1;
  // 是否在職篩選條件
  searchIsEmployed: boolean | null = null;
  // 表格要顯示的欄位
  displayedColumns: string[] = ["id", "department", "grade", "name"];
  // 表格資料來源
  dataSource = new MatTableDataSource<Employee>();
  // 分頁元件
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  // 排序元件
  @ViewChild(MatSort) sort!: MatSort;
  // 被選取的員工
  selectedEmployee!: Employee;
  // 當前登入的使用者
  user!: Employee;
  // 當前登入者的角色
  userRole: Role | null = null;
  // 快取所有取得的員工資料
  allEmployees: Employee[] = [];

  // ====== 建構式與初始化 ======

  constructor(
    private router: Router,
    private employeeService: EmployeeService,
    private authService: AuthService,
  ) { }

  // 初始化
  ngOnInit() {
    this.user = this.authService.getUserInfo()!;
    this.userRole = this.authService.getUserRole();
    // 根據登入角色決定顯示哪些欄位
    if (this.userRole == Role.Boss || this.userRole == Role.HRManager || this.userRole == Role.HREmployee) {
      this.displayedColumns.unshift("editEmployee");
    }
    if (this.canViewDetail()) {
      this.displayedColumns.push("detail");
    }
    if (this.canShowSalaryButton()) {
      this.displayedColumns.push("salary");
    }
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    let isBossOrHRorAcct =
      this.userRole == Role.Boss ||
      this.userRole == Role.HRManager ||
      this.userRole == Role.HREmployee ||
      this.userRole == Role.AcctManager ||
      this.userRole == Role.AcctEmployee;

    let isGAManager =
      this.userRole == Role.GAManager &&
      this.user.department == "GA" &&
      this.user.grade >= 6;

    if (isBossOrHRorAcct) {
      // HR或總經理：取得所有員工資料
      this.employeeService.getAll().subscribe({
        next: (data) => {
          this.allEmployees = data;
          this.dataSource.data = this.filterVisibleEmployees(this.allEmployees);
          this.search();
          // MatPaginator中文化
          this.setupPaginator();
        },
        error: (error) => {
          console.error("取得員工資料失敗", error);
        }
      });
    } else if (isGAManager) {
      // 一般部門主管：僅取得同部門的員工資料
      this.employeeService.getDepartmentEmployeesList().subscribe({
        next: (data) => {
          // 補上 employed: true，避免畫面誤判為已離職
          let dataWithEmployed = data.map(employee => ({
            ...employee,
            employed: true
          }));
          this.allEmployees = dataWithEmployed;
          this.dataSource.data = dataWithEmployed;
          this.search();
          // MatPaginator中文化
          this.setupPaginator();
        },
        error: (error) => {
          console.error("取得部門員工資料失敗", error);
        }
      });
    }
  }

  // ====== 搜尋與篩選邏輯 ======

  search() {
    let filterData = [...this.allEmployees];
    // 部門篩選
    if (this.searchDepartment != "全部") {
      let code = this.mapDepartmentToCode(this.searchDepartment);
      filterData = filterData.filter(employee => employee.department == code);
    }
    // 職等篩選
    if (this.searchgrade > 0 && this.searchgrade != -1) {
      if (this.searchgrade == 11) {
        filterData = filterData.filter(employee => employee.grade == 11);
      } else if (this.searchgrade == 6) {
        filterData = filterData.filter(employee => employee.grade >= 6 && employee.grade <= 10);
      } else if (this.searchgrade == 1) {
        filterData = filterData.filter(employee => employee.grade >= 1 && employee.grade <= 5);
      }
    }
    // 是否在職
    if (this.searchIsEmployed != null) {
      filterData = filterData.filter(employee => employee.employed == this.searchIsEmployed);
    }
    // 依在職狀態排序
    filterData = filterData.sort((a, b) => a.employed == b.employed ? 0 : a.employed ? -1 : 1);
    // 最終過濾：權限可見的員工
    this.dataSource.data = this.filterVisibleEmployees(filterData);
  }

  /**
   * 員工編號的模糊搜尋
   * @param event 使用者輸入的員工編號
   */
  onEmployeeIdKeyup(event: KeyboardEvent) {
    let keyword = this.searchEmployeeId.trim();
    this.dataSource.data = this.allEmployees.filter(employee =>
      employee.id.toString().includes(keyword)
    );
  }

  /**
   * 姓名的模糊搜尋
   * @param event 使用者輸入的姓名
   */
  onEmployeeNameKeyup(event: KeyboardEvent) {
    let keyword = this.searchEmployeeName.trim();
    this.dataSource.data = this.allEmployees.filter(employee =>
      employee.name.includes(keyword)
    );
  }

  // 權限篩選與顯示控制
  filterVisibleEmployees(employees: Employee[]): Employee[] {
    let role = this.authService.getUserRole();
    let current = this.authService.getUserInfo();
    if (!role || !current) return [];

    return employees.filter(employee => {
      // 總經理、人資主管和會計主管能夠讀取所有員工
      if (role == Role.Boss || role == Role.HRManager || role == Role.AcctManager) return true;
      // 人資員工和會計員工能讀取職等 <= 5 的員工
      if (role == Role.HREmployee || role == Role.AcctEmployee) return employee.grade <= 5;
      // 一般部門主管只能讀取一般部門的員工
      if (role == Role.GAManager) return employee.department == "GA";
      // 一般部門員工只能讀取自己的資料
      if (role == Role.GAEmployee) return employee.id == current.id;
      return false;
    });
  }

  searchDepartmentEventListener(event: Event) {
    this.searchDepartment = (event.target as HTMLSelectElement).value;
    this.search();
  }

  searchGradeEventListener(event: Event) {
    this.searchgrade = Number((event.target as HTMLSelectElement).value);
    this.search();
  }

  searchIsEmployedEventListener(event: Event) {
    let value = (event.target as HTMLSelectElement).value;
    this.searchIsEmployed = value == "true" ? true : value == "false" ? false : null;
    this.search();
  }

  // ====== 權限判斷邏輯 ======

  /**
   * 新增員工的權限
   */
  canCreateEmployee(): boolean {
    let user = this.authService.getUserInfo();
    if (!user) return false;

    let isBoss = user.grade == 11;
    let isHR = user.department == "HR";

    return isBoss || isHR;
  }

  /**
   * 編輯全部員工詳細資訊的權限
   */
  canEditAnyEmployee(): boolean {
    let user = this.authService.getUserInfo();
    if (!user) return false;

    let isBoss = user.grade == 11;
    let isHRManager = user.department == "HR" && user.grade >= 6;
    let isHREmployee = user.department == "HR" && user.grade <= 5;

    return isBoss || isHRManager || isHREmployee;
  }

  /**
   * 編輯員工詳細資訊的權限
   */
  canEdit(employee: Employee): boolean {

    let user = this.authService.getUserInfo();
    if (!user) return false;

    let isBoss = user.grade == 11;
    let isHRManager = user.department == "HR" && user.grade >= 6;
    let isHREmployee = user.department == "HR" && user.grade <= 5;

    // 總經理: 可以編輯其他總經理，但不能編輯自己，也不能改其他部門
    if (isBoss) {
      return true;
    }

    // 人資主管: 除了總經理以外，其他都可以編輯
    if (isHRManager) {
      return employee.grade < 11;
    }

    // 人資員工: 只能更改所有部門的員工
    if (isHREmployee) {
      return employee.grade <= 5;
    }

    // 其他的員工都沒有更改的權限
    return false;
  }

  /**
   * 查看員工詳細資訊的權限
   */
  canViewEmployeeList(): boolean {
    let user = this.authService.getUserInfo();
    if (!user) return false;
    let isGM = user.grade == 11;
    let isHR = user.department == "HR";
    let isGAManager = user.department == "GA" && user.grade >= 6;
    let isAcctEmployee = user.department == "Acct";
    return isGM || isHR || isGAManager || isAcctEmployee;
  }

  /**
   * 查看員工詳細資料的權限
   */
  canViewDetail(): boolean {
    let user = this.authService.getUserInfo();
    // 一般部門主管不可看詳細畫面
    return !(user?.department == "GA" && user?.grade >= 6);
  }

  /**
   * 薪資管理按鈕權限
   * @param employee 可選的員工
   * @returns 是否允許顯示薪資管理按鈕
   */
  canShowSalaryButton(employee?: Employee): boolean {
    const role = this.authService.getUserRole();

    // 沒有傳 employee → 檢查是否為能使用薪資功能的角色（整體權限）
    if (!employee) {
      return role == "Boss" || role == "AcctManager" || role == "AcctEmployee";
    }

    // 傳入了員工 → 檢查是否有權限針對該員工操作
    if (role == "Boss" || role == "AcctManager") {
      return true;
    }

    if (role == "AcctEmployee") {
      // 只能對非主管等級的員工操作
      return employee.grade < 6;
    }

    return false;
  }

  /**
   * 限制下拉選單裡的總經理
   * @returns 總經理的選項
   */
  canShowBossSelect() {
    let user = this.authService.getUserInfo();
    if (!user) return false;

    let Boss = user.grade == 11;
    let isHRManager = user.grade >= 6 && user.department == "HR";
    let isAcctManager = user.grade >= 6 && user.department == "Acct";

    return Boss || isHRManager || isAcctManager
  }

  /**
   * 限制下拉選單裡的人資部門
   * @returns 人資部門的選項
   */
  canShowHRSelect() {
    let user = this.authService.getUserInfo();
    if (!user) return false;

    let Boss = user.grade == 11;
    let isHR = user.department == "HR";
    let isAcct = user.department == "Acct";

    return Boss || isHR || isAcct
  }

  /**
   * 限制下拉選單裡的會計部門
   * @returns 會計部門的選項
   */
  canShowAcctSelect() {
    let user = this.authService.getUserInfo();
    if (!user) return false;

    let Boss = user.grade == 11;
    let isHR = user.department == "HR";
    let isAcct = user.department == "Acct";

    return Boss || isHR || isAcct
  }

  /**
   * 限制下拉選單裡的主管
   * @returns 主管的選項
   */
  canShowManagerSelect() {
    let user = this.authService.getUserInfo();
    if (!user) return false;

    let manager = user.grade >= 6;

    return manager
  }

  // ====== 頁面導向功能 ======

  /**
   * 前往建立員工資料的畫面
   */
  goToCreateEmployee() {
    this.router.navigate(["/employeeCreate"]);
  }

  /**
   * 前往員工詳細資料的畫面
   * @param employee 依照員工id前往並帶入對應的員工資料
   */
  goToDetail(employee: Employee) {
    this.router.navigate(["/employeeDetail", employee.id]);
  }

  /**
   * 前往編輯員工資訊的畫面
   * @param employee 根據員工id前往並帶入對應的員工資料
   * @returns
   */
  goToEmployeeEdit(employee: Employee) {
    let currentUser = this.authService.getUserInfo();
    let canEdit = this.authService.canEditEmployee(employee);

    if (!canEdit) {
      alert("您沒有權限編輯這位員工的資料");
      return;
    }
    this.router.navigate(["/employeeEdit", employee.id]);
  }

  /**
   * 前往薪資管理的畫面
   * @param employee 根據員工id前往並帶入對應的員工資料
   */
  goToSalaryManagement(employee: Employee) {
    this.router.navigate(["/salaryManagement", employee.id]);
  }

  /**
   * 小導覽條-返回首頁
   */
  backToHome() {
    this.router.navigate(["/home"]);
  }

  /**
   * 小導覽條-返回員工清單
   */
  backToEmployeeList() {
    this.router.navigate(["/employeeList"]);
  }

  // ====== 顯示輔助函式 ======

  /**
   * 將部門英文代碼轉換為對應中文名稱
   */
  getDepartmentText(department: string): string {
    return getDepartmentLabel(department);
  }

  /**
   * 根據員工回傳的職等轉換為中文顯示
   */
  getGradeText(employee: Employee): string {
    return getGradeLabel(employee);
  }

  /**
   * 將部門中文名稱轉換為英文代號(用於搜尋篩選)
   */
  mapDepartmentToCode(name: string): string {
    switch (name) {
      case "總經理": return "Boss";
      case "人資部門": return "HR";
      case "會計部門": return "Acct";
      case "一般部門": return "GA";
      default: return name;
    }
  }

  /**
   * 判斷員工是否為目前登入的使用者
   */
  isCurrentUser(employee: Employee): boolean {
    let currentUser = this.authService.getUserInfo();
    return currentUser?.id == employee.id;
  }

  /**
   * 設定 MatPaginator 的中文顯示
   */
  setupPaginator() {
    this.dataSource.paginator!._intl.itemsPerPageLabel = "每頁筆數";
    this.paginator._intl.getRangeLabel = (page, pageSize, length) => {
      if (length == 0 || pageSize == 0) return `共 0 筆`;
      let startIndex = page * pageSize;
      let endIndex = Math.min(startIndex + pageSize, length);
      return `顯示第 ${startIndex + 1} 到 ${endIndex} 筆資料（共 ${length} 筆）`;
    };
  }
}
