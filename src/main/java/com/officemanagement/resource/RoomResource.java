package com.officemanagement.resource;

import com.officemanagement.model.OfficeRoom;
import com.officemanagement.model.Seat;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    private SessionFactory sessionFactory;

    public RoomResource() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
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