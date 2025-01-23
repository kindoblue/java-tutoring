package com.officemanagement.resource;

import com.officemanagement.model.Floor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/floors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FloorResource {
    private SessionFactory sessionFactory;

    public FloorResource() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @GET
    public Response getAllFloors() {
        try (Session session = sessionFactory.openSession()) {
            List<Floor> floors = session.createQuery(
                "select new Floor(f.id, f.name, f.floorNumber) from Floor f", 
                Floor.class).list();
            return Response.ok(floors).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getFloor(@PathParam("id") Long id) {
        try (Session session = sessionFactory.openSession()) {
            // Using criteria to fetch the floor and its associations
            Floor floor = session.createQuery(
                "select distinct f from Floor f " +
                "left join fetch f.rooms r " +
                "left join fetch r.seats " +
                "where f.id = :id", Floor.class)
                .setParameter("id", id)
                .uniqueResult();
                
            if (floor == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(floor).build();
        }
    }

    @POST
    public Response createFloor(Floor floor) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(floor);
            session.getTransaction().commit();
            return Response.status(Response.Status.CREATED).entity(floor).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateFloor(@PathParam("id") Long id, Floor floor) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            floor.setId(id);
            session.update(floor);
            session.getTransaction().commit();
            return Response.ok(floor).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteFloor(@PathParam("id") Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Floor floor = session.get(Floor.class, id);
            if (floor != null) {
                session.delete(floor);
            }
            session.getTransaction().commit();
            return Response.noContent().build();
        }
    }
} 