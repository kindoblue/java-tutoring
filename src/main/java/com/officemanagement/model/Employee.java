package com.officemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "employee_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, updatable = false)
    @JsonView(Seat.Views.Base.class)
    private Long id;

    @Column(name = "full_name")
    @JsonView(Seat.Views.Base.class)
    private String fullName;

    @JsonView(Seat.Views.Base.class)
    private String occupation;

    @ManyToMany
    @JoinTable(
        name = "employee_seat_assignments",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "seat_id")
    )
    @JsonIgnoreProperties("employees")
    @JsonView(Seat.Views.Base.class)
    private Set<Seat> seats = new HashSet<>();

    @Column(name = "created_at")
    @JsonView(Seat.Views.Base.class)
    private LocalDateTime createdAt;

    // Constructors
    public Employee() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Set<Seat> getSeats() {
        return seats;
    }

    public void setSeats(Set<Seat> seats) {
        this.seats = seats;
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
        seat.getEmployees().add(this);
    }

    public void removeSeat(Seat seat) {
        seats.remove(seat);
        seat.getEmployees().remove(this);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 