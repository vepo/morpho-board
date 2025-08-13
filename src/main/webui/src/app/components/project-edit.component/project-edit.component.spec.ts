import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectEditComponent } from './project-edit.component';

describe('ProjectEditComponent', () => {
  let component: ProjectEditComponent;
  let fixture: ComponentFixture<ProjectEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectEditComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProjectEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
