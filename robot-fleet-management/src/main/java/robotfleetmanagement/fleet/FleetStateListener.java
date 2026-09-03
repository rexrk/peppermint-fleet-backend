package robotfleetmanagement.fleet;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import robotfleetmanagement.model.FleetUpdateEvent;
import robotfleetmanagement.model.Robot;

@Component
@RequiredArgsConstructor
public class FleetStateListener {

    private final FleetState fleetState;

    @EventListener
    public void handle(FleetUpdateEvent event) {

        Robot incoming = event.robot();
        Robot current = fleetState.get(incoming.getId());

        if (current == null || incoming.getT() > current.getT()) {
            fleetState.put(incoming);
        }
    }
}