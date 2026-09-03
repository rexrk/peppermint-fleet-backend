# Fleet Management Dashboard – Backend

## Overview

A backend service for monitoring a fleet of autonomous robots in real time.

The system receives robot telemetry over MQTT, maintains the latest fleet state, exposes the state through REST APIs, and pushes state changes to connected dashboard clients over STOMP/WebSocket.

The project also includes a mock robot service that replays the provided `events.jsonl` data for all eight robots.

## Architecture

                  ┌──────────────────────┐
                  │    Frontend Client   │
                  │     Web Dashboard    │
                  └───────┬───────┬──────┘
                          │       │
                     REST │       │ WebSocket / STOMP
                          │       │ /topic/robots
                          ▼       ▼
              ┌────────────────────────────┐
              │       Fleet Backend        │
              │         Spring Boot        │
              │                            │
              │  MqttSubscriber            │
              │        ↓                   │
              │  FleetState                │
              │        ↓                   │
              │  FleetStateListener        │
              │     ↙         ↘           │
              │  Health      WebSocket     │
              │ Scheduler     Listener     │
              │                            │
              │      RobotController       │
              └───────┬──────────┬─────────┘
                      │          │
                 Redis│          │MQTT
                      ▼          ▲
               ┌──────────┐      │
               │  Redis   │      │
               │ Snapshot │      │
               └──────────┘      │
                                 │
                         ┌───────┴────────┐
                         │   Mosquitto    │
                         │   MQTT Broker  │
                         └───────┬────────┘
                                 │
               ┌─────────────────┼─────────────────┐
               │                 │                 │
               ▼                 ▼                 ▼
          ┌─────────┐       ┌─────────┐       ┌─────────┐
          │ Robot 1 │       │ Robot 2 │  ...  │ Robot 8 │
          │ Service │       │ Service │       │ Service │
          └─────────┘       └─────────┘       └─────────┘

### Main components

* `MqttSubscriber` receives robot telemetry from MQTT and converts it into `FleetUpdateEvent`.
* `FleetState` maintains the current in-memory state using a `ConcurrentHashMap`, keyed by robot ID.
* `FleetStateListener` accepts only newer robot updates based on the robot event timestamp `t`.
* `RobotHealthScheduler` monitors `lastSeen`, sends PING requests for unresponsive robots, and marks robots offline after the timeout.
* `RobotStateRedisListener` stores the latest robot snapshot for recovery after a backend restart.
* `FleetWebSocketListener` broadcasts accepted state changes to `/topic/robots`.
* `RobotController` exposes the current fleet state through REST APIs.

## Running the Project

The complete system can be started using Docker Compose:

```bash
docker compose up -d --build
```

This starts:

* Fleet backend
* Mosquitto MQTT broker
* Redis
* Eight mock robot services

To stop the system:

```bash
docker compose down
```

## REST API

### Swagger
```http
http://localhost:8080/swagger-ui.html
```

### Get all robots

```http
GET /robots
```

### Get a specific robot

```http
GET /robots/{robotId}
```

### Get the latest Redis snapshot

```http
GET /robots/{robotId}/last-seen
```

## WebSocket

The backend exposes a STOMP endpoint at:

```text
ws://localhost:8080/ws
```

Clients subscribe to:

```text
/topic/robots
```

The initial fleet snapshot is provided through `FleetWebSocketController` when a client subscribes. Subsequent messages contain only the robot whose state changed rather than repeatedly sending the complete fleet.

A simple browser-based WebSocket test client is included in:

```text
index.html
```

Open it in a browser while the backend is running to observe live robot updates.

## MQTT Testing

The following commands can be used to manually test MQTT communication.

### Send PING to all robots

```bash
mosquitto_pub -h localhost -p 1883 -t robots/broadcast/commands -m "PING"
```

### Listen for robot status updates
```bash
mosquitto_sub -h localhost -p 1883 -t 'robots/+/status'
```

## Redis and State Recovery

Redis stores the latest accepted robot snapshot under keys such as:

```text
robot:r1
```

Redis is used as a recovery mechanism rather than as a historical event store.

When the backend starts, `RedisRecoveryService` restores available snapshots into `FleetState` and broadcasts a PING to the robots so that live state can be recovered.

## Robot Failure and Recovery

Robot health is monitored using the backend-side `lastSeen` timestamp.

The current behavior is:

```text
< 10 seconds      → normal operation
10–30 seconds     → backend sends PING
> 30 seconds      → robot marked OFFLINE
```

When a robot is declared offline, its last known state is preserved in Redis while its timestamp is reset to `t = -1`. The in-memory state is then marked `OFFLINE` and the change is sent to WebSocket clients.

When the robot restarts, the mock robot begins publishing from `t = 0`. Because the previous offline state used `t = -1`, the new sequence can be accepted normally.

This failure/recovery flow was manually verified by stopping a robot during operation, waiting for it to become offline, and restarting it.

## Design Decisions

### MQTT for robot communication

MQTT was chosen for robot-to-backend communication because it is lightweight and well suited to unreliable network connections and telemetry-style communication.

The system uses MQTT QoS 1, providing at-least-once delivery. Since this can result in duplicate or delayed messages, `FleetStateListener` compares the robot event timestamp `t` and rejects stale updates.

### In-memory fleet state

`FleetState` is the source of truth for the backend's current state. A `ConcurrentHashMap` provides efficient robot lookup while allowing concurrent access from MQTT processing, REST requests, and scheduled health checks.

### Redis

Redis stores only the latest snapshot. It allows the backend to recover useful state after a restart without introducing a full persistent history system that is not required for the current assignment.

### WebSocket

STOMP over WebSocket provides topic-based fanout for dashboard clients. WebSocket updates are generated only after `FleetStateListener` accepts an incoming state change, preventing stale MQTT messages from being propagated to clients.

## AI Delegation Notes

#### LLM USED:

* **ChatGPT 5.6 Luna (FREE TIER)** — https://chatgpt.com/c/6a96d6c3-1298-83ee-adb7-d81349fe92a0
* **Gemini 3.6 Flash (PLUS)**


AI assistance was used as a development aid throughout the project. ChatGPT was primarily used for understanding coding concepts, implementation guidance, debugging, and discussing architectural/design decisions. Gemini was primarily used for verifying the latest package versions, checking current APIs/documentation, and validating up-to-date code and configuration details.

The core architectural decisions, including MQTT communication, Redis snapshot recovery, timestamp handling, offline detection, WebSocket state propagation, and robot failure/recovery behavior, were reviewed and integrated into the implementation by the author.

The implementation was manually tested end-to-end using Docker Compose, MQTT communication, REST endpoints, Redis state inspection, and the WebSocket test html client.


## Refrences:
MQTT: https://docs.spring.io/spring-integration/reference/7.0/mqtt.html \
STOMP : https://websocket.org/guides/frameworks/spring-boot/