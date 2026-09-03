mosquitto_pub -h localhost -p 1883 -t robots/broadcast/commands -m "PING"
mosquitto_sub -h localhost -p 1883 -t robots/+/status