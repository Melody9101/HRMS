import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-reinstatement-dialog',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './reinstatement-dialog.component.html',
  styleUrl: './reinstatement-dialog.component.scss'
})
export class ReinstatementDialogComponent {

  form: FormGroup;

  constructor(
    private formbuilder: FormBuilder,
    private dialogRef: MatDialogRef<ReinstatementDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.form = this.formbuilder.group({
      salaries: [null, [Validators.required, Validators.min(1)]],
      reinstatementDate: [null, Validators.required]
    });
  }

  confirm() {
    if (this.form.valid) {
      // 回傳輸入的薪資與日期
      this.dialogRef.close(this.form.value);
    }
  }

  cancel() {
    this.dialogRef.close(null);
  }

}
