package Gui;

import classes.Booking;
import classes.Room;
import classes.Guest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class BookingManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    public BookingManagementPanel(CardLayout cardLayout, JPanel mainContainer) {
        setLayout(new BorderLayout());

        // Table Columns
        String[] columnNames = { "Booking ID", "Guest Name", "Room Number", "Check-In", "Check-Out", "Total Price",
                "Paid" };

        // Create Table Model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return (column == 6) ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        loadBookings();

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Booking");
        JButton deleteButton = new JButton("Delete Booking");
        JButton updateButton = new JButton("Update Booking");
        JButton backButton = new JButton("Back to Main Menu");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        addButton.addActionListener(e -> addBooking());

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String guestName = tableModel.getValueAt(row, 1).toString();
                String roomNumber = tableModel.getValueAt(row, 2).toString();
                Booking.deleteBookingDB(guestName, roomNumber);
                tableModel.removeRow(row);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a booking to delete.");
            }
        });

        updateButton.addActionListener(e -> handleUpdateBooking());

        backButton.addActionListener(e -> cardLayout.show(mainContainer, "MainMenu"));
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        ArrayList<Booking> bookings = Booking.readBookingDB();
        for (Booking b : bookings) {
            Object[] row = {
                    b.bookingId,
                    b.guest.name,
                    b.room.roomNumber,
                    b.check_in_date,
                    b.check_out_date,
                    b.totalPrice,
                    b.isPaid
            };
            tableModel.addRow(row);
        }
    }

    private void addBooking() {
        JTextField guestField = new JTextField();
        JTextField roomField = new JTextField();
        JTextField checkinField = new JTextField("yyyy-MM-dd");
        JTextField checkoutField = new JTextField("yyyy-MM-dd");
        ArrayList<Room> rooms = Room.readRoomDB();
        ArrayList<Guest> guests = Guest.readGuestDB();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Guest Name:"));
        panel.add(guestField);
        panel.add(new JLabel("Room Number:"));
        panel.add(roomField);
        panel.add(new JLabel("Check-In Date:"));
        panel.add(checkinField);
        panel.add(new JLabel("Check-Out Date:"));
        panel.add(checkoutField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Booking", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String guestName = guestField.getText().trim();
            String roomNumber = roomField.getText().trim();
            String checkIn = checkinField.getText().trim();
            String checkOut = checkoutField.getText().trim();

            Room room = null;
            for (Room r : rooms)
                if (r.roomNumber.equals(roomNumber)) {
                    room = r;
                    break;
                }
            Guest guest = null;
            for (Guest g : guests)
                if (g.name.equals(guestName)) {
                    guest = g;
                    break;
                }

            if (room == null || guest == null) {
                JOptionPane.showMessageDialog(this, "Invalid guest or room.");
                return;
            }
            if (Booking.isValidDate(checkIn, true, true, "", "")
                    && Booking.isValidDate(checkOut, true, false, checkIn, "")) {
                new Booking(room, guest, checkIn, checkOut);
                loadBookings();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid dates entered.");
            }
        }
    }

    private void handleUpdateBooking() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to update.");
            return;
        }
        String guestName = tableModel.getValueAt(row, 1).toString();
        String roomNumber = tableModel.getValueAt(row, 2).toString();

        String[] options = { "Check-In Date", "Check-Out Date", "Guest Name", "Room Number" };
        String choice = (String) JOptionPane.showInputDialog(this, "Select field to update:",
                "Update Booking", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == null)
            return;

        String column = "";
        String newValue = null;
        boolean isGuestOrRoom = false;

        switch (choice) {
            case "Check-In Date":
                column = "check_In_Date";
                newValue = JOptionPane.showInputDialog(this, "Enter new check-in date (yyyy-MM-dd):");
                if (!Booking.isValidDate(newValue, false, true,
                        tableModel.getValueAt(row, 3).toString(), tableModel.getValueAt(row, 4).toString())) {
                    JOptionPane.showMessageDialog(this, "Invalid date.");
                    return;
                }
                break;
            case "Check-Out Date":
                column = "check_Out_Date";
                newValue = JOptionPane.showInputDialog(this, "Enter new check-out date (yyyy-MM-dd):");
                if (!Booking.isValidDate(newValue, false, false,
                        tableModel.getValueAt(row, 3).toString(), tableModel.getValueAt(row, 4).toString())) {
                    JOptionPane.showMessageDialog(this, "Invalid date.");
                    return;
                }
                break;
            case "Guest Name":
                column = "guestId";
                newValue = JOptionPane.showInputDialog(this, "Enter new guest name:");
                isGuestOrRoom = true;
                break;
            case "Room Number":
                column = "roomId";
                newValue = JOptionPane.showInputDialog(this, "Enter new room number:");
                isGuestOrRoom = true;
                break;
        }
        if (newValue != null && !newValue.trim().isEmpty()) {
            Booking.updateBookingInDB(guestName, roomNumber, column, newValue.trim(), isGuestOrRoom);
            loadBookings();
            JOptionPane.showMessageDialog(this, "Booking updated.");
        }
    }
}
