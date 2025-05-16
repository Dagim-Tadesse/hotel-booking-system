package network;

import java.io.*;
import java.net.Socket;

public class GuestHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    public GuestHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("Welcome to Hotel Booking Assistant. Type 'exit' to quit.");
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from guest: " + inputLine);

                if (inputLine.equalsIgnoreCase("exit")) {
                    out.println("Goodbye!");
                    break;
                }

                // ✅ Delegate logic to MessageProtocol
                String response = MessageProtocol.processMessage(inputLine);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Connection error with guest: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Guest disconnected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
