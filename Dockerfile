# Use Eclipse Temurin JDK 17 as the base image (more secure)
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy the pom.xml file
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Compile the application
RUN mvn clean compile

# Expose the RMI registry port and callback ports
EXPOSE 1099
EXPOSE 1100-1110

# Set JVM properties for RMI to work in Docker
ENV JAVA_OPTS="-Djava.rmi.server.hostname=localhost -Djava.net.preferIPv4Stack=true -Djava.rmi.server.useLocalHostname=true -Djava.rmi.dgc.leaseValue=600000"

# Run the chat server
CMD ["mvn", "exec:java", "-Pserver"]
