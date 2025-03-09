package com.officemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seat_seq")
    @SequenceGenerator(name = "seat_seq", sequenceName = "seat_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnoreProperties("seats")
    private OfficeRoom room;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToMany(mappedBy = "seats")
    @JsonIgnoreProperties("seats")
    private Set<Employee> employees = new HashSet<>();

    // Add a convenience method to check if seat is occupied
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
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