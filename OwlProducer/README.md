# Owl Producer

## Build

Perform a _mvn clean generate-sources_ to generate Owl class

If the class Owl is not detected, mark the target/generated folder as 'Generated Source Root'

Change the configuration in the application.yml according to your Kafka cluster

## Kafka configuration

You need to have a Kafka cluster up and running with informations about bootstrap server, credentials, schema registry url and authentification

After that, fill up the application.properties file

In the application.yml file, the param spring.kafka.template.default-topic is inventory by default but you can customize it. 
You need to create a topic in your Kafka cluster with the correct name

## Behaviour

There is a Rest API to post some Owl and to see the number of Owl sent

You can use Postman with the collection OwlCollection.json in resources folder