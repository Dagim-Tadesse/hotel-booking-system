package classes;

// import java.io.BufferedReader;
// import java.io.FileInputStream;
// import java.io.IOException;
// import java.io.InputStreamReader;
import java.util.ArrayList;

import database.Conn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;//for changing string to localdate object
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;//for counting the days between checkin and checkout

public class Booking {
    public Room room;
    public Guest guest;
    public boolean isPaid;
    public String check_in_date;
    public String check_out_date;
    public int totalPrice;
    public int roomId;
    public int guestId;
    public int bookingId;

    public Booking(Room room, Guest guest, String check_in_date, String check_out_date) {
        this.room = room;
        this.guest = guest;
        this.isPaid = false;
        this.check_in_date = check_in_date;
        this.check_out_date = check_out_date;
        this.totalPrice = room.roomPrice * countdays(check_in_date, check_out_date);
        this.roomId = Room.getRoomId(this.room.roomNumber);
        this.guestId = Guest.getGuestId(this.guest.name);
        this.bookingId = getBookingId(this.guest.name, this.room.roomNumber);
        insertBookingDB(this.guestId, this.roomId, this.check_in_date, this.check_out_date, this.totalPrice,
                this.isPaid);
    }

    public static int countdays(String checkIn, String checkOut) {
        LocalDate checkInDate = LocalDate.parse(checkIn);
        LocalDate checkOutDate = LocalDate.parse(checkOut);

        // Calculate the difference in days
        // used (int) because it by default returns Long
        // but since the checkin and checkout are restricted to nomore than 30 days
        int count = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return count;
    }

    public Booking(Room room, Guest guest, String checkIn, String checkOut, int totalPrice, boolean isPaid,
            int roomId, int guestId, int bookingId) {
        this.room = room;
        this.guest = guest;
        this.check_in_date = checkIn;
        this.check_out_date = checkOut;
        this.totalPrice = totalPrice;
        this.isPaid = isPaid;
        this.roomId = roomId;
        this.guestId = guestId;
        this.bookingId = bookingId;

    }

    public static boolean isValidDate(String date, boolean isNew, boolean isCheckin, String checkin, String checkout) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate checkDate = LocalDate.parse(date, formatter); // Parse input date
            LocalDate checkoutDate;
            if (!isCheckin) {
                LocalDate checkinDate = LocalDate.parse(checkin, formatter);
                LocalDate maxDate = checkinDate.plusDays(30);

                if (isNew) {
                    return !checkDate.isBefore(checkinDate) && !checkDate.isAfter(maxDate);
                } else {
                    checkoutDate = LocalDate.parse(checkout, formatter);

                    return !checkDate.isBefore(checkoutDate) && !checkDate.isAfter(maxDate);
                }
            }

            LocalDate today = LocalDate.now(); // Get current date
            LocalDate maxAllowedDate = today.plusDays(30); // 30 days from today
            if (isNew) {
                return !checkDate.isBefore(today) && !checkDate.isAfter(maxAllowedDate);
            } else {
                checkoutDate = LocalDate.parse(checkout, formatter);
                return !checkDate.isBefore(today) && !checkDate.isAfter(maxAllowedDate)
                        && !checkDate.isAfter(checkoutDate);
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return false; // Invalid format or impossible date
        }
    }

    public static int getBookingId(String name, String num) {
        Connection connection = Conn.getConnection();
        int id = -1;
        int gId = Guest.getGuestId(name);
        int rId = Room.getRoomId(num);
        try {
            if (connection != null) {
                String query1 = "SELECT bookingId FROM booking WHERE guestId=? AND roomId=?";
                PreparedStatement pstmt1 = connection.prepareStatement(query1);
                pstmt1.setInt(1, gId);
                pstmt1.setInt(2, rId);
                ResultSet rs = pstmt1.executeQuery();
                if (rs.next()) { // If a match is found
                    id = rs.getInt("bookingId");

                }
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public static ArrayList<Booking> readBookingDB() {

        ArrayList<Booking> bookings = new ArrayList<>();
        ArrayList<Room> rooms = Room.readRoomDB();
        ArrayList<Guest> guests = Guest.readGuestDB();
        Connection connection = Conn.getConnection();

        try {
            if (connection != null) {
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT booking.bookingId, booking.guestId, booking.roomId, booking.isPaid, booking.check_In_Date, "
                                + "booking.check_Out_Date, booking.totalPrice, guest.guestName, guest.phoneNumber, "
                                + "room.roomNumber, room.roomType, room.roomPrice, room.isAvailable, room.isUnderMaintenance "
                                + "FROM booking "
                                + "JOIN guest ON booking.guestId = guest.guestId "
                                + "JOIN room ON booking.roomId = room.roomId");

                while (rs.next()) {
                    Room room = null;
                    String roomNumber = rs.getString("roomNumber");
                    String guestName = rs.getString("guestName");
                    for (Room r : rooms) {
                        if (r.roomNumber.equals(roomNumber)) {
                            room = r;
                            break;
                        }
                    }

                    Guest guest = null;
                    for (Guest g : guests) {
                        if (g.name.equals(guestName)) {
                            guest = g;
                            break;
                        }
                    }

                    String checkIn = rs.getString("check_In_Date");
                    String checkOut = rs.getString("check_Out_Date");
                    int totalPrice = rs.getInt("totalPrice");
                    boolean isPaid = (rs.getInt("isPaid") == 1);// change 0,1 to false,true
                    int roomid = rs.getInt("roomId");
                    int guestid = rs.getInt("guestId");
                    int bookingid = rs.getInt("bookingId");

                    bookings.add(new Booking(room, guest, checkIn, checkOut, totalPrice, isPaid, roomid, guestid,
                            bookingid));
                }
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public static void insertBookingDB(int guestId, int roomId, String checkIn, String checkOut, int totalPrice,
            boolean isPaid) {
        Connection connection = Conn.getConnection();
        try {
            if (connection != null) {
                String query = "INSERT INTO booking (guestId, roomId, check_In_Date," +
                        " check_Out_Date, totalPrice, isPaid) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, guestId);
                pstmt.setInt(2, roomId);
                pstmt.setString(3, checkIn);
                pstmt.setString(4, checkOut);
                pstmt.setInt(5, totalPrice);
                pstmt.setBoolean(6, isPaid);
                pstmt.executeUpdate();

                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteBookingDB(String name, String num) {
        Connection connection = Conn.getConnection();
        int id = getBookingId(name, num);
        try {
            if (connection != null) {
                String query = "DELETE FROM booking WHERE bookingId = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, id); // Use bookingId to delete the correct row
                pstmt.executeUpdate();

                pstmt.close();
                connection.close();
                System.out.println("Booking deleted successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateBookingInDB(String gname, String rnum, String column, String newValue,
            boolean isGuestOrRoom) {
        Connection connection = Conn.getConnection();
        int bId = getBookingId(gname, rnum);
        try {
            if (connection != null) {
                String query = "UPDATE booking SET " + column + " = ? WHERE bookingId = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);

                if (isGuestOrRoom) {

                    int Id = column.equals("guestId") ? Guest.getGuestId(newValue) : Room.getRoomId(newValue);

                    pstmt.setInt(1, Id); // Store the retrieved ID
                } else {
                    pstmt.setString(1, newValue);
                }

                pstmt.setInt(2, bId);
                pstmt.executeUpdate();
                connection.close();
                System.out.println("Booking updated successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updatePriceDB(String name, String num, int newPrice) {
        Connection connection = Conn.getConnection();
        int bId = getBookingId(name, num);
        try {
            if (connection != null) {
                String query = "UPDATE booking SET totalPrice = ? WHERE bookingId = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);

                pstmt.setInt(1, newPrice);
                pstmt.setInt(2, bId);

                pstmt.executeUpdate();
                if (newPrice == 0) {
                    updatePayStatusDB(name, num, true);
                }
                connection.close();
                System.out.println("Booking total price updated successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updatePayStatusDB(String name, String num, boolean isPay) {
        Connection connection = Conn.getConnection();
        int bId = getBookingId(name, num);
        try {
            if (connection != null) {
                String query1 = "UPDATE booking SET isPaid = ? WHERE bookingId = ?";
                PreparedStatement pstmt1 = connection.prepareStatement(query1);
                pstmt1.setBoolean(1, isPay);
                pstmt1.setInt(2, bId);
                pstmt1.executeUpdate();
                connection.close();
                System.out.println("Booking payment status updated successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
