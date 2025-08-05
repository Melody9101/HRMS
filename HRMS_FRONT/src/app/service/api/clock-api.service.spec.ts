import { TestBed } from '@angular/core/testing';

import {ClockApiService } from './clock-api.service';

describe('ClockApiService', () => {
  let service: ClockApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClockApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
