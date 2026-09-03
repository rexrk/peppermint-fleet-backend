package robotfleetmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import robotfleetmanagement.fleet.FleetState;
import robotfleetmanagement.model.Robot;
import robotfleetmanagement.redis.RedisSnapshotService;

import java.util.Collection;

@RestController
@RequestMapping("/robots/status")
@RequiredArgsConstructor
public class FleetController {

    private final FleetState fleetState;
    private final RedisSnapshotService redisSnapshotService;


    @GetMapping()
    public ResponseEntity<Collection<Robot>> getRobots() {
        return ResponseEntity.ok(fleetState.getAll());
    }

    @GetMapping("/{robotId}")
    public ResponseEntity<Robot> getRobot(@PathVariable String robotId) {

        Robot robot = fleetState.get(robotId);

        if (robot == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(robot);
    }

    @GetMapping("/{robotId}/last-seen")
    public ResponseEntity<Robot> getLastSeen(
            @PathVariable String robotId) {

        Robot robot = redisSnapshotService.get(robotId);

        if (robot == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(robot);
    }
}