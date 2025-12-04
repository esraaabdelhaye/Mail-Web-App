import { TestBed } from '@angular/core/testing';

import { EmailHandler } from './email-handler';

describe('EmailHandler', () => {
  let service: EmailHandler;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EmailHandler);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
