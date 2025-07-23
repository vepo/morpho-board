import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TicketExpanded, TicketService } from '../../services/ticket.service';
import { DatePipe } from '@angular/common';
import { NormalizePipe } from '../pipes/normalize.pipe';

@Component({
  selector: 'app-ticket-view',
  templateUrl: './ticket-view.component.html',
  styleUrls: ['./ticket-view.component.scss'],
  imports: [DatePipe, NormalizePipe]
})
export class TicketViewComponent implements OnInit {
  ticket?: TicketExpanded;

  constructor(private route: ActivatedRoute, private ticketService: TicketService) { }

  ngOnInit(): void {
    this.route.data.subscribe(({ ticket }) => this.ticket = ticket);
  }
} 