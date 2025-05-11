package Gui;

import classes.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class RoomManagementPanel extends JPanel {
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private CardLayout cardLayout;
    private JPanel parentPanel;

    public RoomManagementPanel(CardLayout cardLayout, JPanel parentPanel) {
        this.cardLayout = cardLayout;
        this.parentPanel = parentPanel;

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Room Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = { "Room Number", "Type", "Price", "Available", "Maintenance" };
        tableModel = new DefaultTableModel(columnNames, 0);
        roomTable = new JTable(tableModel);
        loadRoomData();

        JScrollPane scrollPane = new JScrollPane(roomTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Room");
        JButton updateButton = new JButton("Update Room");
        JButton deleteButton = new JButton("Delete Room");
        JButton backButton = new JButton("Back to Main Menu");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> handleAddRoom());
        updateButton.addActionListener(e -> handleUpdateRoom());
        deleteButton.addActionListener(e -> handleDeleteRoom());
        backButton.addActionListener(e -> cardLayout.show(parentPanel, "MainMenu"));
    }

    private void loadRoomData() {
        tableModel.setRowCount(0); // Clear table
        ArrayList<Room> rooms = Room.readRoomDB();
        for (Room room : rooms) {
            tableModel.addRow(new Object[] {
                    room.roomNumber,
                    room.roomType,
                    room.roomPrice,
                    room.isAvailable,
                    room.isUnderMaintenance
            });
        }
    }

    private void handleAddRoom() {
        JTextField roomNumberField = new JTextField();
        JTextField roomTypeField = new JTextField();

        Object[] message = {
                "Room Number:", roomNumberField,
                "Room Type (singlebed, doublebed, suite):", roomTypeField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Room", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String roomNumber = roomNumberField.getText().trim();
            String roomType = roomTypeField.getText().trim().toLowerCase();

            if (!Room.roomtypecheck(roomType)) {
                JOptionPane.showMessageDialog(this, "Invalid room type.");
                return;
            }

            ArrayList<Room> rooms = Room.readRoomDB();
            for (Room r : rooms) {
                if (r.roomNumber.equals(roomNumber)) {
                    JOptionPane.showMessageDialog(this, "Room already exists.");
                    return;
                }
            }

            new Room(roomNumber, roomType);
            loadRoomData();
        }
    }

    private void handleUpdateRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to update.");
            return;
        }

        String roomNumber = (String) tableModel.getValueAt(selectedRow, 0);
        String[] options = { "Room Type", "Availability", "Maintenance", "Price" };
        String selected = (String) JOptionPane.showInputDialog(this, "What do you want to update?", "Update Room",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selected == null)
            return;

        String column = "";
        String newValue = "";
        boolean isBoolean = false;
        boolean isInt = false;

        switch (selected) {
            case "Room Type":
                column = "roomType";
                newValue = JOptionPane.showInputDialog(this, "Enter new room type:");
                if (!Room.roomtypecheck(newValue)) {
                    JOptionPane.showMessageDialog(this, "Invalid room type.");
                    return;
                }
                break;
            case "Availability":
                column = "isAvailable";
                newValue = JOptionPane.showInputDialog(this, "Enter availability (true/false):");
                isBoolean = true;
                break;
            case "Maintenance":
                column = "isUnderMaintenance";
                newValue = JOptionPane.showInputDialog(this, "Enter maintenance status (true/false):");
                isBoolean = true;
                break;
            case "Price":
                column = "roomPrice";
                newValue = JOptionPane.showInputDialog(this, "Enter new price:");
                isInt = true;
                break;
        }

        if (newValue != null && !newValue.isEmpty()) {
            Room.updateRoomDB(roomNumber, column, newValue, isBoolean, isInt);
            loadRoomData();
        }
    }

    private void handleDeleteRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete.");
            return;
        }

        String roomNumber = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete room " + roomNumber + "?",
                "Delete Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Room.deleteRoomDB(roomNumber);
            loadRoomData();
        }
    }
}
