import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TicketService, Ticket } from '../services/ticket.service';

@Component({
  selector: 'app-search-tickets',
  templateUrl: './search-tickets.component.html',
  styleUrls: ['./search-tickets.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class SearchTicketsComponent implements OnInit {
  tickets: Ticket[] = [];
  loading = false;
  error = '';
  term = '';

  constructor(private route: ActivatedRoute, private ticketService: TicketService) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.term = params['q'] || '';
      if (this.term) {
        this.searchTickets(this.term);
      }
    });
  }

  searchTickets(term: string) {
    this.loading = true;
    this.error = '';
    this.ticketService.search(term).subscribe({
      next: (tickets) => {
        this.tickets = tickets;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao buscar tickets';
        this.loading = false;
      }
    });
  }
} 