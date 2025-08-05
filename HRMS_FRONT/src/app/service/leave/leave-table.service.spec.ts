import { TestBed } from '@angular/core/testing';

import { LeaveTableService } from './leave-table.service';

describe('LeaveTableService', () => {
  let service: LeaveTableService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LeaveTableService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
