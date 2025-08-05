import { TestBed } from '@angular/core/testing';

import { ForgotPasswordApiService } from './forgot-password-api.service';

describe('ForgotPasswordService', () => {
  let service: ForgotPasswordApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ForgotPasswordApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
