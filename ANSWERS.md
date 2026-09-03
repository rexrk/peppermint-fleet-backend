# Answers

## 1. What holds the fleet's current state in your backend, and why that shape, given it has to serve both the WebSocket stream and the polling endpoint consistently?

The fleet's current state is held in the `FleetState` component (`FleetState.java`), which stores robots in a `ConcurrentHashMap<String, Robot>`, keyed by robot ID. This gives constant-time lookup for individual robots while also allowing the complete collection to be returned for the polling endpoint. Incoming MQTT updates are processed by `FleetStateListener`, which only replaces the stored robot when the update is newer based on its event timestamp `t`.

Both REST and WebSocket use this same in-memory state as the source of truth. `RobotController` reads from `FleetState` for `/robots/status` and `/robots/status/{robotId}`, while `FleetStateListener` publishes a `FleetStateUpdatedEvent` only after an update has been accepted. `FleetWebSocketListener` then broadcasts that accepted robot update to `/topic/robots`. This keeps REST and WebSocket consistent instead of maintaining separate states for each interface.

## 2. Name one real tradeoff you made: the mechanism you chose for robots to reach your backend, its delivery guarantees, and how you reconcile that mechanism's semantics with your WebSocket fanout. Argue for the decision, including its cost.

I chose MQTT for robot-to-backend communication, using Mosquitto as the broker. The robot status topics use MQTT QoS 1, giving at-least-once delivery. This is useful for handling flaky robot connections because messages can be delivered again after reconnects, but it also means duplicate or stale messages are possible. To handle this, `MqttSubscriber` converts incoming messages into `FleetUpdateEvent`, and `FleetStateListener` only accepts an update when its `t` value is newer than the currently stored value.

The WebSocket layer does not directly fan out every MQTT message. `FleetWebSocketListener` only receives `FleetStateUpdatedEvent`, which is published after `FleetStateListener` accepts an update. This prevents duplicate or out-of-order MQTT messages from being propagated to dashboard clients. The tradeoff is the additional Mosquitto broker and the complexity of handling delivery semantics, but it gives the robot communication layer better resilience than relying on a direct WebSocket connection from each robot.

## 3. What did you leave out, and what would you build next given more time?

I intentionally left out persistent robot history. The current Redis integration stores only the latest snapshot for recovery. Given more time, I would persist the history of meaningful state changes at the point where FleetStateListener accepts an update, rather than storing every incoming MQTT message, and expose it through /robots/history/{robotId}.

I would also replace the scripted mock events with a real robot integration for end-to-end testing. Instead of replaying events.jsonl, the robot would publish telemetry generated from its actual sensors, allowing the system to be tested against real movement, battery readings, connection loss, and changing robot states. Finally, if the backend were scaled horizontally, consistency and idempotency would become a key concern because FleetState is currently local to one instance. I would move state/update coordination to a shared store such as Redis and ensure that WebSocket fanout occurs only after an update has been accepted by the shared state layer.

I would also add automated unit and integration tests around the state-transition logic, MQTT reconnects, stale/out-of-order updates, Redis recovery, and offline/online transitions. During this assignment I manually tested each of these flows end-to-end.

![Architecture Diagram](Design Notes/f1747ad2-a815-4eb0-aa6e-5f742b7b52fa.png)