import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { NotificationService, UserNotification } from '../../services/notification.service';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-notification',
  imports: [MatButtonModule, MatIconModule, MatMenuModule, DatePipe],
  styleUrl: './notification.component.scss',
  templateUrl: './notification.component.html',
  standalone: true,
})
export class NotificationComponent implements OnInit {

  events: UserNotification[] = [];

  constructor(private readonly notificationService: NotificationService,
              private readonly router: Router) {
  }

  ngOnInit(): void {
    this.notificationService.connect();
    this.notificationService.listen()
      .subscribe(event => {
        if (this.events.indexOf(event) == -1) {
          this.events.push(event);
          this.events.sort((a, b) => b.timestamp - a.timestamp);
        }
      });
  }

  eventsUnread(): number {
    return this.events.filter(e => !e.read).length;
  }

  navigate(notification: UserNotification): void {
    console.log('Notification', notification);
    if (!notification.read) {
      this.notificationService.markAsRead(notification.id).subscribe(notification => {
        console.log("Notification updated", notification, this.events)
        this.events = this.events.filter(n => n.id != notification.id);
        this.events.push(notification);
        console.log("Notification updated! done", this.events)
        this.router.navigate(['/', 'ticket', notification.ticketId]);
      });
    } else {
      this.router.navigate(['/', 'ticket', notification.ticketId]);
    }
  }
}
