package robotfleetmanagement.mqtt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import robotfleetmanagement.model.FleetUpdateEvent;
import robotfleetmanagement.model.Robot;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class MqttSubscriber {

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    public void receive(Message<?> message) {
        String payload = message.getPayload().toString();
        try {
            Robot robot = objectMapper.readValue(payload, Robot.class);
            robot.setLastSeen(Instant.now());

            log.info(
                    "Received robot update: {} status={} battery={}",
                    robot.getId(),
                    robot.getStatus(),
                    robot.getBattery()
            );

            eventPublisher.publishEvent(new FleetUpdateEvent(robot));

        } catch (Exception e) {
            log.error("Failed to process robot MQTT message: {}", payload, e);
        }
    }
}