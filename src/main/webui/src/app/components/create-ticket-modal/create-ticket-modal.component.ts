import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
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

@Component({
  selector: 'app-create-ticket-modal',
  templateUrl: './create-ticket-modal.component.html',
  styleUrls: ['./create-ticket-modal.component.scss'],
  standalone: true,
  imports: [FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatSelectModule, MatOptionModule]
})
export class CreateTicketModalComponent implements OnInit {
  projectId: number|null = null;
  authorId!: number;
  emptyCategory: Category = { id: -1, name: "Escolha..." };
  title: string = '';
  description: string = '';
  category: Category = this.emptyCategory;
  categories: Category[] = [];
  error = '';
  projects: Project[] = [];

  constructor(
    private readonly dialogRef: MatDialogRef<CreateTicketModalComponent>,
    private readonly ticketService: TicketService,
    private readonly categoryService: CategoryService,
    private readonly projectsService: ProjectsService,
    private readonly authService: AuthService,
  ) {
    this.authorId = authService.getAuthUserId();
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
    if (!this.title || !this.description || !this.category || !this.projectId || !this.authorId) {
      this.error = 'Preencha todos os campos obrigatórios';
      return;
    }
    const req: CreateTicketRequest = {
      title: this.title,
      description: this.description,
      categoryId: this.category.id,
      authorId: this.authorId,
      projectId: this.projectId
    };
    this.ticketService.createTicket(req)
                      .subscribe(ticket => this.dialogRef.close(ticket));
  }
} 