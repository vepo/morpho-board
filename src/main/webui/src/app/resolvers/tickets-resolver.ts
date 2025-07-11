import { inject } from '@angular/core';
import { RedirectCommand, ResolveFn, Router } from '@angular/router';
import { Ticket, TicketService } from '../services/ticket.service';

export const ticketsResolver: ResolveFn<Ticket[]> = (route, state) => {
  const projectId = route.paramMap.get('projectId');
  console.log('ticketsResolver: Resolving tickets for project:', projectId);
  if (!projectId) {
    return new RedirectCommand(inject(Router).parseUrl('/'));
  }
  return inject(TicketService).findByProjectId(Number(projectId));
};
