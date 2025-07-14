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

  title: string = '';
  description: string = '';
  categoryId: number|null = null;
  categories: { id: number, name: string }[] = [];
  loading = false;
  error = '';
  projects: Project[] = [];

  constructor(
    private dialogRef: MatDialogRef<CreateTicketModalComponent>,
    private ticketService: TicketService,
    private http: HttpClient,
    private projectsService: ProjectsService,
    @Inject(MAT_DIALOG_DATA) public data: { authorId: number }
  ) {
    this.authorId = data.authorId;
  }

  ngOnInit() {
    this.http.get<{ id: number, name: string }[]>('/api/categories').subscribe({
      next: (cats) => this.categories = cats,
      error: () => this.error = 'Erro ao carregar categorias'
    });
    this.projectsService.findAll().subscribe({
      next: (projs) => this.projects = projs,
      error: () => this.error = 'Erro ao carregar projetos'
    });
  }

  close() {
    this.dialogRef.close();
  }

  createTicket() {
    if (!this.title || !this.description || !this.categoryId || !this.projectId || !this.authorId) {
      this.error = 'Preencha todos os campos obrigatórios';
      return;
    }
    this.loading = true;
    const req: CreateTicketRequest = {
      title: this.title,
      description: this.description,
      categoryId: this.categoryId,
      authorId: this.authorId,
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