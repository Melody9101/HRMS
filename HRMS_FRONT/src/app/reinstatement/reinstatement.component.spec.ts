import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReinstatementComponent } from './reinstatement.component';

describe('ReinstatementComponent', () => {
  let component: ReinstatementComponent;
  let fixture: ComponentFixture<ReinstatementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReinstatementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReinstatementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
