package com.officemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "office_rooms")
public class OfficeRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "office_room_seq")
    @SequenceGenerator(name = "office_room_seq", sequenceName = "office_room_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, updatable = false)
    @JsonView(Floor.Views.Base.class)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "floor_id")
    @JsonIgnoreProperties("rooms")
    @JsonView(Floor.Views.Base.class)
    private Floor floor;

    @Column(name = "room_number")
    @JsonView(Floor.Views.Base.class)
    private String roomNumber;

    @JsonView(Floor.Views.Base.class)
    private String name;

    @Column(name = "created_at")
    @JsonView(Floor.Views.Base.class)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("room")
    @JsonView(Floor.Views.Base.class)
    private Set<Seat> seats = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Seat> getSeats() {
        return seats;
    }

    public void setSeats(Set<Seat> seats) {
        this.seats = seats;
    }
} 