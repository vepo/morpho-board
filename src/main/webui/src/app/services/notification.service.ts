import { Injectable, NgZone } from "@angular/core";
import { Observable, Subject } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private readonly API_URL: string = 'http://localhost:8080/api/notifications/register';
    private eventSource: EventSource | null = null;
    private eventSubject = new Subject<any>();
    private connectionSubject = new Subject<boolean>();

    constructor(private zone: NgZone) { }

    /**
     * Connect to SSE stream with optional channel parameter
     */
    public connect(channel?: string): void {
        // Close existing connection if any
        this.disconnect();

        // Build URL with query parameters
        const url = channel ? `${this.API_URL}?channel=${channel}` : this.API_URL;

        this.eventSource = new EventSource(url);

        // Listen for messages
        this.eventSource.onmessage = (event) => {
            this.zone.run(() => {
                try {
                    console.log(event.data);
                    const data = event.data; // JSON.parse(event.data);
                    this.eventSubject.next(data);
                } catch (error) {
                    console.error('Error parsing SSE data:', error);
                    this.eventSubject.next({ type: 'error', data: event.data });
                }
            });
        };

        // Listen for custom events (if your server sends named events)
        this.eventSource.addEventListener('ticket-changed', (event: any) => {
            this.zone.run(() => {
                try {
                    console.log(event);
                    const data =  event.data; //JSON.parse(event.data);
                    this.eventSubject.next({ ...data, type: 'ticket-changed' });
                } catch (error) {
                    console.error('Error parsing user-registered event:', error);
                }
            });
        });

        // Handle connection open
        this.eventSource.onopen = () => {
            this.zone.run(() => {
                console.log('SSE connection established');
                this.connectionSubject.next(true);
            });
        };

        // Handle errors
        this.eventSource.onerror = (error) => {
            this.zone.run(() => {
                console.error('SSE connection error:', error);
                this.connectionSubject.next(false);
                this.eventSubject.next({
                    type: 'connection-error',
                    data: 'Connection failed or closed'
                });

                // Attempt reconnect after delay
                setTimeout(() => this.connect(channel), 3000);
            });
        };
    }

    /**
     * Listen for all events
     */
    public listen(): Observable<any> {
        return this.eventSubject.asObservable();
    }

    /**
     * Get connection status observable
     */
    public connectionStatus(): Observable<boolean> {
        return this.connectionSubject.asObservable();
    }

    /**
     * Check if connected
     */
    public isConnected(): boolean {
        return this.eventSource?.readyState === EventSource.OPEN;
    }

    /**
     * Disconnect from SSE
     */
    public disconnect(): void {
        if (this.eventSource) {
            this.eventSource.close();
            this.eventSource = null;
            this.connectionSubject.next(false);
        }
    }

    /**
     * Reconnect with optional new channel
     */
    public reconnect(channel?: string): void {
        this.disconnect();
        this.connect(channel);
    }
}