package robotfleetmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class Robot {

    // robot identity
    private String id;
    private RobotType type;

    // current state
    private Position position;
    private double battery;
    private RobotStatus status;
    private TaskEvent taskEvent;

    // timestamp metadata
    private long t;
    private Instant lastSeen;

    public enum RobotType {
        PICKER,
        HAULER
    }

    public enum RobotStatus {
        IDLE,
        ACTIVE,
        ON_MISSION,
        CHARGING,
        BLOCKED,
        ERROR,
        MAINTENANCE,
        OFFLINE
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