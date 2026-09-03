package robotfleetmanagement.mqtt;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import robotfleetmanagement.config.MqttProperties;

@Component
@RequiredArgsConstructor
public class MqttPublisher {
    private final MessageChannel mqttOutboundChannel;
    private final MqttProperties mqttProperties;

    public void ping(String robotId) {
        String topic = mqttProperties.getTopics()
                .getRobotCommand()
                .formatted(robotId);

        publish(topic, "PING");
    }

    public void pingAll() {

        String topic = mqttProperties.getTopics()
                .getRobotCommand()
                .formatted("broadcast");

        publish(topic, "PING");
    }


    private void publish(String topic, String payload) {
        mqttOutboundChannel.send(
                MessageBuilder.withPayload(payload)
                        .setHeader("mqtt_topic", topic)
                        .build()
        );

    }
}