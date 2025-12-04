import { TestBed } from '@angular/core/testing';

import { BackendController } from './backend-controller';

describe('BackendController', () => {
  let service: BackendController;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BackendController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
