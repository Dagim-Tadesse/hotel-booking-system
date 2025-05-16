package rmi;

import classes.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import database.Conn;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReportImp extends UnicastRemoteObject implements Report {

    public ReportImp() throws RemoteException {
        super();
    }

    @Override
    public ArrayList<String> readReportDB() throws RemoteException {
        Connection connection = Conn.getConnection();
        ArrayList<String> reports = new ArrayList<>();

        try {
            if (connection != null) {
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT guestName, phoneNumber,"
                        + "roomNumber, roomType, check_In_Date, check_Out_Date,"
                        + "paymentDate, totalPrice, paymentMethod FROM report");

                while (rs.next()) {
                    String gname = rs.getString("guestName");
                    String phoneNum = rs.getString("phoneNumber");
                    String roomNum = rs.getString("roomNumber");
                    String roomtype = rs.getString("roomType");
                    String checkin = rs.getString("check_In_Date");
                    String checkout = rs.getString("check_Out_Date");
                    String paydate = rs.getString("paymentDate");
                    String totalprice = rs.getString("totalPrice");
                    String paymethod = rs.getString("paymentMethod");

                    reports.add("Guest info: " + gname + " - " + phoneNum
                            + "\n\troom info: " + roomNum + " - " + roomtype
                            + "\n\tcheck-in-date -- check-out-date: " + checkin + " -- " + checkout
                            + "\n\ttotalPrice: " + totalprice + "$ - " + paymethod
                            + "\n\tPayment Date: " + paydate);
                }

                connection.close(); // Close connection
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reports;
    }

    @Override
    public void insertReportDB(Booking booking, String paymethod, int payPrice) throws RemoteException {
        Connection connection = Conn.getConnection();

        try {
            if (connection != null) {
                String query = "INSERT INTO report (guestId, guestName, phoneNumber, roomId,"
                        + "roomNumber, roomType, roomPrice,check_In_Date,"
                        + "check_Out_Date, totalPrice, paymentMethod) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, booking.guestId);
                pstmt.setString(2, booking.guest.name);
                pstmt.setString(3, booking.guest.contactInfo);
                pstmt.setInt(4, booking.roomId);
                pstmt.setString(5, booking.room.roomNumber);
                pstmt.setString(6, booking.room.roomType);
                pstmt.setInt(7, booking.room.roomPrice);
                pstmt.setString(8, booking.check_in_date);
                pstmt.setString(9, booking.check_out_date);
                pstmt.setInt(10, payPrice);
                pstmt.setString(11, paymethod);
                pstmt.executeUpdate();
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int dailyReportDB(String date) throws RemoteException {
        Connection connection = Conn.getConnection();
        int sum = -1;

        try {
            if (connection != null) {
                // DATE(paymentDate) extracts only the yyyy-mm-dd
                String query1 = "SELECT SUM(totalPrice) AS total_sum FROM report WHERE DATE(paymentDate) = ?";
                PreparedStatement pstmt1 = connection.prepareStatement(query1);
                pstmt1.setString(1, date);
                ResultSet rs = pstmt1.executeQuery();
                if (rs.next()) {
                    sum = rs.getInt("total_sum");
                } else {
                    System.out.println("no daily report in date: " + date);
                    connection.close();
                    return sum;
                }
                connection.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sum;
    }

    @Override
    public int countRow(String date) throws RemoteException {
        Connection connection = Conn.getConnection();
        int count = 0;

        try {
            if (connection != null) {
                // DATE(paymentDate) extracts only the yyyy-mm-dd
                String query1 = "SELECT COUNT(*) AS paycount FROM report WHERE DATE(paymentDate) = ?";
                PreparedStatement pstmt1 = connection.prepareStatement(query1);
                pstmt1.setString(1, date);
                ResultSet rs = pstmt1.executeQuery();
                if (rs.next()) {
                    count = rs.getInt("paycount");
                } else {
                    System.out.println("no daily report in date: " + date);
                    connection.close();
                    return count;
                }
                connection.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

}