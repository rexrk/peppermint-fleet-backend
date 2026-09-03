package robotfleetmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import robotfleetmanagement.config.MqttProperties;

@SpringBootApplication
@EnableConfigurationProperties(MqttProperties.class)
@EnableScheduling
public class RobotFleetManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(RobotFleetManagementApplication.class, args);
    }

}
