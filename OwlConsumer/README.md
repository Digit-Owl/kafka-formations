# Owl Consumer

## Build

Perform a _mvn clean generate-sources_ to generate Owl class

If the class Owl is not detected, mark the target/generated folder as 'Generated Source Root'

Change the configuration in the application.yml according to your Kafka cluster

## Kafka configuration

You need to have a Kafka cluster up and running with information about bootstrap server, credentials, schema registry url and authentification

After that, fill up the application.properties file

In the application.yml file, the param spring.kafka.template.default-topic is inventory by default but you can customize it.
You need to create a topic in your Kafka cluster with the correct name

Some of the consumer's properties can be modified to customize it

## Behaviour

Run the OwlConsumer as a Spring Boot App and it will fetch and display in the log, the content of the record 