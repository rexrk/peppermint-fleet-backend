package robotfleetnode.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
/*
    Use it to initialize robot once
 */
public class Robot {
    // immutable states
    private String id;
    private RobotType type;

    // mutable states
    private long t;
    private Position position;
    private double battery;
    private RobotStatus status;
    private TaskEvent taskEvent;

    public enum RobotType {
        PICKER,
        HAULER
    }

    public enum RobotStatus {
        IDLE,
        ACTIVE,
        MAINTENANCE,
        OFFLINE,
        BLOCKED,
        ERROR,
        ON_MISSION
    }

    public enum TaskEvent {
        TASK_STARTED,
        TASK_COMPLETED
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Position {
        private double x;
        private double y;
    }
}