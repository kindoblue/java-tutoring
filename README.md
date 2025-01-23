# Office Management System

A REST API service for managing office spaces, including floors, rooms, and seats.

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

## Building and Running

Build the project, create WAR file, and start Tomcat automatically with the following command:

``` 
mvn package
```

This single command will:
1. Compile the Java code
2. Run any tests
3. Package the application into a WAR file
4. Start an embedded Tomcat server
5. Deploy the WAR file to Tomcat
6. The application will be available at `http://localhost:8080`

## API Endpoints

### Floors
- `GET /api/floors` - List all floors (basic info)
- `GET /api/floors/{id}` - Get floor details with rooms and seats
- `POST /api/floors` - Create a new floor
- `PUT /api/floors/{id}` - Update a floor
- `DELETE /api/floors/{id}` - Delete a floor

### Rooms
- `GET /api/rooms/{id}/seats` - Get all seats in a room

### Seats
- `GET /api/seats/{id}` - Get seat details
- `POST /api/seats` - Create a new seat

Example seat creation:

```
curl -X POST http://localhost:8080/api/seats \
-H "Content-Type: application/json" \
-d '{
"seatNumber": "801-05",
"room": {"id": 1},
"occupied": false
}'
```

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
