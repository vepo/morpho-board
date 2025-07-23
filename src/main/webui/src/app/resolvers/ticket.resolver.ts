import { inject } from '@angular/core';
import { RedirectCommand, ResolveFn, Router } from '@angular/router';
import { TicketExpanded, TicketService } from '../services/ticket.service';

export const ticketResolver: ResolveFn<TicketExpanded> = (route, state) => {
  const ticketId = route.paramMap.get('ticketId');
  console.log('ticketResolver: Resolving ticket with id:', ticketId);
  if (!ticketId) {
    return new RedirectCommand(inject(Router).parseUrl('/'));
  }
  return inject(TicketService).findExpandedById(Number(ticketId));
};
