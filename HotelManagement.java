import classes.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HotelManagement {
    static ArrayList<Room> rooms = Room.readRoomDB();
    static ArrayList<Guest> guests = Guest.readGuestDB();
    static ArrayList<Booking> bookings = Booking.readBookingDB();
    static ArrayList<String> reports = Report.readReportDB();

    // to set the date
    // Get the current date and time
    static LocalDateTime currentDateTime = LocalDateTime.now(); // Define a formatter to display the date and time in a
    // readable format for only the date
    static DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Format the current date

    static String formattedDate = currentDateTime.format(dateformatter); // Print the current date

    public static void main(String[] args) {
        System.out.println("Program started...");
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            displayMainMenu();
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();

            if (choice == 6) {
                displayExitMenu();
                int exitChoice = scanner.nextInt();
                handleExitMenuChoice(exitChoice);
                if (exitChoice == 1)
                    break; // Exit loop to terminate program
            } else {
                handleMainMenuChoice(choice, scanner);
            }

        }

        scanner.close();
    }

    public static void displayMainMenu() {
        // clearScreen();
        System.out.println("----------------------------------");
        System.out.println("      HOTEL BOOKING MANAGEMENT    ");
        System.out.println("----------------------------------");
        System.out.println("1. Room Management");
        System.out.println("2. Guest Management");
        System.out.println("3. Booking Management");
        System.out.println("4. Billing and Payments");
        System.out.println("5. Reports and Analytics");
        System.out.println("6. Exit");
        System.out.println("----------------------------------");
    }

    public static void handleMainMenuChoice(int choice, Scanner scanner) {
        switch (choice) {
            case 1:
                displayRoomMenu(scanner);
                break;
            case 2:
                displayGuestMenu(scanner);
                break;
            case 3:
                displayBookingMenu(scanner);
                break;
            case 4:
                displayBillingMenu(scanner);
                break;
            case 5:
                displayReportsMenu(scanner);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    public static void displayRoomMenu(Scanner scanner) {
        System.out.println("\nROOM MANAGEMENT");
        System.out.println("----------------");
        System.out.println("1. Add Room");
        System.out.println("2. List All Rooms");
        System.out.println("3. Search Room by room number");
        System.out.println("4. Update Room Details");
        System.out.println("5. Delete Room");
        System.out.println("6. Back to Main Menu");

        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();// when scanning it consumes only the intiger but not the '\n'(which exists in
                                       // every input scanner)
        scanner.nextLine();// to consume the leftover '\n'
        handleRoomMenuChoice(choice, scanner);
    }

    public static void handleRoomMenuChoice(int choice, Scanner scanner) {
        switch (choice) {
            case 1:
                addRoom(scanner);
                break;
            case 2:
                listAllRooms();
                break;
            case 3:
                searchRoomByID(scanner);
                break;
            case 4:
                updateRoomDetails(scanner);
                break;
            case 5:
                deleteRoom(scanner);
                break;
            case 6:
                System.out.println("Returning to Main Menu...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    public static void addRoom(Scanner scanner) {
        System.out.print("Enter room number to add: ");
        String roomNumber = scanner.nextLine();

        for (Room room : rooms) {
            if (room.roomNumber.equals(roomNumber)) {
                System.out.println("Room number: " + roomNumber + " already exists. try again.");
                return;
            }
        }

        System.out.println("Enter the room type(singlebed,doublebed,suite):");
        String roomtype = scanner.nextLine();

        roomtype = roomtype.toLowerCase();// to change uppercase to lowercase

        if (Room.roomtypecheck(roomtype)) {
            rooms.add(new Room(roomNumber, roomtype));
            System.out.println("Room " + roomNumber + " added.");
        } else {
            System.out.println("ERROR wrong room type, try again");
        }
    }

    public static void listAllRooms() {
        System.out.println("All Rooms:");
        for (Room room : rooms) {
            System.out.println("Room " + room.roomNumber + " - " + room.roomType + " - "
                    + ((room.isAvailable) ? "available" : "booked"));
        }
    }

    public static void searchRoomByID(Scanner scanner) {
        System.out.print("Enter room number to search: ");
        String roomNumber = scanner.nextLine();
        for (Room room : rooms) {
            if (room.roomNumber.equals(roomNumber)) {
                room.checkAvailability();
                return;
            }
        }
        System.out.println("Room " + roomNumber + " not found.");
    }

    public static void updateRoomDetails(Scanner scanner) {
        System.out.print("Enter room number to update: ");
        String roomNumber = scanner.nextLine();
        for (Room room : rooms) {
            if (room.roomNumber.equals(roomNumber)) {
                System.out.println("Which field would you like to update?");
                System.out.println("1. Room Number");
                System.out.println("2. Room Type");
                System.out.println("3. Availability");
                System.out.println("4. Maintenance Status");
                System.out.println("5. Room Price");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                String column = "";
                String newValue = "";
                boolean isBoolean = false;
                boolean isInt = false;

                switch (choice) {
                    case 1:
                        column = "roomNumber";
                        System.out.print("Enter new Room Number: ");
                        newValue = scanner.nextLine();
                        room.roomNumber = newValue;
                        break;
                    case 2:
                        column = "roomType";
                        System.out.print("Enter new Room Type (singlebed, doublebed, suite): ");
                        newValue = scanner.nextLine();
                        if (Room.roomtypecheck(newValue)) {
                            room.roomType = newValue;
                            break;
                        } else {
                            System.out.println("wrong roomtype ,try again!!!");
                            return;
                        }

                    case 3:
                        column = "isAvailable";
                        System.out.print("Enter Availability (true/false): ");
                        newValue = scanner.nextLine();
                        room.isAvailable = Boolean.parseBoolean(newValue);
                        isBoolean = true;
                        break;
                    case 4:
                        column = "isUnderMaintenance";
                        System.out.print("Enter Maintenance Status (true/false): ");
                        newValue = scanner.nextLine();
                        room.isUnderMaintenance = Boolean.parseBoolean(newValue);
                        isBoolean = true;
                        break;
                    case 5:
                        column = "roomPrice";
                        System.out.println("Enter new room Price: ");
                        newValue = scanner.nextLine();
                        room.roomPrice = Integer.parseInt(newValue);
                        isInt = true;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                        return;
                }

                // Pass the OLD roomNumber for update
                Room.updateRoomDB(roomNumber, column, newValue, isBoolean, isInt);

                return;
            }
        }
        System.out.println("Room " + roomNumber + " not found.");
    }

    public static void deleteRoom(Scanner scanner) {
        System.out.print("Enter room number to delete: ");
        String roomNumber = scanner.nextLine();
        for (Room room : rooms) {
            if (room.roomNumber.equals(roomNumber)) {
                Room.deleteRoomDB(roomNumber);
                rooms.remove(room);
                bookings.removeIf(booking -> booking.room.roomNumber.equals(roomNumber));
                System.out.println("Room " + roomNumber + " deleted.");
                return;
            }

        }
        System.out.println("Room " + roomNumber + " not found.");
    }

    public static void displayGuestMenu(Scanner scanner) {
        System.out.println("\nGUEST MANAGEMENT");
        System.out.println("----------------");
        System.out.println("1. Add Guest");
        System.out.println("2. List All Guests");
        System.out.println("3. Search Guest by Name");
        System.out.println("4. Update Guest Details");
        System.out.println("5. Delete Guest");
        System.out.println("6. Back to Main Menu");

        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        handleGuestMenuChoice(choice, scanner);
    }

    public static void handleGuestMenuChoice(int choice, Scanner scanner) {
        switch (choice) {
            case 1:
                addGuest(scanner);
                break;
            case 2:
                listAllGuests();
                break;
            case 3:
                searchGuestByName(scanner);
                break;
            case 4:
                updateGuestDetails(scanner);
                break;
            case 5:
                deleteGuest(scanner);
                break;
            case 6:
                System.out.println("Returning to Main Menu...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    public static void addGuest(Scanner scanner) {
        System.out.print("Enter Full name to add: ");
        String name = scanner.nextLine().trim().toLowerCase();
        System.out.print("Enter phone-Number('09--' or '+251--') : ");
        String contactInfo = scanner.nextLine();
        if ((contactInfo.substring(0, 2).equals("09") &&
                (contactInfo.length() == 10)) ||
                (contactInfo.substring(0, 4).equals("+251") &&
                        (contactInfo.length() == 13))) {
            Guest guest = new Guest(name, contactInfo, true);
            guests.add(guest);
            System.out.println("Guest " + name + " added.");
            return;
        }

        System.out.println("invalid phone-Number entered , please try again.");

    }

    public static void listAllGuests() {
        System.out.println("All Guests:");
        for (Guest guest : guests) {
            System.out.println("Guest Name: " + guest.name + " , Contact-Info: " + guest.contactInfo);
        }

    }

    public static void searchGuestByName(Scanner scanner) {
        System.out.print("Enter guest name to search: ");
        String name = scanner.nextLine().trim().toLowerCase();
        for (Guest guest : guests) {
            if (guest.name.equals(name)) {
                System.out.println("Guest " + name + " - " + guest.contactInfo + " found.");
                return;
            }
        }
        System.out.println("Guest " + name + " not found.");
    }

    public static void updateGuestDetails(Scanner scanner) {
        System.out.print("Enter guest name to update: ");
        String name = scanner.nextLine().trim().toLowerCase();
        for (Guest guest : guests) {
            if (guest.name.equals(name)) {
                System.out.println("Which field would you like to update?");
                System.out.println("1. Guest Name");
                System.out.println("2. Phone Number");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                String column = "";
                String newValue = "";

                switch (choice) {
                    case 1:
                        column = "guestName";
                        System.out.print("Enter new Guest Name: ");
                        newValue = scanner.nextLine().trim().toLowerCase();
                        guest.name = newValue;
                        break;
                    case 2:
                        column = "phoneNumber";
                        System.out.print("Enter new Phone Number: ");
                        newValue = scanner.nextLine();
                        if ((newValue.substring(0, 2).equals("09") &&
                                (newValue.length() == 10)) ||
                                (newValue.substring(0, 4).equals("+251") &&
                                        (newValue.length() == 13))) {

                            guest.contactInfo = newValue;
                            break;
                        }
                        System.out.println("invalid phone-Number entered , please try again.");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                        return;
                }

                Guest.updateGuestDB(name, column, newValue);
                return;
            }
        }
        System.out.println("Guest " + name + " not found.");
    }

    public static void deleteGuest(Scanner scanner) {
        System.out.print("Enter guest name to delete: ");
        String name = scanner.nextLine().trim().toLowerCase();
        for (Guest guest : guests) {
            if (guest.name.equals(name)) {
                Guest.deleteGuestDB(name);
                guests.remove(guest);
                bookings.removeIf(booking -> booking.guest.name.equals(name));
                System.out.println("Guest " + name + " deleted.");
                return;
            }
        }
        System.out.println("Guest " + name + " not found.");
    }

    public static void displayBookingMenu(Scanner scanner) {
        System.out.println("\nBOOKING MANAGEMENT");
        System.out.println("------------------");
        System.out.println("1. Add Booking");
        System.out.println("2. List All Bookings");
        System.out.println("3. Search Booking by Room Number");
        System.out.println("4. Update Booking Details");
        System.out.println("5. Delete Booking");
        System.out.println("6. Back to Main Menu");

        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        handleBookingMenuChoice(choice, scanner);
    }

    public static void handleBookingMenuChoice(int choice, Scanner scanner) {
        switch (choice) {
            case 1:
                addBooking(scanner);
                break;
            case 2:
                listAllBookings();
                break;
            case 3:
                searchBookingByRoomNumber(scanner);
                break;
            case 4:
                updateBookingDetails(scanner);
                break;
            case 5:
                deleteBooking(scanner);
                break;
            case 6:
                System.out.println("Returning to Main Menu...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    public static void addBooking(Scanner scanner) {
        System.out.print("Enter room number to book: ");
        String roomNumber = scanner.nextLine();
        System.out.print("Enter guest name: ");
        String guestName = scanner.nextLine().trim().toLowerCase();

        Room room = null;
        for (Room r : rooms) {
            if (r.roomNumber.equals(roomNumber)) {
                room = r;
                break;
            }
        }

        if (room == null) {
            System.out.println("Room " + roomNumber + " not found.");
            return;
        }

        Guest guest = null;
        for (Guest g : guests) {
            if (g.name.equals(guestName)) {
                guest = g;
                break;
            }
        }

        if (guest == null) {
            System.out.println("Guest " + guestName + " not found.");
            return;
        }
        if (!room.isAvailable || room.isUnderMaintenance) {
            System.out.println("room is booked or under maintenance.");
            return;
        }
        System.out.print("Enter check-in-date(yyyy-mm-dd) and within 30 days of now: ");
        String checkIn = scanner.nextLine();

        if (Booking.isValidDate(checkIn, true, true, "", "")) {
            System.out.println("ACCEPTED.");
        } else {
            System.out.println("Invalid date format or out of allowed range (within 30 days).");
            return;
        }

        System.out.print("Enter check-out-date(yyyy-mm-dd) and within 30 days of check-in date: ");
        String checkOut = scanner.next();

        if (Booking.isValidDate(checkOut, true, false, checkIn, "")) {
            System.out.println("ACCEPTED.");
        } else {
            System.out.println("Invalid date format or out of allowed range (within 30 days).");
            return;
        }

        bookings.add(new Booking(room, guest, checkIn, checkOut));
        room.bookRoom();
    }

    public static void listAllBookings() {
        System.out.println("All Bookings:");
        for (Booking booking : bookings) {
            System.out.println("Room " + booking.room.roomNumber + " booked by " + booking.guest.name);
            if (booking.isPaid)
                System.out.println("paid.");
            else
                System.out.println("not paid.");
        }
    }

    public static void searchBookingByRoomNumber(Scanner scanner) {
        System.out.print("Enter room number to search booking: ");
        String roomNumber = scanner.nextLine();
        for (Booking booking : bookings) {
            if (booking.room.roomNumber.equals(roomNumber)) {
                System.out.println("Room " + roomNumber + " booked by " + booking.guest.name);
                if (booking.isPaid)
                    System.out.println("paid.");
                else
                    System.out.println("not paid.");
                return;
            }
        }
        System.out.println("Booking for room " + roomNumber + " not found.");
    }

    public static void updateBookingDetails(Scanner scanner) {
        System.out.print("Enter room number: ");
        String roomNumber = scanner.nextLine();
        for (Booking booking : bookings) {
            if (booking.room.roomNumber.equals(roomNumber)) {
                String gname = booking.guest.name;

                System.out.println("Which field would you like to update?");
                System.out.println("1. Check-In Date");
                System.out.println("2. Check-Out Date");
                System.out.println("3. Guest Name");
                System.out.println("4. Room Number");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                String column = "";
                String newValue = "";
                boolean isGuestOrRoom = false;

                switch (choice) {

                    case 1:
                        column = "check_In_Date";
                        System.out.print(
                                "Enter new Check-In Date (" + booking.check_in_date + ") within 30days from now: ");
                        newValue = scanner.nextLine();

                        if (!Booking.isValidDate(newValue, false, true, booking.check_in_date,
                                booking.check_out_date)) {
                            System.out.println("Invalid date format or out of allowed range (within 30 days).");
                            return;
                        }
                        String oldcheckin = booking.check_in_date;
                        booking.check_in_date = newValue;

                        if (!booking.isPaid) {
                            booking.totalPrice = booking.totalPrice - (booking.room.roomPrice
                                    * Booking.countdays(oldcheckin, booking.check_in_date));

                            Booking.updatePriceDB(booking.guest.name, roomNumber, booking.totalPrice);
                            if (booking.totalPrice <= 0) {
                                booking.isPaid = true;
                                booking.totalPrice = 0;
                                Booking.updatePriceDB(booking.guest.name, roomNumber, 0);
                            }

                        } else {
                            System.out.println("since you already paid the total price ,there is no refunds.");
                        }
                        break;

                    case 2:
                        column = "check_Out_Date";
                        System.out.print("Enter new Check-Out Date (" + booking.check_out_date
                                + ") within 30days from check-in date: ");
                        newValue = scanner.nextLine();

                        if (!Booking.isValidDate(newValue, false, false, booking.check_in_date,
                                booking.check_out_date)) {
                            System.out.println("Invalid date format or out of allowed range (within 30 days).");
                            return;
                        }
                        String oldcheckout = booking.check_out_date;
                        booking.check_out_date = newValue;
                        if (booking.isPaid) {
                            booking.totalPrice = booking.room.roomPrice
                                    * Booking.countdays(oldcheckout, booking.check_out_date);
                            booking.isPaid = false;

                            Booking.updatePriceDB(booking.guest.name, roomNumber, booking.totalPrice);
                            Booking.updatePayStatusDB(booking.guest.name, roomNumber, booking.isPaid);
                        } else {
                            booking.totalPrice = booking.totalPrice + (booking.room.roomPrice
                                    * Booking.countdays(oldcheckout, booking.check_out_date));

                            Booking.updatePriceDB(booking.guest.name, roomNumber, booking.totalPrice);

                            if (booking.totalPrice <= 0) {
                                booking.isPaid = true;
                                booking.totalPrice = 0;
                                Booking.updatePriceDB(booking.guest.name, roomNumber, 0);
                            }
                        }
                        break;

                    case 3:
                        column = "guestId"; // Need to convert name to ID
                        System.out.print("Enter new Guest Name(" + booking.guest.name + ") : ");
                        newValue = scanner.nextLine().trim().toLowerCase();
                        isGuestOrRoom = true;

                        Guest newGuest = null;
                        for (Guest g : guests) {
                            if (g.name.equals(newValue)) {
                                newGuest = g;
                                break;
                            }
                        }

                        if (newGuest == null) {
                            System.out.println("Guest " + newValue + " not found.");
                            return;
                        }

                        booking.guest = newGuest;
                        break;

                    case 4:
                        column = "roomId"; // Need to convert room number to ID
                        System.out.print("Enter new Room Number(of the same roomtype): ");
                        newValue = scanner.nextLine();
                        isGuestOrRoom = true;

                        Room newRoom = null;
                        for (Room r : rooms) {
                            if (r.roomNumber.equals(newValue)) {
                                newRoom = r;
                                break;
                            }
                        }

                        if (newRoom == null) {
                            System.out.println("Room " + newValue + " not found.");
                            return;
                        }
                        if (booking.room.roomType.equals(newRoom.roomType)) {
                            roomNumber = booking.room.roomNumber;
                            booking.room = newRoom;
                        } else {
                            System.out.println("A different room type is not allowed!!!");
                            return;
                        }

                        break;

                    default:
                        System.out.println("Invalid choice.");
                        return;
                }

                Booking.updateBookingInDB(gname, roomNumber, column, newValue, isGuestOrRoom);
                return;
            }
        }
        System.out.println("Booking for room: " + roomNumber + " not found.");
    }

    public static void deleteBooking(Scanner scanner) {
        System.out.println("fill the following details to delete the booking");
        System.out.println("Enter room number: ");
        String roomNumber = scanner.nextLine();
        System.out.println("Enter guest name:");
        String name = scanner.nextLine().trim().toLowerCase();

        for (Booking booking : bookings) {
            if (booking.room.roomNumber.equals(roomNumber) && booking.guest.name.equals(name)) {

                Booking.deleteBookingDB(name, roomNumber);
                booking.room.isAvailable = true;
                bookings.remove(booking);
                System.out.println("Booking for room: " + roomNumber + " and guest: " + name + " has been deleted.");
                return;
            }
        }
        System.out.println("Booking is not found.");
    }

    public static void displayExitMenu() {
        System.out.println("\nEXIT MENU");
        System.out.println("------------------");
        System.out.println("1. Exit");
        System.out.println("2. Cancel");
    }

    public static void handleExitMenuChoice(int choice) {
        switch (choice) {
            case 1:
                System.out.println("Exiting...");
                System.exit(0);
                break;
            case 2:
                System.out.println("Returning to Main Menu...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    public static void displayBillingMenu(Scanner scanner) {
        System.out.println("\nBILLING AND PAYMENTS");
        System.out.println("------------------");
        System.out.println("1. Generate Invoice");
        System.out.println("2. Process Payment");
        System.out.println("3. Back to Main Menu");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        handleBillingMenuChoice(choice, scanner);
    }

    public static void handleBillingMenuChoice(int choice, Scanner scanner) {
        switch (choice) {
            case 1:
                generateInvoice(scanner);
                break;
            case 2:
                processPayment(scanner);
                break;
            case 3:
                System.out.println("Returning to Main Menu...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    public static void generateInvoice(Scanner scanner) {
        System.out.print("Enter room number: ");
        String roomNumber = scanner.nextLine();

        for (Booking booking : bookings) {
            if (booking.room.roomNumber.equals(roomNumber)) {
                if (!booking.isPaid) {
                    System.out.println("Room " + booking.room.roomNumber + " booked by " + booking.guest.name + " - "
                            + booking.room.roomType + "\nTotal price: " + booking.totalPrice + "$.");
                    System.out.println("Invoice generated for room " + roomNumber + ".");
                } else {
                    System.out.println(
                            "Room " + booking.room.roomNumber + " booked by " + booking.guest.name + " - is paid.");
                }
                return;
            }
        }
        System.out.println("The room doesn't exist.");
    }

    public static void processPayment(Scanner scanner) {

        System.out.print("Enter room number: ");
        String roomNumber = scanner.nextLine();

        for (Booking booking : bookings) {
            if (booking.room.roomNumber.equals(roomNumber) && !booking.isPaid) {

                System.out.print("Enter the payment method(cash, telebirr, cbe, awash bank, boa, dashen bank): ");
                String payMethod = scanner.nextLine().trim().toLowerCase();
                if (!(payMethod.equals("cash") || payMethod.equals("telebirr") || payMethod.equals("cbe")
                        || payMethod.equals("awash bank") || payMethod.equals("boa")
                        || payMethod.equals("dashen bank"))) {
                    System.out.println("entered wrong payment method , try again.");
                    return;
                }
                System.out.print("Enter the amount you want to pay: ");
                int amount = scanner.nextInt();

                booking.totalPrice = booking.totalPrice - amount;
                Report.insertReportDB(booking, payMethod, booking.totalPrice);
                Booking.updatePriceDB(booking.guest.name, roomNumber, booking.totalPrice);
                if (booking.totalPrice <= 0) {
                    booking.isPaid = true;
                    booking.totalPrice = 0;
                    Booking.updatePriceDB(booking.guest.name, roomNumber, 0);
                }
                // -------------------------------------
                reports.add("Guest info: " + booking.guest.name + " - " + booking.guest.contactInfo + "\n"
                        + "\troom info: " + booking.room.roomNumber + " - " + booking.room.roomType + "\n"
                        + "\tcheck-in-date -- check-out-date: " + booking.check_in_date + " -- "
                        + booking.check_out_date + "\n"
                        + "\ttotalPrice: " + booking.totalPrice + "$ - " + payMethod + " -- " + formattedDate);

                System.out.println(
                        "Payment of " + booking.room.roomNumber + " - " + booking.guest.name + " has been processed.");
                return;
            }
        }
        System.out.println("The booking doesn't exist or has been paid.");
    }

    public static void displayReportsMenu(Scanner scanner) {
        System.out.println("\nREPORTS AND ANALYTICS");
        System.out.println("------------------");
        System.out.println("1. View Payment History");
        System.out.println("2. Daily Sales Report");
        System.out.println("3. Back to Main Menu");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        handleReportsMenuChoice(choice, scanner);
    }

    public static void handleReportsMenuChoice(int choice, Scanner scanner) {
        switch (choice) {
            case 1:
                viewPaymentHistory(scanner);
                break;
            case 2:
                generateDailySalesReport(scanner);
                break;

            case 3:
                System.out.println("Returning to Main Menu...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    public static void viewPaymentHistory(Scanner scanner) {
        System.out.println("Payment History:"); // Logic to display payment history
        for (String report : reports) {
            System.out.println(report);
            System.out.println("");// for spacing (\n)
        }

    }

    public static void generateDailySalesReport(Scanner scanner) {

        System.out.print("Enter a date (yyyy-MM-dd): ");
        String datestr = scanner.nextLine();

        int totalSales = Report.dailyReportDB(datestr);
        int count = Report.countRow(datestr);
        double averageSales = 0;
        if (count > 0) {
            averageSales = (double) totalSales / count;
        }

        System.out.println("Daily Sales Report:");
        System.out.println("Total Sales: " + totalSales + "$");
        System.out.println("Average Sales: " + averageSales + "$");
    }

}