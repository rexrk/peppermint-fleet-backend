package robotfleetmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import robotfleetmanagement.fleet.FleetState;
import robotfleetmanagement.model.FleetUpdateEvent;

@Controller
@RequiredArgsConstructor
public class FleetStateWebSocketController {

    public static final String FLEET_STATE_TOPIC = "/topic/fleet-state";

    private final FleetState fleetState;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void publishFleetState(FleetUpdateEvent event) {
        messagingTemplate.convertAndSend(FLEET_STATE_TOPIC, fleetState);
    }

    @MessageMapping("/fleet-state")
    public void publishCurrentFleetState() {
        messagingTemplate.convertAndSend(FLEET_STATE_TOPIC, fleetState);
    }
}
