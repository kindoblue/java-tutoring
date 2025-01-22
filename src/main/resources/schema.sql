CREATE TABLE floors (
    id SERIAL PRIMARY KEY,
    floor_number INTEGER NOT NULL,
    name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE office_rooms (
    id SERIAL PRIMARY KEY,
    floor_id INTEGER REFERENCES floors(id),
    room_number VARCHAR(50) NOT NULL,
    name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE seats (
    id SERIAL PRIMARY KEY,
    room_id INTEGER REFERENCES office_rooms(id),
    seat_number VARCHAR(50) NOT NULL,
    is_occupied BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
); 