import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddClockRecordComponent } from './add-clock-record.component';

describe('AddClockRecordComponent', () => {
  let component: AddClockRecordComponent;
  let fixture: ComponentFixture<AddClockRecordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddClockRecordComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddClockRecordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
