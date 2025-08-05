import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeaveFlowComponent } from './leave-flow.component';
import { ConfirmDialogComponent } from '../../confirm-dialog/confirm-dialog.component';

describe('LeaveFlowComponent', () => {
  let component: LeaveFlowComponent;
  let fixture: ComponentFixture<LeaveFlowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LeaveFlowComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LeaveFlowComponent);})})

    describe('ConfirmDialogComponent', () => {
  let component: ConfirmDialogComponent;
  let fixture: ComponentFixture<ConfirmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfirmDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
