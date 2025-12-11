import { TestBed } from '@angular/core/testing';

import { ContactsHandler } from './contacts-handler';

describe('ContactsHandler', () => {
  let service: ContactsHandler;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContactsHandler);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
