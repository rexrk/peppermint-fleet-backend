package robotfleetnode.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.core.ClientManager;
import org.springframework.integration.mqtt.core.Mqttv3ClientManager;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MqttConfig {

    private final RobotProperties robotProperties;
    private final MqttProperties mqttProperties;

    // Same Client for both inbound and outbound channels
    @Bean
    public ClientManager<IMqttAsyncClient, MqttConnectOptions> mqttClientManager() {

        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(
                new String[]{mqttProperties.getBrokerUrl()}
        );

        options.setCleanSession(mqttProperties.isCleanSession());

        return new Mqttv3ClientManager(
                options,
                mqttProperties.getClientId()
                        .formatted(robotProperties.getId())
        );
    }

    @Bean
    public IntegrationFlow mqttInbound(
            ClientManager<IMqttAsyncClient, MqttConnectOptions> clientManager) {

        String commandTopic = mqttProperties.getTopics()
                .getRobotCommand()
                .formatted(robotProperties.getId());

        return IntegrationFlow
                .from(new MqttPahoMessageDrivenChannelAdapter(
                        clientManager,
                        commandTopic
                ))
                .handle(message -> {
                    log.info("Received: {}", message.getPayload());
                })
                .get();
    }

    @Bean
    public IntegrationFlow mqttOutbound(
            ClientManager<IMqttAsyncClient, MqttConnectOptions> clientManager) {

        String statusTopic = mqttProperties.getTopics()
                .getRobotStatus()
                .formatted(robotProperties.getId());

        MqttPahoMessageHandler handler =
                new MqttPahoMessageHandler(clientManager);

        handler.setDefaultTopic(statusTopic);
        handler.setDefaultQos(mqttProperties.getQos());

        return IntegrationFlow
                .from("mqttOutboundChannel")
                .handle(handler)
                .get();
    }

    /* Outbounnd test
    @Bean
    CommandLineRunner testOutbound(MessageChannel mqttOutboundChannel) {
        return args -> mqttOutboundChannel.send(
                MessageBuilder.withPayload(
                        "{\"status\":\"R1 ONLINE\"}"
                ).build()
        );
    }

     */
}