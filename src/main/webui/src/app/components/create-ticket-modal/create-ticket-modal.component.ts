import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { TicketService, CreateTicketRequest } from '../../services/ticket.service';
import { HttpClient } from '@angular/common/http';
import { ProjectsService, Project } from '../../services/projects.service';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { Category, CategoryService } from '../../services/category.service';

@Component({
  selector: 'app-create-ticket-modal',
  templateUrl: './create-ticket-modal.component.html',
  styleUrls: ['./create-ticket-modal.component.scss'],
  standalone: true,
  imports: [FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatSelectModule, MatOptionModule]
})
export class CreateTicketModalComponent implements OnInit {
  // @Input() projectId!: number;
  // @Input() authorId!: number;
  projectId: number|null = null;
  authorId!: number;
  emptyCategory: Category = { id: -1, name: "Escolha..." };
  title: string = '';
  description: string = '';
  category: Category = this.emptyCategory;
  categories: Category[] = [];
  loading = false;
  error = '';
  projects: Project[] = [];

  constructor(
    private dialogRef: MatDialogRef<CreateTicketModalComponent>,
    private ticketService: TicketService,
    private categoryService: CategoryService,
    private projectsService: ProjectsService,
    @Inject(MAT_DIALOG_DATA) public data: { authorId: number }
  ) {
    this.authorId = 1;//data.authorId;
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
    this.loading = true;
    const req: CreateTicketRequest = {
      title: this.title,
      description: this.description,
      categoryId: this.category.id,
      authorId: 1,
      projectId: this.projectId
    };
    this.ticketService.createTicket(req).subscribe({
      next: (ticket) => this.dialogRef.close(ticket),
      error: () => {
        this.error = 'Erro ao criar ticket';
        this.loading = false;
      }
    });
  }
} 