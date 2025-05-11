package classes;

import database.Conn;
import java.util.*;
// import java.io.BufferedReader;
// import java.io.FileInputStream;
// import java.io.IOException;
// import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Guest {
    public String name;
    public String contactInfo;

    public Guest(String name, String contactInfo, boolean isNew) {
        this.name = name;
        this.contactInfo = contactInfo;
        if (isNew) {
            insertGuestDB(this.name, this.contactInfo);
        }

    }

    // public static ArrayList<Guest> read() {
    // ArrayList<Guest> guests = new ArrayList<>();
    // String GuestPath = "txt_file/guest.txt";

    // try (FileInputStream fis = new FileInputStream(GuestPath);
    // BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
    // String line, line1;
    // while ((line = reader.readLine()) != null) {
    // line1 = reader.readLine();
    // guests.add(new Guest(line, line1));
    // }
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return guests;

    // }

    public static int getGuestId(String name) {
        Connection connection = Conn.getConnection();
        int id = -1;
        try {
            if (connection != null) {
                String query1 = "SELECT guestId FROM guest WHERE guestName=?";
                PreparedStatement pstmt1 = connection.prepareStatement(query1);
                pstmt1.setString(1, name);
                ResultSet rs = pstmt1.executeQuery();
                if (rs.next()) { // If a match is found
                    id = rs.getInt("guestId");

                }
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public static ArrayList<Guest> readGuestDB() {
        ArrayList<Guest> newguests = new ArrayList<>();
        Connection connection = Conn.getConnection();
        try {
            if (connection != null) {

                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT guestName,phoneNumber FROM guest");

                while (rs.next()) {

                    String gname = rs.getString("guestName");
                    String num = rs.getString("phoneNumber");
                    newguests.add(new Guest(gname, num, false)); // Add to ArrayList
                }

                connection.close(); // Close connection
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return newguests;
    }

    public static void insertGuestDB(String name, String num) {
        Connection connection = Conn.getConnection();
        try {
            if (connection != null) {
                String query = "INSERT INTO guest (guestName, phoneNumber) VALUES (?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, num);
                pstmt.executeUpdate();
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteGuestDB(String name) {
        Connection connection = Conn.getConnection();
        int id = getGuestId(name);
        try {
            if (connection != null) {

                String query = "DELETE FROM guest WHERE guestId = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateGuestDB(String guestname, String column, String newValue) {

        Connection connection = Conn.getConnection();
        int id = Guest.getGuestId("sd");
        try {
            if (connection != null) {
                String query = "UPDATE guest SET " + column + " = ? WHERE guestId = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setString(1, newValue);
                pstmt.setInt(2, id);

                pstmt.executeUpdate();
                pstmt.close();
                connection.close();
                System.out.println("Guest details updated successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
