import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FunctionCardComponent } from './function-card.component';

describe('FunctionCardComponent', () => {
  let component: FunctionCardComponent;
  let fixture: ComponentFixture<FunctionCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FunctionCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FunctionCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
