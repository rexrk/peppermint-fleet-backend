package robotfleetmanagement.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import robotfleetmanagement.model.Robot;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RedisSnapshotService {

    private static final String ROBOT_KEY_PREFIX = "robot:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public Robot get(String robotId) {
        String json = redisTemplate.opsForValue()
                .get(ROBOT_KEY_PREFIX + robotId);

        if (json == null) return null;

        try {
            return objectMapper.readValue(json, Robot.class);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to read robot " + robotId + " from Redis",
                    e
            );
        }
    }

    public void save(Robot robot) {
        try {
            String json = objectMapper.writeValueAsString(robot);

            redisTemplate.opsForValue().set(
                    ROBOT_KEY_PREFIX + robot.getId(),
                    json
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to save robot " + robot.getId() + " to Redis",
                    e
            );
        }
    }
}