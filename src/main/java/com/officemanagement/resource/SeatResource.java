package com.officemanagement.resource;

import com.officemanagement.model.Seat;
import com.officemanagement.model.OfficeRoom;
import com.officemanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;

@Path("/seats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeatResource {
    private final SessionFactory sessionFactory;

    public SeatResource() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
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
            
            // Validate that room is provided
            if (seat.getRoom() == null || seat.getRoom().getId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Room reference is required")
                    .build();
            }
            
            // Load the referenced room
            OfficeRoom room = session.get(OfficeRoom.class, seat.getRoom().getId());
            if (room == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Referenced room does not exist")
                    .build();
            }
            
            // Set the room and creation timestamp
            seat.setRoom(room);
            seat.setCreatedAt(LocalDateTime.now());
            
            // Save the seat
            session.save(seat);
            session.getTransaction().commit();
            
            // Refresh the seat to get the generated ID
            session.refresh(seat);
            
            return Response.status(Response.Status.CREATED)
                .entity(seat)
                .build();
        }
    }
} 