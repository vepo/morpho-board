import { Injectable, NgZone } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { ServerSideEventsClient } from "./sse.client";

export interface UserNotification {
    id: number;
    type: string;
    read: boolean;
    content: string;
    timestamp: number;
}

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private readonly API_URL: string = 'http://localhost:8080/api/notifications/register';
    private readonly eventSubject = new Subject<UserNotification>();
    constructor(private readonly zone: NgZone, private readonly sseClient: ServerSideEventsClient) { }

    /**
     * Connect to SSE stream with optional channel parameter
     */
    public connect(channel?: string): void {
        // Close eCloseaxisting connection if any
        this.disconnect();
        // Build URL with query parameters
        const url = channel ? `${this.API_URL}?channel=${channel}` : this.API_URL;
        this.sseClient.connect(url).subscribe(data => {
            if (data) {
                console.log(data);
                this.zone.run(() => this.eventSubject.next(data.data as UserNotification));
            }
        })
    }

    /**
     * Listen for all events
     */
    public listen(): Observable<UserNotification> {
        return this.eventSubject.asObservable();
    }

    /**
     * Disconnect from SSE
     */
    public disconnect(): void {
        this.sseClient.close();
    }

    /**
     * Reconnect with optional new channel
     */
    public reconnect(channel?: string): void {
        this.disconnect();
        this.connect(channel);
    }
}