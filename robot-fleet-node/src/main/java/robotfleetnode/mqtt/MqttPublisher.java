package robotfleetnode.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import robotfleetnode.model.Robot;

@Component
@RequiredArgsConstructor
public class MqttPublisher {
    private final MessageChannel mqttOutboundChannel;
    private final ObjectMapper objectMapper;

    public void publish(Robot robot) {

        try {
            String payload = objectMapper.writeValueAsString(robot);

            mqttOutboundChannel.send(
                    MessageBuilder
                            .withPayload(payload)
                            .build()
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize robot state", e);
        }
    }
}