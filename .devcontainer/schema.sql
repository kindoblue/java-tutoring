-- Create database
CREATE DATABASE office_management;
\c office_management;

-- Create tables
CREATE TABLE floors (
    id BIGSERIAL PRIMARY KEY,
    floor_number INTEGER NOT NULL,
    name VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE office_rooms (
    id BIGSERIAL PRIMARY KEY,
    room_number VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    floor_id BIGINT REFERENCES floors(id),
    created_at TIMESTAMP
);

CREATE TABLE seats (
    id BIGSERIAL PRIMARY KEY,
    seat_number VARCHAR(255) NOT NULL,
    room_id BIGINT REFERENCES office_rooms(id),
    is_occupied BOOLEAN DEFAULT false,
    created_at TIMESTAMP
); 