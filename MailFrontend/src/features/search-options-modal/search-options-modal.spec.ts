import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchOptionsModal } from './search-options-modal';

describe('SearchOptionsModal', () => {
  let component: SearchOptionsModal;
  let fixture: ComponentFixture<SearchOptionsModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchOptionsModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchOptionsModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
