import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplementCertificateComponent } from './supplement-certificate.component';

describe('SupplementCertificateComponent', () => {
  let component: SupplementCertificateComponent;
  let fixture: ComponentFixture<SupplementCertificateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SupplementCertificateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SupplementCertificateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
