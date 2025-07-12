import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { Status, StatusService } from './services/status.service';
import { NormalizePipe } from './components/pipes/normalize.pipe';
import { map } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, FormsModule, NormalizePipe],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class AppComponent implements OnInit {
  anyStatus: Status = { id: -1, name: "Todos" };
  title = 'morphoboard';
  searchTerm: string = '';
  statuses: Status[] = [this.anyStatus];
  selectStatus: Status = this.anyStatus;

  constructor(private router: Router, private statusService: StatusService) { }

  onSearchKeydown(event: KeyboardEvent) {
    if (event.key === 'Enter' && this.searchTerm.trim()) {
      this.goToSearch(this.searchTerm.trim(), this.selectStatus);
    }
  }
  
  goToSearch(term: string, status: Status) {
    if (status != this.anyStatus) {
      this.router.navigate(['/search'], { queryParams: { q: term, status: status.id } });
    } else {
      this.router.navigate(['/search'], { queryParams: { q: term } }); 
    }
  }

  ngOnInit(): void {
    this.statusService.findAll()
                     .pipe(map(statuses => [this.anyStatus, ...statuses])) 
                     .subscribe(statuses => this.statuses = statuses)
  }

  onChange(event: Status) {
    this.goToSearch(this.searchTerm.trim(), this.selectStatus);
  }
}
