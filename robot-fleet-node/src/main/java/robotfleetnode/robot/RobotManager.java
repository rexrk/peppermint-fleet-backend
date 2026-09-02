package robotfleetnode.robot;

import lombok.Getter;
import org.springframework.stereotype.Component;
import robotfleetnode.config.RobotProperties;
import robotfleetnode.model.Robot;

@Getter
@Component
public class RobotManager {

    private final Robot robot;

    public RobotManager(RobotProperties robotProperties) {
        this.robot = Robot.builder()
                .id(robotProperties.getId())
                .type(Robot.RobotType.valueOf(robotProperties.getType().toUpperCase()))
                .position(new Robot.Position(
                        robotProperties.getStartPosition().getX(),
                        robotProperties.getStartPosition().getY()))
                .battery(robotProperties.getBatteryPercentage())
                .status(Robot.RobotStatus.IDLE)
                .build();

    }

    public void updateTimeStamp(long t) { robot.setT(t); }

    public void updateBattery(double battery) {
        robot.setBattery(battery);
    }

    public void updatePosition(double x, double y) {
        robot.getPosition().setX(x);
        robot.getPosition().setY(y);
    }

    public void updateStatus(Robot.RobotStatus status) {
        robot.setStatus(status);
    }

    public void updateTaskEvent(Robot.TaskEvent event) {
        robot.setTaskEvent(event);
    }
}