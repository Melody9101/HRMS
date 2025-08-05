import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';


export const leaveReviewGuardsGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const userInfo = authService.getUserInfo();
  const userGrade = userInfo?.grade;

  //確認有等級 並且在6~11間，可進入頁面
  if (userGrade !== undefined && userGrade >= 6 && userGrade <= 11) {
    return true;
  }

  //否則導回主頁面
  router.navigate(['/']);
  return false;

};
