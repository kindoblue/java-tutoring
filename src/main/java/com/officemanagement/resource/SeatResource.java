package com.officemanagement.resource;

import com.officemanagement.model.Seat;
import com.officemanagement.model.OfficeRoom;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;

@Path("/seats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeatResource {
    private SessionFactory sessionFactory;

    public SeatResource() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @GET
    @Path("/{id}")
    public Response getSeat(@PathParam("id") Long id) {
        try (Session session = sessionFactory.openSession()) {
            Seat seat = session.get(Seat.class, id);
                
            if (seat == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            
            return Response.ok(seat).build();
        }
    }

    @POST
    public Response createSeat(Seat seat) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            
            // Load the referenced room
            if (seat.getRoom() != null && seat.getRoom().getId() != null) {
                OfficeRoom room = session.get(OfficeRoom.class, seat.getRoom().getId());
                if (room == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Referenced room does not exist")
                        .build();
                }
                seat.setRoom(room);
            }
            
            // Set creation timestamp
            seat.setCreatedAt(LocalDateTime.now());
            
            session.save(seat);
            session.getTransaction().commit();
            
            return Response.status(Response.Status.CREATED)
                .entity(seat)
                .build();
        }
    }

    @PATCH
    @Path("/{id}/occupy")
    public Response updateSeatOccupation(@PathParam("id") Long id, @QueryParam("occupied") Boolean occupied) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            
            Seat seat = session.get(Seat.class, id);
            if (seat == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            
            seat.setOccupied(occupied);
            session.update(seat);
            session.getTransaction().commit();
            
            return Response.ok(seat).build();
        }
    }
} 