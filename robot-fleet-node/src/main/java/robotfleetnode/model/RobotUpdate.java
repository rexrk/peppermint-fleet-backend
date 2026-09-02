package robotfleetnode.model;

public record RobotUpdate(
        Long t,
        Double battery,
        Double x,
        Double y,
        Robot.RobotStatus status,
        Robot.TaskEvent taskEvent
) {}