package robotfleetmanagement.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import robotfleetmanagement.fleet.FleetState;
import robotfleetmanagement.model.Robot;
import robotfleetmanagement.mqtt.MqttPublisher;
import robotfleetmanagement.redis.RedisSnapshotService;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class RobotHealthScheduler {

    private final FleetState fleetState;
    private final MqttPublisher mqttPublisher;
    private final RedisSnapshotService redisSnapshotService;

    @Scheduled(fixedDelay = 5000)
    public void checkRobotHealth() {

        Instant now = Instant.now();

        fleetState.getAll().forEach(robot -> {
            if (robot.getStatus() == Robot.RobotStatus.OFFLINE) return;

            Instant lastSeen = robot.getLastSeen();

            if (lastSeen == null) {
                mqttPublisher.ping(robot.getId());
                return;
            }

            long seconds = Duration.between(lastSeen, now).getSeconds();

            if (seconds > 30) {
                if (robot.getT() >= 0) {
                    robot.setT(-1);
                    redisSnapshotService.save(robot);

                    robot.setStatus(Robot.RobotStatus.OFFLINE);
                    fleetState.put(robot);
                }
            }
            else if (seconds >= 10) mqttPublisher.ping(robot.getId());

        });
    }
}