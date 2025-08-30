import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectsService } from '../../services/projects.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { Workflow } from '../../services/workflow.service';
import { JsonPipe } from '@angular/common';

@Component({
  selector: 'app-project-edit.component',
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatSelectModule],
  templateUrl: './project-edit.component.html'
})
export class ProjectEditComponent implements OnInit {
  editMode: boolean = false;
  projectId: number | null = null;
  workflows: Workflow[] = [];
  projectForm = new FormGroup({
    name: new FormControl('', Validators.required),
    description: new FormControl('', []),
    prefix: new FormControl('', [Validators.minLength(3), Validators.maxLength(5), Validators.required]),
    workflow: new FormControl(-1, [Validators.required, Validators.min(1)])
  });


  constructor(private readonly activatedRoute: ActivatedRoute,
    private readonly projectsService: ProjectsService,
    private readonly router: Router) {
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ project, workflows }) => {
      console.debug("Workflows data:", workflows);
      this.workflows = workflows;

      console.debug("Project data: ", project);
      this.editMode = project != null;
      this.projectId = project.id;
      this.projectForm.setValue({
        name: project.name,
        description: project.description,
        prefix: project.prefix,
        workflow: 1
      });
    });
  }


  cancel(): void {
    this.router.navigate(['/', 'projects']);
  }

  save() {
    console.log("Save call!")
    if (this.projectForm.invalid) return;
    const { name, description, prefix } = this.projectForm.value;
    if (!name || !prefix || !description) return;

    if (this.projectId) {
      this.projectsService.update(this.projectId, { name, description, prefix, workflowId: 1 })
        .subscribe(project => this.router.navigate(['/', 'projects']));
    } else {
      this.projectsService.create({ name, description, prefix, workflowId: 1 })
        .subscribe(project => this.router.navigate(['/', 'projects']));
    }
  }
}
