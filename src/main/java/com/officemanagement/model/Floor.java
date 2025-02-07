package com.officemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "floors")
public class Floor {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "floor_seq")
    @SequenceGenerator(name = "floor_seq", sequenceName = "floor_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "floor_number")
    private Integer floorNumber;

    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "floor", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("floor")
    private Set<OfficeRoom> rooms = new HashSet<>();

    // Default constructor required by JPA/Hibernate
    public Floor() {
    }

    public Floor(Long id, String name, Integer level) {
        this.id = id;
        this.name = name;
        this.floorNumber = level;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
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

    public Set<OfficeRoom> getRooms() {
        return rooms;
    }

    public void setRooms(Set<OfficeRoom> rooms) {
        this.rooms = rooms;
    }
} 