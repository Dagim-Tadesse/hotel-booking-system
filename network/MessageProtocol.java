package network;

import java.util.ArrayList;
import classes.*;

public class MessageProtocol {

    public static String processMessage(String message) {
        message = message.trim().toLowerCase();

        if (message.startsWith("sign-up")) {
            String[] parts = message.split("\\s+"); // split into 3 parts: ["sign-up", "name", "phone..."]
            if (parts.length != 4) {
                return "Please use format: sign-up <FName> <LName> <Phone Number>";
            }

            String name = (parts[1].trim() + " " + parts[2].trim());
            String phone = parts[3].trim();

            if (!phone.matches("\\d{7,15}")) { // basic phone number check
                return "Invalid phone number. Please enter digits only (7-15 characters).";
            }

            Guest.insertGuestDB(name, phone);
            return "Successfully signed up!";

        } else if (message.startsWith("contact-admin")) {
            String adminMsg = message.substring("contact-admin".length()).trim();
            System.out.println("[ADMIN REQUEST] From guest: " + adminMsg); // Print for admin to see
            return "Your message has been sent to the admin. for more info please contact us.";
        }

        switch (message) {
            case "hi":
            case "hello":
                return "Hello! How can I assist you today?";

            case "available rooms":
                ArrayList<Room> rooms = Room.readRoomDB();
                StringBuilder avroom = new StringBuilder();
                for (Room r : rooms) {
                    if (r.isAvailable && !r.isUnderMaintenance) {
                        avroom.append(r.roomNumber)
                                .append(" - ")
                                .append(r.roomType)
                                .append(" - $")
                                .append(r.roomPrice)
                                .append("\n");
                    }
                }
                if (avroom.length() > 0 && avroom.charAt(avroom.length() - 1) == '\n') {
                    avroom.deleteCharAt(avroom.length() - 1);
                    return avroom.toString();
                } else {
                    return "No available rooms at the moment.";
                }

            case "new guest":
                return "please sign up using: sign-up <FName> <LName> <Phone Number>";

            case "book room":
                return "to book a room send in this format: 'contact-admin <Fullname> <roomNumber> <check-in-date> <check-out-date>'";

            case "contact admin":
                return "Please enter your message after 'contact-admin'. Example: contact-admin I want to book room 101.";

            case "help":
                return "You can ask: 'available rooms', 'new guest', 'book room', 'contact admin', or 'exit'.";

            case "exit":
                return "Goodbye! Have a nice day!";

            default:
                return "Sorry, I didn't understand that. Try typing 'help'.";
        }
    }
}
