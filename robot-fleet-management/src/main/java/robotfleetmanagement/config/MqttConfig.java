package robotfleetmanagement.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.core.ClientManager;
import org.springframework.integration.mqtt.core.Mqttv3ClientManager;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import robotfleetmanagement.mqtt.MqttSubscriber;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class MqttConfig {

    private final MqttProperties mqttProperties;

    // Same Client for both inbound and outbound channels
    @Bean
    public ClientManager<IMqttAsyncClient, MqttConnectOptions> mqttClientManager() {

        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{mqttProperties.getBrokerUrl()});
        options.setCleanSession(mqttProperties.isCleanSession());

        return new Mqttv3ClientManager(
                options,
                mqttProperties.getClientId()
        );
    }

    @Bean
    public IntegrationFlow mqttInbound(
            ClientManager<IMqttAsyncClient, MqttConnectOptions> clientManager,
            MqttSubscriber mqttSubscriber) {

        return IntegrationFlow
                .from(new MqttPahoMessageDrivenChannelAdapter(
                        clientManager,
                        mqttProperties.getTopics().getRobotStatus()
                ))
                .handle(mqttSubscriber::receive)
                .get();
    }

    @Bean
    public IntegrationFlow mqttOutbound(
            ClientManager<IMqttAsyncClient, MqttConnectOptions> clientManager) {

        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(clientManager);
        handler.setDefaultQos(mqttProperties.getQos());

        return IntegrationFlow
                .from(mqttOutboundChannel())
                .handle(handler)
                .get();
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

}