import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { leaveReviewGuardsGuard } from './leave-review-guards.guard';

describe('leaveReviewGuardsGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => leaveReviewGuardsGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
