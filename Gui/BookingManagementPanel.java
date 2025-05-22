
package Gui;

import classes.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class BookingManagementPanel extends JPanel {
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, markPaidButton, refreshButton, backButton;

    public BookingManagementPanel(CardLayout cardLayout, JPanel parentPanel) {

        setLayout(new BorderLayout());

        // Table setup
        String[] columns = { "Booking ID", "Guest", "Room", "Check-in", "Check-out", "Total Price", "Paid" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells non-editable
            }
        };
        bookingTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookingTable);

        // Button panel
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Booking");
        editButton = new JButton("Edit Booking");
        deleteButton = new JButton("Delete Booking");
        markPaidButton = new JButton("Mark as Paid/Unpaid");
        refreshButton = new JButton("Refresh");
        backButton = new JButton("Back");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(markPaidButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load bookings
        loadBookings();

        // Button actions
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteSelectedBooking());
        markPaidButton.addActionListener(e -> togglePaidStatus());
        refreshButton.addActionListener(e -> loadBookings());
        backButton.addActionListener(e -> {
            cardLayout.show(parentPanel, "MainMenu");
        });
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        ArrayList<Booking> bookings = Booking.readBookingDB();
        for (Booking b : bookings) {
            tableModel.addRow(new Object[] {
                    b.bookingId,
                    b.guest != null ? b.guest.name : "",
                    b.room != null ? b.room.roomNumber : "",
                    b.check_in_date,
                    b.check_out_date,
                    b.totalPrice,
                    b.isPaid ? "Yes" : "No"
            });
        }
    }

    private void showAddDialog() {
        ArrayList<Guest> guests = Guest.readGuestDB();
        ArrayList<Room> rooms = Room.readRoomDB();

        JComboBox<String> guestCombo = new JComboBox<>();
        for (Guest g : guests)
            guestCombo.addItem(g.name);

        JComboBox<String> roomCombo = new JComboBox<>();
        for (Room r : rooms)
            roomCombo.addItem(r.roomNumber);

        JTextField checkInField = new JTextField(10);
        JTextField checkOutField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Guest:"));
        panel.add(guestCombo);
        panel.add(new JLabel("Room:"));
        panel.add(roomCombo);
        panel.add(new JLabel("Check-in (yyyy-MM-dd):"));
        panel.add(checkInField);
        panel.add(new JLabel("Check-out (yyyy-MM-dd):"));
        panel.add(checkOutField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Booking", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String guestName = (String) guestCombo.getSelectedItem();
            String roomNumber = (String) roomCombo.getSelectedItem();
            String checkIn = checkInField.getText().trim();
            String checkOut = checkOutField.getText().trim();

            // Validation
            if (guestName == null || roomNumber == null || checkIn.isEmpty() || checkOut.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Validation logic is correct:
            if (!Booking.isValidDate(checkIn, true, true, null, null) ||
                    !Booking.isValidDate(checkOut, true, false, checkIn, null)) {
                JOptionPane.showMessageDialog(this, "Invalid dates.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Guest guest = guests.stream().filter(g -> g.name.equals(guestName)).findFirst().orElse(null);
            Room room = rooms.stream().filter(r -> r.roomNumber.equals(roomNumber)).findFirst().orElse(null);
            if (guest == null || room == null) {
                JOptionPane.showMessageDialog(this, "Invalid guest or room.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            new Booking(room, guest, checkIn, checkOut); // This inserts into DB
            loadBookings();
        }
    }

    private void showEditDialog() {
        int row = bookingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a booking to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String guestName = (String) tableModel.getValueAt(row, 1);
        String roomNumber = (String) tableModel.getValueAt(row, 2);
        String checkIn = (String) tableModel.getValueAt(row, 3);
        String checkOut = (String) tableModel.getValueAt(row, 4);

        ArrayList<Guest> guests = Guest.readGuestDB();
        ArrayList<Room> rooms = Room.readRoomDB();
        ArrayList<Booking> bookings = Booking.readBookingDB();

        JComboBox<String> guestCombo = new JComboBox<>();
        for (Guest g : guests)
            guestCombo.addItem(g.name);
        guestCombo.setSelectedItem(guestName);

        JComboBox<String> roomCombo = new JComboBox<>();
        for (Room r : rooms)
            roomCombo.addItem(r.roomNumber);
        roomCombo.setSelectedItem(roomNumber);

        JTextField checkInField = new JTextField(checkIn, 10);
        JTextField checkOutField = new JTextField(checkOut, 10);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Guest:"));
        panel.add(guestCombo);
        panel.add(new JLabel("Room:"));
        panel.add(roomCombo);
        panel.add(new JLabel("Check-in (yyyy-MM-dd):"));
        panel.add(checkInField);
        panel.add(new JLabel("Check-out (yyyy-MM-dd):"));
        panel.add(checkOutField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Booking", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newGuestName = (String) guestCombo.getSelectedItem();
            String newRoomNumber = (String) roomCombo.getSelectedItem();
            String newCheckIn = checkInField.getText().trim();
            String newCheckOut = checkOutField.getText().trim();

            if (newGuestName == null || newRoomNumber == null || newCheckIn.isEmpty() || newCheckOut.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Validation logic is correct:
            if (!Booking.isValidDate(newCheckIn, false, true, checkIn, checkOut) ||
                    !Booking.isValidDate(newCheckOut, false, false, newCheckIn, checkOut)) {
                JOptionPane.showMessageDialog(this, "Invalid dates.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update guest or room if changed
            if (!newGuestName.equals(guestName)) {
                Booking.updateBookingInDB(guestName, roomNumber, "guestId", newGuestName, true);
            }
            if (!newRoomNumber.equals(roomNumber)) {
                Booking.updateBookingInDB(guestName, roomNumber, "roomId", newRoomNumber, true);
            }
            if (!newCheckIn.equals(checkIn)) {
                Booking.updateBookingInDB(newGuestName, newRoomNumber, "check_In_Date", newCheckIn, false);

                for (Booking booking : bookings) {
                    if (booking.room.roomNumber.equals(roomNumber)) {
                        if (!booking.isPaid) {
                            booking.totalPrice = booking.totalPrice - (booking.room.roomPrice
                                    * Booking.countdays(checkIn, newCheckIn));

                            Booking.updatePriceDB(booking.guest.name, booking.room.roomNumber, booking.totalPrice);
                            if (booking.totalPrice <= 0) {
                                booking.isPaid = true;
                                booking.totalPrice = 0;
                                Booking.updatePriceDB(booking.guest.name, booking.room.roomNumber, 0);
                            }
                        }
                    }
                }
            }
            if (!newCheckOut.equals(checkOut)) {
                Booking.updateBookingInDB(newGuestName, newRoomNumber, "check_Out_Date", newCheckOut, false);

                for (Booking booking : bookings) {
                    if (booking.room.roomNumber.equals(roomNumber)) {
                        if (booking.isPaid) {
                            booking.totalPrice = booking.room.roomPrice
                                    * Booking.countdays(checkOut, newCheckOut);
                            booking.isPaid = false;
                            System.out.println(checkOut);
                            System.out.println(newCheckOut);
                            Booking.updatePriceDB(booking.guest.name, booking.room.roomNumber, booking.totalPrice);
                            Booking.updatePayStatusDB(booking.guest.name, booking.room.roomNumber, booking.isPaid);
                        } else {
                            booking.totalPrice = booking.totalPrice + (booking.room.roomPrice
                                    * Booking.countdays(checkOut, newCheckOut));

                            Booking.updatePriceDB(booking.guest.name, booking.room.roomNumber, booking.totalPrice);

                            if (booking.totalPrice <= 0) {
                                booking.isPaid = true;
                                booking.totalPrice = 0;
                                Booking.updatePriceDB(booking.guest.name, booking.room.roomNumber, 0);
                            }
                        }

                    }
                }

            }
            loadBookings();
        }
    }

    private void deleteSelectedBooking() {
        int row = bookingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a booking to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String guestName = (String) tableModel.getValueAt(row, 1);
        String roomNumber = (String) tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this booking?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Booking.deleteBookingDB(guestName, roomNumber);
            loadBookings();
        }
    }

    private void togglePaidStatus() {
        int row = bookingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a booking to update payment status.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String guestName = (String) tableModel.getValueAt(row, 1);
        String roomNumber = (String) tableModel.getValueAt(row, 2);
        boolean isPaid = "Yes".equals(tableModel.getValueAt(row, 6));
        Booking.updatePayStatusDB(guestName, roomNumber, !isPaid);
        loadBookings();
    }
}
