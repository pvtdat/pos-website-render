# Use Maven image to build the application
FROM maven:3.8.5-openjdk-17 AS build

# Copy the entire project directory to the Docker image
COPY . .

# Run Maven package command to build the application
RUN mvn clean package -DskipTests

# List the contents of the target directory to verify the JAR file
RUN ls -l target

# Use a slim version of OpenJDK to run the application
FROM openjdk:17.0.1-jdk-slim

# Copy the JAR file from the Maven build stage to the final image
COPY --from=build /target/demo-0.0.1-SNAPSHOT.jar demo.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "demo.jar"]