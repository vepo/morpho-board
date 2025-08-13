import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { AuthService } from '../../services/auth.service';
import { Category, CategoryService } from '../../services/category.service';
import { Project, ProjectsService } from '../../services/projects.service';
import { CreateTicketRequest, TicketService } from '../../services/ticket.service';
import { JsonPipe } from '@angular/common';

@Component({
  selector: 'app-create-ticket-modal',
  templateUrl: './create-ticket-modal.component.html',
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatSelectModule, MatOptionModule , JsonPipe]
})
export class CreateTicketModalComponent implements OnInit {
  authorId!: number;
  categories: Category[] = [];
  projects: Project[] = [];

  ticketForm = new FormGroup({
    title: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(255)]),
    projectId: new FormControl(-1, [Validators.min(1)]),
    description: new FormControl('', [Validators.minLength(5), Validators.maxLength(1200)]),
    categoryId: new FormControl(-1, [Validators.min(1)]),
  });

  constructor(
    private readonly dialogRef: MatDialogRef<CreateTicketModalComponent>,
    private readonly ticketService: TicketService,
    private readonly categoryService: CategoryService,
    private readonly projectsService: ProjectsService,
    private readonly authService: AuthService,
  ) {
    this.authorId = authService.getAuthUserId();
  }

  get title() {
    return this.ticketForm.get('title');
  }

  get description() {
    return this.ticketForm.get('description');
  }

  get categoryId() {
    return this.ticketForm.get('categoryId');
  }

  get projectId() {
    return this.ticketForm.get('projectId');
  }

  ngOnInit() {
    this.categoryService.findAll()
                        .subscribe(categories => this.categories = categories);
    this.projectsService.findAll()
                        .subscribe(projs => this.projects = projs);
  }

  close() {
    this.dialogRef.close();
  }

  createTicket() {
    if (this.ticketForm.invalid) return;
    
    const req: CreateTicketRequest = {
      title: this.ticketForm.value.title!,
      description: this.ticketForm.value.description!,
      categoryId: this.ticketForm.value.categoryId!,
      authorId: this.authorId,
      projectId: this.ticketForm.value.projectId!
    };
    this.ticketService.createTicket(req)
                      .subscribe(ticket => this.dialogRef.close(ticket));
  }
} 