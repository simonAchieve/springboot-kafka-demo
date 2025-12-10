# Spring Boot Kafka Demo

A Spring Boot application demonstrating Kafka message publishing and consuming using Avro Schema Registry, with Spring Boot Actuator and Web support.

## Features

- ✅ Spring Boot 3.1.5 with Java 17
- ✅ Apache Kafka integration for message publishing and consuming
- ✅ Avro schema support with custom serializers/deserializers
- ✅ Schema Registry compatible design (custom implementation)
- ✅ Spring Boot Actuator for monitoring and health checks
- ✅ RESTful API endpoints for message publishing
- ✅ Automatic Kafka topic creation
- ✅ Consumer service with detailed logging

## Project Structure

```
springboot-kafka-demo/
├── src/
│   ├── main/
│   │   ├── avro/
│   │   │   └── user-event.avsc          # Avro schema definition
│   │   ├── java/
│   │   │   └── com/example/kafka/
│   │   │       ├── KafkaDemoApplication.java
│   │   │       ├── config/
│   │   │       │   └── KafkaConfig.java  # Kafka configuration
│   │   │       ├── controller/
│   │   │       │   └── UserEventController.java
│   │   │       ├── serializer/
│   │   │       │   ├── AvroSerializer.java
│   │   │       │   └── AvroDeserializer.java
│   │   │       └── service/
│   │   │           ├── KafkaProducerService.java
│   │   │           └── KafkaConsumerService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
└── pom.xml
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Apache Kafka (version 3.x)
- Kafka Schema Registry (optional, but recommended for production)

## Building the Project

```bash
# Clean and compile
mvn clean compile

# Package the application
mvn clean package

# Run tests (if available)
mvn test
```

## Configuration

Edit `src/main/resources/application.properties` to configure Kafka connection:

```properties
# Kafka Bootstrap Servers
spring.kafka.bootstrap-servers=localhost:9092

# Schema Registry URL (if using external registry)
spring.kafka.properties.schema.registry.url=http://localhost:8081

# Kafka Topic
kafka.topic.user-events=user-events

# Consumer Group
spring.kafka.consumer.group-id=user-event-consumer-group
```

## Running the Application

### 1. Start Kafka and Zookeeper

```bash
# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka
bin/kafka-server-start.sh config/server.properties
```

### 2. (Optional) Start Schema Registry

```bash
bin/schema-registry-start etc/schema-registry/schema-registry.properties
```

### 3. Run the Spring Boot Application

```bash
# Using Maven
mvn spring-boot:run

# Or using the JAR file
java -jar target/springboot-kafka-demo-1.0.0-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## API Endpoints

### Publish a User Event

**POST** `/api/users/events`

Request Body:
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

Response:
```
User event published successfully with id: <generated-uuid>
```

Example using curl:
```bash
curl -X POST http://localhost:8080/api/users/events \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john.doe@example.com"}'
```

### Health Check

**GET** `/api/users/health`

Response:
```
User Event Service is running
```

## Actuator Endpoints

Spring Boot Actuator provides monitoring and management endpoints:

- **Health**: `http://localhost:8080/actuator/health`
- **Info**: `http://localhost:8080/actuator/info`

**Note**: By default, only `health` and `info` endpoints are exposed for security reasons. To expose additional endpoints like `metrics` or `prometheus` in production, ensure proper authentication and authorization are configured.

To expose more endpoints (for development only), add to `application.properties`:
```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
```

## Avro Schema

The project uses Avro schema for message serialization. The schema is defined in `src/main/avro/user-event.avsc`:

```json
{
  "type": "record",
  "name": "UserEvent",
  "namespace": "com.example.kafka.model",
  "fields": [
    {"name": "id", "type": "string"},
    {"name": "name", "type": "string"},
    {"name": "email", "type": "string"},
    {"name": "timestamp", "type": "long"}
  ]
}
```

The Avro Maven plugin automatically generates Java classes from this schema during the build process.

## How It Works

1. **Producer**: The `KafkaProducerService` sends messages to the Kafka topic using custom Avro serialization
2. **Consumer**: The `KafkaConsumerService` listens to the topic and processes incoming messages
3. **Serialization**: Custom `AvroSerializer` and `AvroDeserializer` handle Avro message conversion
4. **REST API**: The `UserEventController` exposes endpoints to trigger message publishing

## Logging

The application logs all Kafka activities:
- Message publishing with partition information
- Message consumption with offset and partition details
- Any errors during serialization/deserialization

Check application logs for detailed information:
```bash
# View logs while running
tail -f logs/spring-boot-application.log
```

## Troubleshooting

### Kafka Connection Issues
- Ensure Kafka is running on `localhost:9092`
- Check firewall settings
- Verify `bootstrap-servers` configuration

### Serialization Errors
- Ensure Avro schema matches the data being sent
- Check that generated classes are in classpath
- Rebuild the project: `mvn clean compile`

### Consumer Not Receiving Messages
- Verify the topic exists: `kafka-topics.sh --list --bootstrap-server localhost:9092`
- Check consumer group: `kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group user-event-consumer-group`
- Ensure producer and consumer use compatible serializers

## Development

### Adding New Avro Schemas

1. Create a new `.avsc` file in `src/main/avro/`
2. Run `mvn clean compile` to generate Java classes
3. Create corresponding producer/consumer services
4. Update Kafka configuration if needed

### Testing

The project includes Spring Kafka Test dependencies for integration testing:

```java
@SpringBootTest
@EmbeddedKafka
public class KafkaIntegrationTest {
    // Your tests here
}
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.