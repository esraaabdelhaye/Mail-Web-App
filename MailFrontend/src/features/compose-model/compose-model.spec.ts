import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComposeModel } from './compose-model';

describe('ComposeModel', () => {
  let component: ComposeModel;
  let fixture: ComponentFixture<ComposeModel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComposeModel]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComposeModel);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
