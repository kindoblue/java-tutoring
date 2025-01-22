package com.officemanagement.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private OfficeRoom room;

    @Column(name = "seat_number")
    private String seatNumber;

    @Column(name = "is_occupied")
    private boolean isOccupied;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Getters and setters
} 