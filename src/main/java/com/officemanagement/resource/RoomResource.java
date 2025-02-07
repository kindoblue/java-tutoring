package com.officemanagement.resource;

import com.officemanagement.model.OfficeRoom;
import com.officemanagement.model.Seat;
import com.officemanagement.model.Floor;
import com.officemanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

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
            room.setFloor(floor);
            
            // Set creation timestamp
            room.setCreatedAt(LocalDateTime.now());
            
            session.save(room);
            session.getTransaction().commit();
            
            return Response.status(Response.Status.CREATED)
                .entity(room)
                .build();
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
            
            List<Seat> seats = room.getSeats();
            return Response.ok(seats).build();
        }
    }
} 