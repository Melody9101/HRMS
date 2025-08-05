export function getDepartmentLabel(department: string): string {
  switch (department) {
    case 'Boss': return '總經理';
    case 'HR': return '人資部門';
    case 'Acct': return '會計部門';
    case 'GA': return '一般部門';
    default: return department;
  }
}
