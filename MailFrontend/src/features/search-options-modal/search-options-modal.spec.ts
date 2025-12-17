import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchOptionsModalComponent } from './search-options-modal';

describe('SearchOptionsModal', () => {
  let component: SearchOptionsModalComponent;
  let fixture: ComponentFixture<SearchOptionsModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchOptionsModalComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SearchOptionsModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
