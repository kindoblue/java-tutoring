package com.officemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "floors")
public class Floor {
    /**
     * JSON Views for controlling serialization of entities
     */
    public static class Views {
        // Base view with common properties
        public static class Base {}
        
        // Extended view that includes planimetry data
        public static class WithPlanimetry extends Base {}
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "floor_seq")
    @SequenceGenerator(name = "floor_seq", sequenceName = "floor_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, updatable = false)
    @JsonView(Views.Base.class)
    private Long id;

    @Column(name = "floor_number")
    @JsonView(Views.Base.class)
    private Integer floorNumber;

    @JsonView(Views.Base.class)
    private String name;
    
    @Column(name = "floor_plan")
    @JsonView(Views.WithPlanimetry.class) // Only include in WithPlanimetry view
    private String planimetry;

    @Column(name = "created_at")
    @JsonView(Views.Base.class)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "floor", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("floor")
    @JsonView(Views.Base.class)
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

    public String getPlanimetry() {
        return planimetry; 
    }

    public void setPlanimetry(String planimetry) {
        this.planimetry = planimetry; 
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
