import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmClockRecordComponent } from './confirm-clock-record.component';

describe('ConfirmClockRecordComponent', () => {
  let component: ConfirmClockRecordComponent;
  let fixture: ComponentFixture<ConfirmClockRecordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmClockRecordComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfirmClockRecordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
