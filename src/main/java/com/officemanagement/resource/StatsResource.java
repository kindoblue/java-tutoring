package com.officemanagement.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.PreDestroy;

@Path("/stats") // Base path for all stats-related endpoints
public class StatsResource {

    private final SessionFactory sessionFactory;

    public StatsResource() {
        // Initialize the SessionFactory
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @PreDestroy
    public void cleanup() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    // DTO for stats response
    public static class StatsDTO {
        @JsonProperty("totalEmployees")
        private final long totalEmployees;
        
        @JsonProperty("totalFloors")
        private final long totalFloors;
        
        @JsonProperty("totalOffices")
        private final long totalOffices;
        
        @JsonProperty("totalSeats")
        private final long totalSeats;

        public StatsDTO(long totalEmployees, long totalFloors, long totalOffices, long totalSeats) {
            this.totalEmployees = totalEmployees;
            this.totalFloors = totalFloors;
            this.totalOffices = totalOffices;
            this.totalSeats = totalSeats;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON) // Return JSON response
    public Response getStats() {
        try (Session session = sessionFactory.openSession()) {
            // Get counts using getSingleResult instead of uniqueResult
            Long totalEmployees = session.createQuery("SELECT COUNT(e) FROM Employee e", Long.class)
                                      .getSingleResult();

            Long totalFloors = session.createQuery("SELECT COUNT(f) FROM Floor f", Long.class)
                                   .getSingleResult();

            Long totalOffices = session.createQuery("SELECT COUNT(o) FROM OfficeRoom o", Long.class)
                                    .getSingleResult();

            Long totalSeats = session.createQuery("SELECT COUNT(s) FROM Seat s", Long.class)
                                  .getSingleResult();

            StatsDTO stats = new StatsDTO(totalEmployees, totalFloors, totalOffices, totalSeats);
            return Response.ok(stats).build();
        } catch (Exception e) {
            // Handle errors and return an appropriate response
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to retrieve stats: " + e.getMessage()))
                    .build();
        }
    }

    // Error response class
    private static class ErrorResponse {
        @JsonProperty("message")
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}