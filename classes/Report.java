package classes;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface Report extends Remote {

    // Return all reports as list of strings
    ArrayList<String> readReportDB() throws RemoteException;

    // insert into database
    void insertReportDB(Booking booking, String payMethod, int payPrice) throws RemoteException;

    // Generate daily income report by date (format: YYYY-MM-DD)
    int dailyReportDB(String date) throws RemoteException;

    // Count number of payments made on a specific date
    int countRow(String date) throws RemoteException;
}