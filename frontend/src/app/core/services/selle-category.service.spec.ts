import { TestBed } from '@angular/core/testing';

import { SelleCategoryService } from './selle-category.service';

describe('SelleCategoryService', () => {
  let service: SelleCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SelleCategoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
