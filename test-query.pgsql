SELECT  DISTINCT
    e.id,
    e.created_at,
    e.full_name,          -- assuming Employee has a name field
    s.id AS seat_id,
    s.seat_number,        -- assuming Seat has a number field
    r.id AS room_id,
    r.name AS room_name  -- assuming Room has a name field
FROM 
    employees e
LEFT JOIN 
    seats s ON s.employee_id = e.id
LEFT JOIN 
    office_rooms r ON s.room_id = r.id
WHERE 
    e.id = 1