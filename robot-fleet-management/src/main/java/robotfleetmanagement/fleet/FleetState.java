package robotfleetmanagement.fleet;

import org.springframework.stereotype.Component;
import robotfleetmanagement.model.Robot;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FleetState {

    private final ConcurrentHashMap<String, Robot> robots =
            new ConcurrentHashMap<>();

    public void put(Robot robot) {
        robots.put(robot.getId(), robot);
    }

    public Robot get(String id) {
        return robots.get(id);
    }

    public Collection<Robot> getAll() {
        return robots.values();
    }

}