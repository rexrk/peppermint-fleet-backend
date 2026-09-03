package robotfleetmanagement.redis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import robotfleetmanagement.fleet.FleetState;
import robotfleetmanagement.model.Robot;
import robotfleetmanagement.mqtt.MqttPublisher;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisRecoveryService {

    private static final String ROBOT_KEY_PREFIX = "robot:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final FleetState fleetState;
    private final MqttPublisher mqttPublisher;

    @PostConstruct
    public void recover() {

        Set<String> keys = redisTemplate.keys(ROBOT_KEY_PREFIX + "*");

        if (keys == null || keys.isEmpty()) {
            log.info("No robot snapshots found in Redis");
            return;
        }

        for (String key : keys) {
            try {
                String json = redisTemplate.opsForValue().get(key);

                if (json == null) continue;

                Robot robot = objectMapper.readValue(json, Robot.class);
                robot.setStatus(Robot.RobotStatus.OFFLINE);

                fleetState.put(robot);

            } catch (Exception e) {
                log.error("Failed to recover robot from Redis key {}", key, e);

            }
        }

        log.info("Recovered {} robots from Redis", keys.size());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void pingRobotsAfterStartup() {
        log.info("Broadcasting PING to robots");
        mqttPublisher.pingAll();

    }
}