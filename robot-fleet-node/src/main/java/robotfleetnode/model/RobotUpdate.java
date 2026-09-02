package robotfleetnode.model;

public record RobotUpdate(
        Double battery,
        Double x,
        Double y,
        Robot.RobotStatus status,
        Robot.TaskEvent taskEvent
) {}