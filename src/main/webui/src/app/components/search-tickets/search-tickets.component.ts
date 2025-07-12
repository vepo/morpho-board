import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TicketService, Ticket } from '../../services/ticket.service';

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
  statusId = '-1';

  constructor(private route: ActivatedRoute, private ticketService: TicketService) { }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.term = params['q'] || '';
      this.statusId = params['status'] || '-1';
      if (this.term || this.statusId != '-1') {
        this.searchTickets();
      }
    });
  }

  searchTickets() {
    this.loading = true;
    this.error = '';
    this.ticketService.search(this.term, Number(this.statusId)).subscribe({
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