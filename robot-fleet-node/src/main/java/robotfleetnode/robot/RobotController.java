package robotfleetnode.robot;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import robotfleetnode.model.Robot;
import robotfleetnode.model.RobotUpdate;
import robotfleetnode.model.RobotUpdateEvent;
import robotfleetnode.mqtt.MqttPublisher;

@Component
@RequiredArgsConstructor
public class RobotController {

    private final RobotManager robotManager;
    private final MqttPublisher mqttPublisher;

    @EventListener
    public void process(RobotUpdateEvent event) {

        try {
            RobotUpdate update = event.update();

            if(update.t() != null)
                robotManager.updateTimeStamp(update.t());

            if (update.battery() != null)
                robotManager.updateBattery(update.battery());

            if (update.x() != null && update.y() != null)
                robotManager.updatePosition(update.x(), update.y());

            if (update.status() != null)
                robotManager.updateStatus(update.status());

            if (update.taskEvent() != null)
                robotManager.updateTaskEvent(update.taskEvent());

        } catch (Exception e) {
            robotManager.updateStatus(Robot.RobotStatus.ERROR);

        } finally {
            mqttPublisher.publish(robotManager.getRobot());
        }
    }
}