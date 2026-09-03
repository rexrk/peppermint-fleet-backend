package robotfleetmanagement.fleet;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import robotfleetmanagement.model.FleetStateUpdatedEvent;

@Component
@RequiredArgsConstructor
public class FleetWebSocketListener {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handle(FleetStateUpdatedEvent event) {

        messagingTemplate.convertAndSend(
                "/topic/robots",
                event.robot()
        );
    }
}