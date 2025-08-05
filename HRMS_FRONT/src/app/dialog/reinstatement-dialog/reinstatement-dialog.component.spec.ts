import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReinstatementDialogComponent } from './reinstatement-dialog.component';

describe('ReinstatementDialogComponent', () => {
  let component: ReinstatementDialogComponent;
  let fixture: ComponentFixture<ReinstatementDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReinstatementDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReinstatementDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
