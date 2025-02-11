# Office Management System

![Build Status](https://github.com/kindoblue/java-tutoring/actions/workflows/build.yml/badge.svg)
![Dependabot](https://img.shields.io/badge/dependabot-enabled-025E8C?logo=dependabot)
![Java Version](https://img.shields.io/badge/Java-11-orange?logo=java)
![Last Commit](https://img.shields.io/github/last-commit/kindoblue/java-tutoring)

A Java-based office management system for managing employees, seats, and office spaces.

## Technologies Used

- Java 11
- Hibernate 5.6
- Jersey (JAX-RS) 2.34
- PostgreSQL
- HikariCP
- Maven

## Features

- Employee management with pagination and search
- Office space management (floors, rooms, seats)
- Seat assignment system
- RESTful API
- Connection pooling with HikariCP

## Building the Project

```bash
mvn clean package
```

## Running the Application

```bash
mvn cargo:run
```

The application will be available at `http://localhost:8080`

## API Documentation

### Floors
- `GET /api/floors` - List all floors
  - Response: Array of floors with basic info (id, name, floorNumber)
- `GET /api/floors/{id}` - Get floor details with rooms and seats
  - Response: Floor object with nested rooms and seats
- `POST /api/floors` - Create a new floor
  - Request Body: `{"name": "First Floor", "floorNumber": 1}`
  - Response: Created floor object with id
- `PUT /api/floors/{id}` - Update a floor
  - Request Body: `{"name": "Updated Floor", "floorNumber": 1}`
  - Response: Updated floor object
- `DELETE /api/floors/{id}` - Delete a floor
  - Response: 204 No Content
  - Error: 400 Bad Request if floor has rooms

### Rooms
- `GET /api/rooms/{id}` - Get room details with seats
  - Response: Room object with nested seats
- `GET /api/rooms/{id}/seats` - Get all seats in a room
  - Response: Array of seats
- `POST /api/rooms` - Create a new room
  - Request Body: `{"name": "Conference Room", "roomNumber": "101", "floor": {"id": 1}}`
  - Response: Created room object with id
- `PUT /api/rooms/{id}` - Update a room
  - Request Body: `{"name": "Updated Room", "roomNumber": "102", "floor": {"id": 1}}`
  - Response: Updated room object
- `DELETE /api/rooms/{id}` - Delete a room
  - Response: 204 No Content
  - Error: 400 Bad Request if room has seats

### Seats
- `GET /api/seats/{id}` - Get seat details
  - Response: Seat object with room info
- `POST /api/seats` - Create a new seat
  - Request Body: `{"seatNumber": "101-A", "room": {"id": 1}}`
  - Response: Created seat object with id
- `PUT /api/seats/{id}` - Update a seat
  - Request Body: `{"seatNumber": "101-B", "room": {"id": 1}}`
  - Response: Updated seat object
- `DELETE /api/seats/{id}` - Delete a seat
  - Response: 204 No Content
  - Error: 400 Bad Request if seat is assigned to an employee

### Employees
- `GET /api/employees/{id}` - Get employee details
  - Response: Employee object with assigned seats
- `GET /api/employees/{id}/seats` - Get employee's assigned seats
  - Response: Array of seats
- `GET /api/employees/search` - Search employees with pagination
  - Query Parameters:
    - `search`: Search term for name or occupation
    - `page`: Page number (default: 0)
    - `size`: Page size (default: 10)
  - Response: Paginated employee results
- `POST /api/employees` - Create new employee
  - Request Body: `{"fullName": "John Doe", "occupation": "Software Engineer"}`
  - Response: Created employee object with id
- `PUT /api/employees/{id}/assign-seat/{seatId}` - Assign seat to employee
  - Response: Updated employee object with seats
  - Error: 400 Bad Request if seat is already occupied
- `DELETE /api/employees/{id}/unassign-seat/{seatId}` - Unassign seat from employee
  - Response: Updated employee object with seats
  - Error: 400 Bad Request if seat is not assigned to employee

### Statistics
- `GET /api/stats` - Get office statistics
  - Response: Object containing:
    - Total number of floors
    - Total number of rooms
    - Total number of seats
    - Total number of employees
    - Seat occupancy rate

Example Requests:

```bash
# Create a new floor
curl -X POST http://localhost:8080/api/floors \
-H "Content-Type: application/json" \
-d '{
  "name": "First Floor",
  "floorNumber": 1
}'

# Create a new room
curl -X POST http://localhost:8080/api/rooms \
-H "Content-Type: application/json" \
-d '{
  "name": "Conference Room",
  "roomNumber": "101",
  "floor": {"id": 1}
}'

# Create a new seat
curl -X POST http://localhost:8080/api/seats \
-H "Content-Type: application/json" \
-d '{
  "seatNumber": "101-A",
  "room": {"id": 1}
}'

# Create a new employee
curl -X POST http://localhost:8080/api/employees \
-H "Content-Type: application/json" \
-d '{
  "fullName": "John Doe",
  "occupation": "Software Engineer"
}'

# Assign a seat to an employee
curl -X PUT http://localhost:8080/api/employees/1/assign-seat/1

# Search employees
curl "http://localhost:8080/api/employees/search?search=engineer&page=0&size=10"
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Prerequisites

- [Visual Studio Code](https://code.visualstudio.com/download)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- VSCode Extension: [Dev Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers)

## Quick Start

1. Clone this repository
2. Open VSCode
3. Install the "Dev Containers" extension if you haven't already
4. Open the project folder in VSCode
5. When prompted "Folder contains a Dev Container configuration file. Reopen folder to develop in a container?" click "Reopen in Container"

VSCode will then:
1. Build and start two containers:
   - A development container with all necessary tools (Java, Maven, etc.)
   - A PostgreSQL database container (accessible on port 5432)
2. Mount your project files into the development container
3. Connect VSCode to the development container


## Database Setup

The PostgreSQL database is automatically:
- Created with name: `office_management`
- Initialized with tables: `floors`, `office_rooms`, and `seats`
- Populated with sample data
- Accessible with:
  - Host: `localhost`
  - Port: `5432`
  - Username: `postgres`
  - Password: `postgres`

## Building 

Build the project, test and create WAR file with the following command:

``` 
mvn package
```

This single command will:
1. Compile the Java code
2. Run any tests
3. Package the application into a WAR file

## Running the Application
```
mvn cargo:run
```
1. Start an embedded Tomcat server
2. Deploy the WAR file to Tomcat
3. The application will be available at `http://localhost:8080`


## Development Container Details

The project uses VSCode's Dev Containers feature to provide a consistent development environment. The setup includes:

1. **Development Container**:
   - Based on devbox image
   - Contains all development tools (Java 11, Maven, etc.)
   - Mounts your project directory
   - Connected to the database container
   - Configured with necessary extensions for Java development

2. **Database Container**:
   - PostgreSQL 15
   - Persists data in a Docker volume
   - Automatically initialized with schema
   - Health checks ensure database is ready before app starts
