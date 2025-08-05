import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompanyInfoEditComponent } from './company-info-edit.component';

describe('CompanyInfoEditComponent', () => {
  let component: CompanyInfoEditComponent;
  let fixture: ComponentFixture<CompanyInfoEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompanyInfoEditComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompanyInfoEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
