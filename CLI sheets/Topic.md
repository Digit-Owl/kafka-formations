# Display the list of topics
./kafka-topics.sh --command-config sample.properties --bootstrap-server url:port --list

# Create a topic with 10 partitions, RF 3 min isr 2
./kafka-topics.sh --create --command-config sample.properties --bootstrap-server url:port --topic topicname --partitions 10 --replication-factor 3 insync --config min.insync.replicas=2

# Display config of a topic
./kafka-topics.sh --describe --command-config sample.properties --bootstrap-server url:port --topic topicname

# Delete a topic
./kafka-topics.sh --delete --command-config sample.properties --bootstrap-server url:port --topic topicname
