import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class SnackbarService {

  constructor(private snackBar: MatSnackBar) { }

  success(message: string = '新增成功！') {
    this.snackBar.open(message, '關閉', {
      duration: 3000,
      panelClass: ['snackbar-success'],
      verticalPosition: 'bottom',
    });
  }

  error(message: string = '發生錯誤，請稍後再試') {
    this.snackBar.open(message, '關閉', {
      duration: 4000,
      panelClass: ['snackbar-error'],
      verticalPosition: 'bottom',
    });
  }
}
