# Service Manager Dashboard

A CLI application for managing system services on your computer.

## Build

mvn compile

## Run

mvn exec:java -Dexec.mainClass="com.servicemanager.Main"

## Alternative Run

java -cp target/classes com.servicemanager.Main

## Commands

java -cp target/classes com.servicemanager.Main list  
java -cp target/classes com.servicemanager.Main start hello  
java -cp target/classes com.servicemanager.Main stop hello  

## Configuration

Services are loaded from:

src/main/resources/services.json

Example:

{
  "services": [
    {
      "name": "hello",
      "command": "echo Hello from service",
      "workingDir": ""
    }
  ]
}

## Features

- List services
- Start a service
- Stop a service
- Restart a service
- View service status
- View service logs
- Load services from JSON file

## Requirements Met

- Classes and Objects
- Inheritance
- Polymorphism
- Abstraction
- Encapsulation
- Java Collections
- File I/O
- Exception Handling

## Notes

- Project builds and runs in NetBeans without manual configuration
- Developed using Maven (JDK 25)
- Uses Gson for JSON parsing

## Team Contribution

- Semion Andreev: Project Structure, CLI, Service Manager Logic, debugging and fixes  
- Raheem Khawaja: Service class, ConfigLoader, JSON configuration, debugging and fixes