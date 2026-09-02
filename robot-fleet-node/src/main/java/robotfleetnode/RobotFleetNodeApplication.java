package robotfleetnode;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import robotfleetnode.scheduler.JsonlScheduler;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RobotFleetNodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RobotFleetNodeApplication.class, args);
    }

    @Bean
    CommandLineRunner start(JsonlScheduler scheduler) {
        return args -> scheduler.run();
    }

}
