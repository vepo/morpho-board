import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  imports: [CommonModule, RouterModule],
  standalone: true
})
export class HomeComponent {
  constructor(private router: Router) {}

  navigateToKanban() {
    this.router.navigate(['/kanban/1']);
  }
} 