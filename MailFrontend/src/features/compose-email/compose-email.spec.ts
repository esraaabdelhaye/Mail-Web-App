import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComposeEmail } from './compose-email';

describe('ComposeEmail', () => {
  let component: ComposeEmail;
  let fixture: ComponentFixture<ComposeEmail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComposeEmail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComposeEmail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
