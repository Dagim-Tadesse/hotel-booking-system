# Hotel Booking Management System

## This is a Java-based Hotel Booking Management System featuring both a **Command-Line Interface (CLI)**, a **simplified Graphical User Interface (GUI)**, and **basic networking features** using **Sockets** and **RMI**. The system manages hotel rooms, guests, bookings, and reports using a MySQL database backend.

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
- Button to continue **as a Guest** and enter the **chat interface**
- Placeholder buttons for billing and reports (handled via CLI)
- **Note:** GUI is a simplified demo; full functionality is accessible through the CLI

### 🌐 Networking (Sockets):

- Admin-side **Socket Server** (`AdminServer.java`) handles guest communication
- Guest-side **Client Chat** connects via GUI (`ChatPanel`) or CLI (`GuestClient`)
- Message handling and routing using `MessageProtocol.java`
- Guests can:
  - View available rooms
  - Sign up as a new guest
  - Chat with the Admin (simulated via server response)

### 🛰 Remote Access (RMI):

- Remote reporting via Java RMI
- `ReportImp` handles report logic and connects to DB
- `Report` interface defines remote methods (e.g., read, insert, daily reports)
- RMI Server (`RMIServer.java`) registers report implementation
- CLI/GUI can call report functions remotely (if RMI server is running)

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
- **Java Sockets** (Networking)
- **Java RMI** (Remote Reporting)

---

## 🔧 Setup & Run

### 1. Clone the project:

- git clone https://github.com/Dagim-Tadesse/hotel-booking-system.git

### 2. Import into your IDE (e.g., IntelliJ, Eclipse)

### 3. Set up MySQL database:

- Create a MySQL database (e.g., hotel_db)
- Configure the database connection in Conn.java

- > Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "yourUsername", "yourPassword");

- Run SQL scripts to create tables for rooms, guests, bookings, etc. refer to the DB_setup.sql that has all neccessary querys with commented guide

### 4. create the database:

- Follow the instructions in `database/DB_setup.sql` to create necessary tables and functionality.

### 5. Compile and run:

- Run HotelManagement.java for the full-featured CLI (HotelManagement.java)
- Run MainGUI.java for the simplified GUI version(DEMO) (MainGUI.java)
- Run Socket Server (admin chat backend): AdminServer.java
- Run RMI Server (remote reporting): RMIServer.java

---

## 📌 Notes

This GUI is a prototype version of the full CLI-based hotel booking management system. Login functionality is mock/hardcoded for demonstration purposes. The full system is accessible via CLI (HotelManagement.java).

- Networking is added to allow guest communication via chat and remote report access via RMI.

## 📁 Project Structure

HOTEL/
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
├── Gui/
│ ├── BookingManagementPanel.java
│ ├── LoginPanel.java
│ └── [GUI Panels...]
│
├── network/
│ ├── AdminServer.java
│ ├── GuestClient.java
│ ├── GuestHandler.java
│ └── MessageProtocol.java
│
├── rmi/
│ ├── ReportImp.java
│ └── RMIServer.java
│
├── HotelManagement.java
│
└── MainGUI.java
