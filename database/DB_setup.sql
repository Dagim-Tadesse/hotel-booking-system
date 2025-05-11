-- this file is meant for making a database for the hotel booking management system ,, so please follow the steps and instructions:

-- create the database 
CREATE DATABASE Hotel_Management;

use Hotel_management; --for specifing which database you are working with 

-- create this 4 tables 
--first guest
create table guest(
guestId int AUTO_INCREMENT PRIMARY KEY,
guestName varchar(255),
phoneNumber varchar(255)
);

--second room
CREATE TABLE room (
    roomId INT PRIMARY KEY AUTO_INCREMENT,
    roomNumber VARCHAR(20) NOT NULL UNIQUE,
    roomType varchar(255) NOT NULL,
    roomPrice DECIMAL(10,2),
    isAvailable BOOLEAN DEFAULT TRUE,
	isUnderMaintenance BOOLEAN DEFAULT FALSE
);

--third booking
CREATE TABLE booking (
    bookingId INT PRIMARY KEY AUTO_INCREMENT,
    guestId INT,
    roomId INT,
    isPaid BOOLEAN DEFAULT FALSE,
    bookingDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    check_In_Date DATE NOT NULL,
    check_Out_Date DATE NOT NULL,
    totalPrice DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (guestId) REFERENCES guest(guestId) ON DELETE CASCADE,
    FOREIGN KEY (roomId) REFERENCES room(roomId) ON DELETE SET NULL
);

--forth report
CREATE TABLE report(
    reportId INT PRIMARY KEY AUTO_INCREMENT,
    guestId INT,
    guestName VARCHAR(255),
    phoneNumber VARCHAR(20),
    roomId INT,
    roomNumber VARCHAR(20),
    roomType VARCHAR(255),
    roomPrice DECIMAL(10,2), 
    check_In_Date DATE,
    check_Out_Date DATE,
    totalPrice DECIMAL(10,2),
    paymentDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paymentMethod VARCHAR(255) NOT NULL
);

----------
--RULES---
----------

--this constraint for preventing checkout > checkin 
--and is not implemented on report table because data enters report table from booking table
-- so it is already checked in booking table
ALTER TABLE booking
ADD CONSTRAINT check_dates CHECK (check_Out_Date > check_In_Date);

-- to autoupdate isAvailable(in room table) to false when room added to  booking
DELIMITER $$ 

CREATE TRIGGER update_room_availability
AFTER INSERT ON booking 
FOR EACH ROW BEGIN UPDATE room 
SET isAvailable = FALSE 
WHERE roomId = NEW.roomId; 

END $$ 
DELIMITER ;

-- to autoupdate isAvailable(in room table) when room is updated in booking
DELIMITER $$ 

CREATE TRIGGER update_room_availability_on_update
AFTER UPDATE ON booking 
FOR EACH ROW 
BEGIN 
    -- Set the previously booked room to available
    UPDATE room 
    SET isAvailable = TRUE 
    WHERE roomId = OLD.roomId;

    -- Set the new booked room to unavailable
    UPDATE room 
    SET isAvailable = FALSE 
    WHERE roomId = NEW.roomId; 
END $$ 

DELIMITER ;


-- to restore isAvailable(in room table) to true when delete room from booking

DELIMITER $$

CREATE TRIGGER restore_room_availability
AFTER DELETE ON booking
FOR EACH ROW
BEGIN
    UPDATE room
    SET isAvailable = TRUE
    WHERE roomId = OLD.roomId;
END $$

DELIMITER ;

-- to delete the booking row if the room row it is connected to, is deleted
DELIMITER $$ 
CREATE TRIGGER delete_booking_on_room_delete
AFTER DELETE ON room
FOR EACH ROW
BEGIN
    DELETE FROM booking WHERE roomId = OLD.roomId;
END $$

DELIMITER ;

-- to delete the booking row if the guest row it is connected to ,is deleted
DELIMITER $$
CREATE TRIGGER delete_booking_on_guest_delete
AFTER DELETE ON guest
FOR EACH ROW
BEGIN
    DELETE FROM booking WHERE guestId = OLD.guestId;
END$$
 DELIMITER ;

--ALL DONE.


--sample data--

--for guest
--guestName must be lowercase if manually inserted to the database without the java 
INSERT INTO guest (guestName, phoneNumber) VALUES
('mia stone', '0923232143'),
('david king', '0967438792'),
('john mac', '0902732849'),
('ali red', '0903245213'),
('dani cole', '0904754322'),
('mary lane', '0923273232'),
('henok zane', '0914124312'),
('george vale', '0923542556'),
('josh lee', '0991384876'),
('daisy snow', '0945725643'),
('kevin west', '0917481367'),
('emily rose', '0912345678');

--for room
--same for roomType  must be lowercase
INSERT INTO room (roomNumber, roomType, isAvailable, isUnderMaintenance, roomPrice) VALUES
('100', 'singlebed', TRUE, FALSE, 1000.00),
('101', 'doublebed', TRUE, FALSE, 1500.00),
('102', 'doublebed', TRUE, FALSE, 1500.00),
('103', 'singlebed', TRUE, FALSE, 1000.00),
('104', 'suite', FALSE, FALSE, 3000.00),
('105', 'singlebed', TRUE, FALSE, 1000.00),
('200', 'singlebed', TRUE, FALSE, 1000.00),
('201', 'doublebed', TRUE, FALSE, 1500.00),
('202', 'doublebed', TRUE, FALSE, 1500.00),
('203', 'singlebed', FALSE, FALSE, 1000.00),
('204', 'suite', TRUE, FALSE, 3000.00),
('205', 'singlebed', TRUE, FALSE, 1000.00),
('300', 'singlebed', TRUE, FALSE, 1000.00),
('301', 'doublebed', TRUE, FALSE, 1500.00),
('302', 'doublebed', FALSE, FALSE, 1500.00),
('303', 'singlebed', TRUE, FALSE, 1000.00),
('304', 'suite', TRUE, FALSE, 3000.00),
('305', 'singlebed', FALSE, FALSE, 1000.00),
('400', 'singlebed', TRUE, FALSE, 1000.00),
('401', 'singlebed', TRUE, FALSE, 1000.00),
('402', 'doublebed', FALSE, FALSE, 1500.00),
('403', 'doublebed', FALSE, FALSE, 1500.00),
('404', 'suite', FALSE, FALSE, 3000.00),
('405', 'singlebed', TRUE, FALSE, 1000.00);

--for booking
--please dont manually update or add new data to booking table without the java
INSERT INTO booking (guestId, roomId, isPaid, bookingDate, check_In_Date, check_Out_Date, totalPrice) VALUES
(8, 5, FALSE, '2025-04-25 12:15:32', '2025-04-28', '2025-05-03', 15000.00),
(1, 18, FALSE, '2025-04-27 16:40:10', '2025-04-30', '2025-05-07', 7000.00),
(3, 15, FALSE, '2025-04-29 09:55:45', '2025-05-01', '2025-05-05', 6000.00),
(5, 21, FALSE, '2025-04-30 11:30:20', '2025-05-02', '2025-05-10', 12000.00),
(4, 10, TRUE, '2025-05-02 18:13:02', '2025-05-05', '2025-05-11', 0.00),
(5, 22, TRUE, '2025-05-05 14:10:40', '2025-05-08', '2025-05-14', 0.00),
(5, 23, TRUE, '2025-05-07 20:25:50', '2025-05-09', '2025-05-12', 0.00);

--for report
INSERT INTO report (guestId, guestName, phoneNumber, roomId, roomNumber, roomType, roomPrice, check_In_Date, check_Out_Date, totalPrice, paymentDate, paymentMethod)
VALUES
(2, 'david king', '0967438792', 7, '200', 'singlebed', 1000.00, '2025-03-01', '2025-03-06', 5000.00, '2025-03-01 10:30:15', 'telebirr'),
(12, 'emily rose', '0912345678', 11, '204', 'suite', 3000.00, '2025-03-05', '2025-03-10', 15000.00, '2025-03-03 14:20:10', 'cash'),
(6, 'mary lane', '0923273232', 2, '101', 'doublebed', 1500.00, '2025-03-12', '2025-03-18', 9000.00, '2025-03-12 15:10:30', 'Dashen bank'),
(5, 'dani cole', '0904754322', 23, '404', 'suite', 3000.00, '2025-03-20', '2025-03-25', 15000.00, '2025-03-20 09:20:10', 'Awash bank'),
(10, 'daisy snow', '0945725643', 10, '203', 'singlebed', 1000.00, '2025-03-28', '2025-04-02', 5000.00, '2025-03-28 12:35:50', 'CBE'),
(6, 'mary lane', '0923273232', 3, '102', 'doublebed', 1500.00, '2025-04-04', '2025-04-09', 7500.00, '2025-04-01 19:45:20', 'cash'),
(4, 'ali red', '0903245213', 10, '203', 'singlebed', 1000.00, '2025-04-10', '2025-04-12', 2000.00, '2025-04-09 18:15:45', 'CBE'),
(6, 'mary lane', '0923273232', 4, '103', 'singlebed', 1000.00, '2025-04-18', '2025-04-22', 4000.00, '2025-04-18 23:05:40', 'telebirr'),
(1, 'mia stone', '0923232143', 8, '201', 'doublebed', 1500.00, '2025-04-25', '2025-05-01', 9000.00, '2025-04-25 22:30:40', 'telebirr'),
(3, 'john mac', '0902732849', 18, '305', 'singlebed', 1000.00, '2025-04-29', '2025-05-02', 3000.00, '2025-04-29 10:15:20', 'Dashen bank'),
(2, 'david king', '0967438792', 6, '105', 'singlebed', 1000.00, '2025-05-03', '2025-05-07', 4000.00, '2025-05-03 14:45:32', 'cash'),
(5, 'dani cole', '0904754322', 22, '403', 'doublebed', 1500.00, '2025-05-06', '2025-05-10', 6000.00, '2025-05-05 17:10:05', 'BOA'),
(6, 'mary lane', '0923273232', 1, '100', 'singlebed', 1000.00, '2025-05-07', '2025-05-09', 2000.00, '2025-05-05 22:50:00', 'BOA'),
(5, 'dani cole', '0904754322', 23, '404', 'suite', 3000.00, '2025-05-08', '2025-05-09', 3000.00, '2025-05-08 08:45:15', 'Awash bank');

--END
