import { Injectable, inject } from "@angular/core";
import { AuthService } from "./auth.service";
import { EMPTY, Observable, Subject } from "rxjs";

export interface ServerSideEvent {
    id: string;
    data: any;
}

@Injectable({
    providedIn: 'root'
})
export class ServerSideEventsClient {
    private readonly authService = inject(AuthService);


    private open: boolean = true;
    private readonly connectionSubject = new Subject<boolean>();
    private readonly dataSubject = new Subject<ServerSideEvent>();
    private currentEvent: ServerSideEvent | null = null;
    private contentType: string | null = null;

    connect(url: string): Observable<ServerSideEvent> {
        const token = this.authService.getToken();
        if (!token) return EMPTY;
        this.connectWithFetchAPI(url, token);
        this.connectionSubject.subscribe(closed => {
            if (closed && this.open) {
                this.connectWithFetchAPI(url, token);
            }
        });
        return this.dataSubject.asObservable();
    }

    private async connectWithFetchAPI(url: string, token: string) {
        try {
            const response = await fetch(url, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Accept': 'text/event-stream'
                }
            });

            if (!response.ok) {
                throw new Error(`SSE connection failed: ${response.status}`);
            }

            const reader = response.body?.getReader();
            const decoder = new TextDecoder();

            if (reader) {
                while (true) {
                    const { value, done } = await reader.read();
                    if (done) break;

                    const chunk = decoder.decode(value);
                    // Parse SSE events from chunk and emit them
                    this.parseAndEmitSSEEvents(chunk);
                }
            }
        } catch (error) {
            console.error('Fetch-based SSE connection failed:', error);
            this.connectionSubject.next(false);
        }
    }

    private parseAndEmitSSEEvents(data: string): void {
        // Implement SSE event parsing logic here
        // This is a simplified example
        const lines = data.split('\n');

        if (!this.currentEvent) {
            this.currentEvent = { id: '', data: null };
        }

        for (const line of lines) {
            if (line.startsWith('content-type:')) {
                this.contentType = line.substring(13).trim();
            } else if (line.startsWith('id:')) {
                this.currentEvent.id = line.substring(3).trim();
            } else if (line.startsWith('data:')) {
                try {
                    switch (this.contentType) {
                        // maybe others
                        case 'application/json':
                        default:
                            this.currentEvent.data = JSON.parse(line.substring(5).trim());
                            break;
                    }
                    this.flush();
                } catch (e) {
                    console.error('Error parsing SSE data:', e);
                }
            }

           this.flush();
        }
    }

    private flush() {
        if (this.currentEvent && this.currentEvent.data && Object.keys(this.currentEvent.data).length > 0) {
            this.dataSubject.next(this.currentEvent);
            this.currentEvent = { id: '', data: null };
        }
    }

    close(): void {
        this.open = false;
    }
}