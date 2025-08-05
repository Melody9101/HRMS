import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonToggleModule } from '@angular/material/button-toggle';

@Component({
  selector: 'app-general',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonToggleModule
  ],
  templateUrl: './general.component.html',
  styleUrl: './general.component.scss',
})
export class GeneralComponent {

  constructor(
    public dialogRef: MatDialogRef<GeneralComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { message: string, title?: string, showCancel?: boolean }
  ) {
    // 把 data.showCancel 傳進來
    if (data.showCancel == false) {
      this.showCancel = false;
    }
  }
  showCancel: boolean = true;

  onCancel() {
    this.dialogRef.close(false)
  }

  onConfirm() {
    this.dialogRef.close(true)
  }
}
