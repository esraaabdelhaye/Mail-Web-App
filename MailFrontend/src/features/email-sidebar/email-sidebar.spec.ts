import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmailSidebar } from './email-sidebar';

describe('EmailSidebar', () => {
  let component: EmailSidebar;
  let fixture: ComponentFixture<EmailSidebar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmailSidebar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmailSidebar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
