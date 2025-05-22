package classes;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;

import database.Conn;

public class Room implements Serializable {
    // for easy serialization
    private static final long serialVersionUID = 1L;
    public String roomNumber;
    public String roomType;
    public int roomPrice;
    public boolean isAvailable;
    public boolean isUnderMaintenance;

    public Room(String roomNumber, String roomType) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.isAvailable = true;
        this.isUnderMaintenance = false;

        if (roomType.equals("singlebed")) {
            this.roomPrice = 1000;
        } else if (roomType.equals("doublebed")) {
            this.roomPrice = 1500;
        } else {
            this.roomPrice = 3000;
        }
        insertRoomDB(this.roomNumber, this.roomType, this.roomPrice, this.isAvailable, this.isUnderMaintenance);
    }

    public Room(String roomNumber, String roomType, int roomPrice, boolean isAvailable, boolean isUnderMaintenance) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.isAvailable = isAvailable;
        this.isUnderMaintenance = isUnderMaintenance;
        this.roomPrice = roomPrice;
    }

    public void bookRoom() {
        if (isAvailable) {
            isAvailable = false;

            System.out.println("Room " + roomNumber + " has been booked.");
        } else {
            System.out.println("Room " + roomNumber + " is already booked.");
        }
    }

    public void checkAvailability() {
        if (!isAvailable) {
            System.out.println("Room " + roomNumber + " - " + roomType + " is booked.");
        } else if (isUnderMaintenance) {
            System.out.println("Room " + roomNumber + " - " + roomType + " is under Maintenance.");
        } else {
            System.out.println("Room " + roomNumber + " - " + roomType + " is available.");
        }

    }

    public static boolean roomtypecheck(String rtype) {
        rtype = rtype.trim().toLowerCase();
        return (rtype.equals("singlebed") || rtype.equals("doublebed") || rtype.equals("suite"));
    }

    public static int getRoomId(String num) {
        Connection connection = Conn.getConnection();
        int id = -1;
        try {
            if (connection != null) {
                String query1 = "SELECT roomId FROM room WHERE roomNumber=?";
                PreparedStatement pstmt1 = connection.prepareStatement(query1);
                pstmt1.setString(1, num);
                ResultSet rs = pstmt1.executeQuery();
                if (rs.next()) { // If a match is found
                    id = rs.getInt("roomId");

                }
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

    public static ArrayList<Room> readRoomDB() {
        ArrayList<Room> rooms = new ArrayList<>();
        Connection connection = Conn.getConnection();

        try {
            if (connection != null) {
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT roomNumber, roomType, roomPrice, isAvailable,isUnderMaintenance FROM room");

                while (rs.next()) {
                    String roomNumber = rs.getString("roomNumber");
                    String roomType = rs.getString("roomType");
                    int roomPrice = rs.getInt("roomPrice");
                    boolean isUnderMaintenance = (rs.getInt("isUnderMaintenance") == 1);// change 0,1 to false,true
                    boolean isAvailable = (rs.getInt("isAvailable") == 1);

                    rooms.add(new Room(roomNumber, roomType, roomPrice, isAvailable, isUnderMaintenance));
                }
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public static void insertRoomDB(String num, String type, int price, boolean isAvailable,
            boolean isUnderMaintenance) {
        Connection connection = Conn.getConnection();
        try {
            if (connection != null) {
                String query = "INSERT INTO room (roomNumber, roomType, roomPrice, isAvailable,isUnderMaintenance)"
                        + " VALUES (?, ?,?,?,?)";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setString(1, num);
                pstmt.setString(2, type);
                pstmt.setInt(3, price);
                pstmt.setBoolean(4, isAvailable);
                pstmt.setBoolean(5, isUnderMaintenance);
                pstmt.executeUpdate();
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteRoomDB(String roomNumber) {
        Connection connection = Conn.getConnection();
        try {
            if (connection != null) {

                // Delete the room using roomId
                int roomId = getRoomId(roomNumber);
                String query = "DELETE FROM room WHERE roomId = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, roomId);
                pstmt.executeUpdate();

                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateRoomDB(String roomNum, String column, String newValue, boolean isBoolean, boolean isInt) {
        Connection connection = Conn.getConnection();
        int id = Room.getRoomId(roomNum);

        try {
            if (connection != null) {
                String query = "UPDATE room SET " + column + " = ? WHERE roomId = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);

                if (isBoolean) {
                    pstmt.setBoolean(1, Boolean.parseBoolean(newValue)); // Convert String to Boolean
                } else if (isInt) {
                    pstmt.setInt(1, Integer.parseInt(newValue));
                } else {
                    pstmt.setString(1, newValue);
                }

                pstmt.setInt(2, id);
                pstmt.executeUpdate();
                pstmt.close();
                connection.close();
                System.out.println("Room updated successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
