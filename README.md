# Hotel Booking Management System

This is a Java-based Hotel Booking Management System featuring both a **Command-Line Interface (CLI)** and a **simplified Graphical User Interface (GUI)**. The system manages hotel rooms, guests, and bookings using a MySQL database backend.

---

## 🔹 Features

### ✅ Implemented (CLI):

- Add, update, delete, and view **Rooms**
- Add, update, delete, and view **Guests**
- Create, update, cancel, and view **Bookings**
- Generate **Reports**
- **Billing** calculations
- Fully functional database integration

### 🖼 GUI (Prototype):

- GUI Login screen with mock authentication (`admin` / `123456`)
- GUI Panels for:
  - Room Management
  - Guest Management
  - Booking Management
- Placeholder buttons for billing and reports (handled via CLI)
- **Note:** GUI is a simplified demo; full functionality is accessible through the CLI

---

## 🔐 Login (GUI)

- **Username:** `admin`
- **Password:** `123456`
- > _This login is hardcoded for demonstration purposes only._

---

## 💾 Tech Stack

- **Java** (JDK 8+)
- **Swing** (GUI)
- **MySQL** (Database)
- **JDBC** (Database connection)

---

## 🔧 Setup & Run

### 1. Clone the project:

- git clone https://github.com/Dagim-Tadesse/hotel-booking-system.git

### 2. Import into your IDE (e.g., IntelliJ, Eclipse)

### 3. Set up MySQL database:

- Create a MySQL database (e.g., hotel_db)
- Configure the database connection in Conn.java

- > Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "yourUsername", "yourPassword");

- Run SQL scripts to create tables for rooms, guests, bookings, etc. refer to the DB.txt that has all neccessary querys with commentd guide

### 4. create the database:

- Follow the instructions in `database/DB_setup.sql` to create necessary tables and functionality.

### 5. Compile and run:

- Run HotelManagement.java for the full-featured CLI (HotelManagement.java)
- Run MainGUI.java for the simplified GUI version(DEMO) (MainGUI.java)

---

## 📌 Notes

This GUI is a prototype version of the full CLI-based hotel booking management system. Login functionality is mock/hardcoded for demonstration purposes. The full system is accessible via CLI (HotelManagement.java).

## 📁 Project Structure

HOTEL/
│
├── Gui/
│ ├── BookingManagementPanel.java
│ ├── LoginPanel.java
│ └── [GUI Panels...]
│
├── classes/
│ ├── Room.java
│ ├── Guest.java
│ ├── Booking.java
│ └── Report.java
│
├── database/
│ └── Conn.java
│
├── HotelManagement.java
│
└── MainGUI.java
