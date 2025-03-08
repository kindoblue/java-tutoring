package com.officemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "seats")
public class Seat {
    // Define JSON views for different contexts
    public static class Views {
        // Base view with common properties
        public static class Base {}
        
        // View for when seat is shown on its own (includes room)
        public static class Full extends Base {}
        
        // View for when seat is shown as part of an employee (excludes room)
        public static class AsEmployeeSeat extends Base {}
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seat_seq")
    @SequenceGenerator(name = "seat_seq", sequenceName = "seat_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, updatable = false)
    @JsonView(Views.Base.class)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnoreProperties("seats")
    @JsonView(Views.Full.class) // Only include room in the Full view
    private OfficeRoom room;

    @Column(name = "seat_number", nullable = false)
    @JsonView(Views.Base.class)
    private String seatNumber;

    @Column(name = "created_at", nullable = false)
    @JsonView(Views.Base.class)
    private LocalDateTime createdAt;

    @ManyToMany(mappedBy = "seats")
    @JsonIgnoreProperties("seats")
    @JsonView(Views.Full.class)
    private Set<Employee> employees = new HashSet<>();

    // Add a convenience method to check if seat is occupied
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(Views.Base.class)
    public boolean isOccupied() {
        return !employees.isEmpty();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OfficeRoom getRoom() {
        return room;
    }

    public void setRoom(OfficeRoom room) {
        this.room = room;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }
} 