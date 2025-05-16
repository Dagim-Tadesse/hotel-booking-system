package network;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class GuestClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in);) {
            System.out.println("Connected to the Hotel Admin Server.");

            String response;
            System.out.println(in.readLine());
            while (true) {
                System.out.print("You: ");
                String message = scanner.nextLine();
                out.println(message);

                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Disconnected.");
                    break;
                }

                response = in.readLine();
                System.out.println("Admin: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
