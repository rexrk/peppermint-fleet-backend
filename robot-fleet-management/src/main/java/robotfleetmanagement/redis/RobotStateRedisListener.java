package robotfleetmanagement.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import robotfleetmanagement.model.FleetUpdateEvent;
import robotfleetmanagement.model.Robot;

@Component
@RequiredArgsConstructor
@Slf4j
public class RobotStateRedisListener {

    private final RedisSnapshotService redisSnapshotService;

    @EventListener
    public void handle(FleetUpdateEvent event) {
        Robot incoming = event.robot();

        try {
            Robot existing = redisSnapshotService.get(incoming.getId());

            if (existing == null || incoming.getT() > existing.getT()) {
                redisSnapshotService.save(incoming);
            }

        } catch (Exception e) {
            log.error(
                    "Failed to persist robot {} state to Redis",
                    incoming.getId(),
                    e
            );
        }
    }
}