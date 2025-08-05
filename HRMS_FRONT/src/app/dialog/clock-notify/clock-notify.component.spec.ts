import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClockNotifyComponent } from './clock-notify.component';

describe('ClockNotifyComponent', () => {
  let component: ClockNotifyComponent;
  let fixture: ComponentFixture<ClockNotifyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClockNotifyComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClockNotifyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
