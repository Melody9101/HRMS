import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmApplyLeaveComponent } from './confirm-apply-leave.component';

describe('ConfirmApplyLeaveComponent', () => {
  let component: ConfirmApplyLeaveComponent;
  let fixture: ComponentFixture<ConfirmApplyLeaveComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmApplyLeaveComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfirmApplyLeaveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
