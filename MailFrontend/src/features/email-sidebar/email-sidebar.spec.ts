import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmailSidebarComponent } from './email-sidebar';

describe('EmailSidebar', () => {
  let component: EmailSidebarComponent;
  let fixture: ComponentFixture<EmailSidebarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmailSidebarComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EmailSidebarComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
