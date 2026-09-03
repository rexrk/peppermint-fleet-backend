package robotfleetmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import robotfleetmanagement.fleet.FleetState;
import robotfleetmanagement.model.Robot;

import java.util.Collection;

@Controller
@RequiredArgsConstructor
public class FleetWebSocketController {

    private final FleetState fleetState;

    @SubscribeMapping("/robots")
    public Collection<Robot> getInitialFleetState() {
        return fleetState.getAll();
    }
}