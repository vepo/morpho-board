import { inject } from '@angular/core';
import { RedirectCommand, ResolveFn, Router } from '@angular/router';
import { TicketExpanded, TicketService } from '../services/ticket.service';

export const ticketResolver: ResolveFn<TicketExpanded> = (route, state) => {
  const ticketIdentifier = route.paramMap.get('ticketIdentifier');
  console.log('ticketResolver: Resolving ticket with identifier:', ticketIdentifier);
  if (!ticketIdentifier) {
    return new RedirectCommand(inject(Router).parseUrl('/'));
  }
  return inject(TicketService).findExpandedByIdentifier(ticketIdentifier);
};
