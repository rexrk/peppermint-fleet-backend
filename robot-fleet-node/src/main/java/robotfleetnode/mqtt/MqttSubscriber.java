package robotfleetnode.mqtt;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import robotfleetnode.model.RobotUpdate;
import robotfleetnode.model.RobotUpdateEvent;

/*
    Handles incoming MQTT messages and converts them into application events.
 */
@Component
@RequiredArgsConstructor
public class MqttSubscriber {

    private final ApplicationEventPublisher eventPublisher;

    public void receive(Message<?> message) {

        String payload = message.getPayload().toString();

        if ("PING".equalsIgnoreCase(payload)) {
            eventPublisher.publishEvent(
                    new RobotUpdateEvent(
                            new RobotUpdate(null, null, null, null, null)
                    )
            );
        }
    }
}