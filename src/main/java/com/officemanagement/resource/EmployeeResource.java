package com.officemanagement.resource;

import com.officemanagement.model.Employee;
import com.officemanagement.model.Seat;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeeResource {
    private SessionFactory sessionFactory;

    public EmployeeResource() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @GET
    public Response getAllEmployees() {
        try (Session session = sessionFactory.openSession()) {
            List<Employee> employees = session.createQuery(
                "select distinct e from Employee e " +
                "left join fetch e.seat s " +
                "left join fetch s.room r", 
                Employee.class).list();
            return Response.ok(employees).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getEmployee(@PathParam("id") Long id) {
        try (Session session = sessionFactory.openSession()) {
            Employee employee = session.createQuery(
                "select distinct e from Employee e " +
                "left join fetch e.seat s " +
                "left join fetch s.room r " +
                "where e.id = :id", 
                Employee.class)
                .setParameter("id", id)
                .uniqueResult();
            
            if (employee == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(employee).build();
        }
    }

    @POST
    public Response createEmployee(Employee employee) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            employee.setCreatedAt(LocalDateTime.now());
            session.save(employee);
            session.getTransaction().commit();
            return Response.status(Response.Status.CREATED).entity(employee).build();
        }
    }

    @PUT
    @Path("/{id}/assign-seat/{seatId}")
    public Response assignSeat(@PathParam("id") Long employeeId, @PathParam("seatId") Long seatId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            
            Employee employee = session.get(Employee.class, employeeId);
            if (employee == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Employee not found").build();
            }

            Seat seat = session.get(Seat.class, seatId);
            if (seat == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Seat not found").build();
            }

            if (seat.getEmployee() != null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Seat is already occupied").build();
            }

            // Set both sides of the bidirectional relationship
            seat.setEmployee(employee);
            employee.setSeat(seat);
            
            // Update both entities
            session.update(seat);
            session.update(employee);
            
            session.getTransaction().commit();
            
            return Response.ok(employee).build();
        }
    }

    @DELETE
    @Path("/{id}/unassign-seat")
    public Response unassignSeat(@PathParam("id") Long employeeId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            
            Employee employee = session.get(Employee.class, employeeId);
            if (employee == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Employee not found").build();
            }

            employee.setSeat(null);
            session.update(employee);
            session.getTransaction().commit();
            
            return Response.ok(employee).build();
        }
    }
} 