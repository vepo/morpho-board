package dev.vepo.morphoboard.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

@ApplicationScoped
@Path("notifications")
public class NotificationsEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(NotificationsEndpoint.class);

    private final SseBroadcaster sseBroadcaster;
    private final Sse sse;

    @Inject
    public NotificationsEndpoint(@Context Sse sse) {
        this.sse = sse;
        this.sseBroadcaster = sse.newBroadcaster();
    }

    @GET
    @Path("register")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void register(@Context SseEventSink eventSink) {
        sseBroadcaster.register(eventSink);
    }

    public void listenNotifications(@ObservesAsync Notification notification) {
        logger.info("Processing CDI Event! event={}", notification);
        this.sseBroadcaster.broadcast(sse.newEventBuilder()
                                         .mediaType(MediaType.APPLICATION_JSON_TYPE)
                                         .id("ticket-change")
                                         .data(notification)
                                         .build());
    }
}
