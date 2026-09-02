package robotfleetnode.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;

@ConfigurationProperties(prefix = "robot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RobotProperties {

    private String mac;
    private String id;
    private String type;
    private StartPosition startPosition;
    private Heartbeat heartbeat;
    private int batteryPercentage;

    @Setter
    @Getter
    public static class StartPosition {

        private double x;
        private double y;

    }

    @Setter
    @Getter
    public static class Heartbeat {
        private Duration interval;

    }
}