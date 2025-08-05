import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatDivider, MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { GeneralComponent } from '../../general/general.component';

@Component({
  selector: 'app-reject-reason',
  imports: [
    MatDialogModule,
    MatButtonToggleModule,
    MatSelectModule,
    MatFormFieldModule,
    FormsModule,
    MatInputModule,
    MatDivider,
  ],
  templateUrl: './reject-reason.component.html',
  styleUrl: './reject-reason.component.scss'
})
export class RejectReasonComponent {
  reason!:string;
  constructor(
    public dialogRef: MatDialogRef<RejectReasonComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      title: string;
      options: any[];
      element: any;
      switchStatus: string;
      parentDialog: MatDialogRef<any>;
    },
    private dialog: MatDialog,
  ) { }

  onCancel() {
   
    this.dialogRef.close(false);
  }

  onConfirm(){
     if(!this.reason){
       this.dialog.open(GeneralComponent, {
              data: {
                message: '必須選擇理由才可駁回假單',
                title: '提示',
                showCancel: false,
              },
            });
            return;
    }
    this.dialogRef.close({
    result: true,
    reason: this.reason, // 這裡放你選到的理由
  });
  }
}
