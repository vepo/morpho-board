import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { map } from 'rxjs';
import { CreateTicketModalComponent } from './components/create-ticket-modal/create-ticket-modal.component';
import { NormalizePipe } from './components/pipes/normalize.pipe';
import { AuthService } from './services/auth.service';
import { Status, StatusService } from './services/status.service';
import { RoleDirective } from './directives/role.directive';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, FormsModule, NormalizePipe, MatButtonModule, MatDialogModule,
    MatIconModule, MatMenuModule, RoleDirective],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class AppComponent implements OnInit {
  anyStatus: Status = { id: -1, name: "Todos" };
  title = 'morphoboard';
  searchTerm: string = '';
  statuses: Status[] = [this.anyStatus];
  selectStatus: Status = this.anyStatus;

  constructor(private router: Router, 
              private statusService: StatusService, 
              private dialog: MatDialog, 
              private route: ActivatedRoute,
              private authService: AuthService) { }

  onSearchKeydown(event: KeyboardEvent) {
    this.goToSearch(this.searchTerm.trim(), this.selectStatus); 
  }
  
  goToSearch(term: string, status: Status) {
    let params: any = {};

    if (status != this.anyStatus) {
      params['status'] = status.id;
    }

    if (term && term.trim().length > 0) {
      params['q'] = term;
    }

    this.router.navigate(['/search'], { queryParams: params });
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.searchTerm = params['q'] || this.searchTerm;
      let statusId = Number(params['status'] || this.selectStatus.id);
      this.statusService.findAll()
                        .pipe(map(statuses => [this.anyStatus, ...statuses]))
                        .subscribe(statuses => {
                                       this.statuses = statuses;
                                       this.selectStatus = this.statuses.find(s => s.id == statusId) || this.anyStatus;
                                   });
    });
  }

  onChange(event: Status) {
    this.goToSearch(this.searchTerm.trim(), this.selectStatus);
  }

  openCreateTicketDialog() {
    let projectId: number | undefined;
    // Tenta extrair projectId da rota se estiver em /kanban/:projectId
    const match = this.router.url.match(/kanban\/(\d+)/);
    if (match) {
      projectId = Number(match[1]);
    }
    const authorId = 1; // TODO: trocar para usuário autenticado
    this.dialog.open(CreateTicketModalComponent, {
      width: '500px',
      disableClose: true,
      data: { projectId, authorId }
    });
  }

  isAuthenticated(): boolean {
    return this.authService.isLoggedIn();
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
