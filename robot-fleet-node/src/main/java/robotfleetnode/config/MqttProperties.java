package robotfleetnode.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mqtt")
@Setter
@Getter
public class MqttProperties {

    private String brokerUrl;
    private String clientId;
    private Topics topics;
    private int qos;
    private boolean cleanSession;

    @Setter
    @Getter
    public static class Topics {

        private String robotStatus;
        private String robotCommand;

    }
}