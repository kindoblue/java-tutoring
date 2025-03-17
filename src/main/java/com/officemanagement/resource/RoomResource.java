package com.officemanagement.resource;

import com.officemanagement.model.OfficeRoom;
import com.officemanagement.model.Seat;
import com.officemanagement.model.Floor;
import com.officemanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    private final SessionFactory sessionFactory;

    public RoomResource() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    @POST
    public Response createRoom(OfficeRoom room) {
        // Validate input
        if (room == null || room.getName() == null || room.getName().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Room name is required")
                .build();
        }

        if (room.getRoomNumber() == null || room.getRoomNumber().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Room number is required")
                .build();
        }

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            
            // Validate that floor is provided
            if (room.getFloor() == null || room.getFloor().getId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Floor reference is required")
                    .build();
            }
            
            // Load the referenced floor
            Floor floor = session.get(Floor.class, room.getFloor().getId());
            if (floor == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Referenced floor does not exist")
                    .build();
            }

            // Check for duplicate room number in the same floor
            Long count = session.createQuery(
                "SELECT COUNT(r) FROM OfficeRoom r WHERE r.floor.id = :floorId AND r.roomNumber = :roomNumber", Long.class)
                .setParameter("floorId", floor.getId())
                .setParameter("roomNumber", room.getRoomNumber())
                .uniqueResult();

            if (count > 0) {
                return Response.status(Response.Status.CONFLICT)
                    .entity("A room with number " + room.getRoomNumber() + " already exists on this floor")
                    .build();
            }

            room.setFloor(floor);
            room.setCreatedAt(LocalDateTime.now());
            
            session.save(room);
            session.getTransaction().commit();
            
            return Response.status(Response.Status.CREATED)
                .entity(room)
                .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getRoom(@PathParam("id") Long id) {
        try (Session session = sessionFactory.openSession()) {
            OfficeRoom room = session.createQuery(
                "select distinct r from OfficeRoom r " +
                "left join fetch r.seats s " +
                "left join fetch s.employees " +
                "where r.id = :id", OfficeRoom.class)
                .setParameter("id", id)
                .uniqueResult();
                
            if (room == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            
            return Response.ok(room).build();
        }
    }

    @GET
    @Path("/{id}/seats")
    public Response getRoomSeats(@PathParam("id") Long id) {
        try (Session session = sessionFactory.openSession()) {
            OfficeRoom room = session.createQuery(
                "select distinct r from OfficeRoom r " +
                "left join fetch r.seats " +
                "where r.id = :id", OfficeRoom.class)
                .setParameter("id", id)
                .uniqueResult();
                
            if (room == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            
            Set<Seat> seats = room.getSeats();
            return Response.ok(seats).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateRoom(@PathParam("id") Long id, OfficeRoom room) {
        // Validate input
        if (room == null || room.getName() == null || room.getName().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Room name is required")
                .build();
        }

        if (room.getRoomNumber() == null || room.getRoomNumber().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Room number is required")
                .build();
        }

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            
            // Check if room exists
            OfficeRoom existingRoom = session.get(OfficeRoom.class, id);
            if (existingRoom == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Room not found")
                    .build();
            }
            
            // Validate that floor is provided
            if (room.getFloor() == null || room.getFloor().getId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Floor reference is required")
                    .build();
            }
            
            // Load the referenced floor
            Floor floor = session.get(Floor.class, room.getFloor().getId());
            if (floor == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Referenced floor does not exist")
                    .build();
            }

            // Check for duplicate room number in the same floor (excluding current room)
            Long count = session.createQuery(
                "SELECT COUNT(r) FROM OfficeRoom r WHERE r.floor.id = :floorId AND r.roomNumber = :roomNumber AND r.id != :roomId", Long.class)
                .setParameter("floorId", floor.getId())
                .setParameter("roomNumber", room.getRoomNumber())
                .setParameter("roomId", id)
                .uniqueResult();

            if (count > 0) {
                return Response.status(Response.Status.CONFLICT)
                    .entity("A room with number " + room.getRoomNumber() + " already exists on this floor")
                    .build();
            }

            existingRoom.setName(room.getName());
            existingRoom.setRoomNumber(room.getRoomNumber());
            existingRoom.setFloor(floor);
            
            session.update(existingRoom);
            session.getTransaction().commit();
            
            return Response.ok(existingRoom).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            
            // Check if room exists
            OfficeRoom room = session.get(OfficeRoom.class, id);
            if (room == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Room not found")
                    .build();
            }

            // Check if room has seats
            Long seatCount = session.createQuery(
                "SELECT COUNT(s) FROM Seat s WHERE s.room.id = :roomId", Long.class)
                .setParameter("roomId", id)
                .uniqueResult();

            if (seatCount > 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Cannot delete room that has seats")
                    .build();
            }
            
            session.delete(room);
            session.getTransaction().commit();
            
            return Response.noContent().build();
        }
    }

    @PATCH
    @Path("/{id}/geometry")
    public Response updateRoomGeometry(@PathParam("id") Long id, Map<String, Object> geometryData) {
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            
            // Check if room exists
            OfficeRoom room = session.get(OfficeRoom.class, id);
            if (room == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Room not found")
                    .build();
            }
            
            // Update room geometry
            if (geometryData.containsKey("x")) {
                Object xValue = geometryData.get("x");
                if (xValue instanceof Number) {
                    room.setX(((Number) xValue).floatValue());
                }
            }
            
            if (geometryData.containsKey("y")) {
                Object yValue = geometryData.get("y");
                if (yValue instanceof Number) {
                    room.setY(((Number) yValue).floatValue());
                }
            }
            
            if (geometryData.containsKey("width")) {
                Object widthValue = geometryData.get("width");
                if (widthValue instanceof Number) {
                    room.setWidth(((Number) widthValue).floatValue());
                }
            }
            
            if (geometryData.containsKey("height")) {
                Object heightValue = geometryData.get("height");
                if (heightValue instanceof Number) {
                    room.setHeight(((Number) heightValue).floatValue());
                }
            }
            
            session.update(room);
            
            // Check if seat geometries were provided
            if (geometryData.containsKey("seats")) {
                Object seatsObject = geometryData.get("seats");
                if (seatsObject instanceof Map) {
                    Map<?, ?> seatsMap = (Map<?, ?>) seatsObject;
                    
                    // Process each seat
                    for (Map.Entry<?, ?> entry : seatsMap.entrySet()) {
                        if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof Map)) {
                            continue; // Skip invalid entries
                        }
                        
                        String seatIdStr = (String) entry.getKey();
                        Map<String, Object> seatGeometry = (Map<String, Object>) entry.getValue();
                        
                        Long seatId;
                        try {
                            seatId = Long.parseLong(seatIdStr);
                        } catch (NumberFormatException e) {
                            continue; // Skip invalid seat IDs
                        }
                        
                        // Find the seat and verify it belongs to this room
                        Seat seat = session.get(Seat.class, seatId);
                        if (seat != null && seat.getRoom().getId().equals(id)) {
                            // Update seat geometry
                            if (seatGeometry.containsKey("x")) {
                                Object xValue = seatGeometry.get("x");
                                if (xValue instanceof Number) {
                                    seat.setX(((Number) xValue).floatValue());
                                }
                            }
                            
                            if (seatGeometry.containsKey("y")) {
                                Object yValue = seatGeometry.get("y");
                                if (yValue instanceof Number) {
                                    seat.setY(((Number) yValue).floatValue());
                                }
                            }
                            
                            if (seatGeometry.containsKey("width")) {
                                Object widthValue = seatGeometry.get("width");
                                if (widthValue instanceof Number) {
                                    seat.setWidth(((Number) widthValue).floatValue());
                                }
                            }
                            
                            if (seatGeometry.containsKey("height")) {
                                Object heightValue = seatGeometry.get("height");
                                if (heightValue instanceof Number) {
                                    seat.setHeight(((Number) heightValue).floatValue());
                                }
                            }
                            
                            if (seatGeometry.containsKey("rotation")) {
                                Object rotationValue = seatGeometry.get("rotation");
                                if (rotationValue instanceof Number) {
                                    seat.setRotation(((Number) rotationValue).floatValue());
                                }
                            }
                            
                            session.update(seat);
                        }
                    }
                }
            }
            
            transaction.commit();
            
            // Query the room again to return fresh data
            OfficeRoom updatedRoom = session.createQuery(
                "SELECT r FROM OfficeRoom r WHERE r.id = :id", OfficeRoom.class)
                .setParameter("id", id)
                .uniqueResult();
                
            // Create a simplified response object with just the room properties and seat IDs
            Map<String, Object> response = new HashMap<>();
            response.put("id", updatedRoom.getId());
            response.put("name", updatedRoom.getName());
            response.put("roomNumber", updatedRoom.getRoomNumber());
            response.put("x", updatedRoom.getX());
            response.put("y", updatedRoom.getY());
            response.put("width", updatedRoom.getWidth());
            response.put("height", updatedRoom.getHeight());
            
            // Query the seats separately to avoid lazy loading issues
            Set<Seat> seats = session.createQuery(
                "SELECT s FROM Seat s WHERE s.room.id = :roomId", Seat.class)
                .setParameter("roomId", id)
                .getResultList()
                .stream()
                .collect(java.util.stream.Collectors.toSet());
                
            if (seats != null && !seats.isEmpty()) {
                // Add seat IDs to the response
                response.put("seats", seats.size());
            }
            
            return Response.ok(response).build();
            
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Error updating geometry: " + e.getMessage())
                .build();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
} 