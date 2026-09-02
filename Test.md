mosquitto_pub -h localhost -p 1883 -t robots/r1/commands -m "PING"
mosquitto_sub -h localhost -p 1883 -t robots/+/status