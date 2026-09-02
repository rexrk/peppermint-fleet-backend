package robotfleetnode.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import robotfleetnode.config.RobotProperties;
import robotfleetnode.model.Robot;
import robotfleetnode.model.RobotUpdate;
import robotfleetnode.model.RobotUpdateEvent;

import java.io.BufferedReader;
import java.io.FileReader;

@Component
@RequiredArgsConstructor
public class JsonlScheduler {

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final RobotProperties robotProperties;

    public void run() {

        try (BufferedReader reader = new BufferedReader(
                new FileReader("/app/data/events.jsonl")
        )) {

            String line;

            while ((line = reader.readLine()) != null) {

                JsonNode node = objectMapper.readTree(line);

                // Each container only simulates its own robot
                if (!robotProperties.getId().equals(node.get("robot_id").asText())) {
                    continue;
                }

                RobotUpdate update = new RobotUpdate(
                        node.has("t")
                                ? node.get("t").asLong() : null,
                        node.has("battery")
                                ? node.get("battery").asDouble() : null,

                        node.has("x")
                                ? node.get("x").asDouble() : null,

                        node.has("y")
                                ? node.get("y").asDouble() : null,

                        node.has("status")
                                ? Robot.RobotStatus.valueOf(
                                node.get("status").asText().toUpperCase()) : null,

                        node.has("task_event")
                                ? Robot.TaskEvent.valueOf(
                                node.get("task_event").asText().toUpperCase()) : null
                );

                // Send event into the robot processing pipeline
                eventPublisher.publishEvent(
                        new RobotUpdateEvent(update)
                );

                // Simulate heartbeat interval
                Thread.sleep(
                        robotProperties.getHeartbeat()
                                .getInterval()
                                .toMillis()
                );
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(
                    "Robot simulation interrupted", e
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to process events.jsonl", e
            );
        }
    }
}