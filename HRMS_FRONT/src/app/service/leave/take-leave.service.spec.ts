import { TestBed } from '@angular/core/testing';
import { TakeLeaveService } from './take-leave.service';
describe('TakeLeaveService', () => {
  let service: TakeLeaveService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TakeLeaveService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
