import { ComponentFixture, TestBed } from '@angular/core/testing';

import { YigitComponent } from './yigit.component';

describe('YigitComponent', () => {
  let component: YigitComponent;
  let fixture: ComponentFixture<YigitComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [YigitComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(YigitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
