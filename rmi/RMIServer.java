package rmi;

import classes.Report;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
    public static void main(String[] args) {
        try {
            Report reportObj = new ReportImp();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("ReportService", reportObj);
            System.out.println("RMI Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
