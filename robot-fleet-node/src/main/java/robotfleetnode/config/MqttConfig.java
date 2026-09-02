package robotfleetnode.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MqttConfig {

    private final RobotProperties robotProperties;
    private final MqttProperties mqttProperties;

    @Bean
    public IntegrationFlow mqttInbound() {

        String clientId = mqttProperties.getClientId()
                .formatted(robotProperties.getId());

        String commandTopic = mqttProperties.getTopics()
                .getRobotCommand()
                .formatted(robotProperties.getId());

        return IntegrationFlow
                .from(new MqttPahoMessageDrivenChannelAdapter(
                        mqttProperties.getBrokerUrl(),
                        clientId,
                        commandTopic
                ))
                .handle(message -> {
                    log.info("Received: {}", message.getPayload());
                })
                .get();
    }
}