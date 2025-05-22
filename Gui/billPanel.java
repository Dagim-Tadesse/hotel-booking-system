
package Gui;

import classes.*;
import rmi.*;

import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class billPanel extends JPanel {

    public billPanel(CardLayout layout, JPanel container) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Billing & Payments", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        JTextField roomField = new JTextField();
        JTextField amountField = new JTextField();

        String[] methods = { "cash", "telebirr", "cbe", "awash bank", "boa", "dashen bank" };
        JComboBox<String> paymentMethod = new JComboBox<>(methods);

        JButton generateBtn = new JButton("Generate Invoice");
        JButton payBtn = new JButton("Process Payment");
        JButton backBtn = new JButton("Back");

        JTextArea output = new JTextArea();
        output.setEditable(false);
        JScrollPane scroll = new JScrollPane(output);
        scroll.setPreferredSize(new Dimension(400, 120)); // Adjust width/height as needed

        formPanel.add(new JLabel("Room Number:"));
        formPanel.add(roomField);
        formPanel.add(new JLabel("Amount to Pay:"));
        formPanel.add(amountField);
        formPanel.add(new JLabel("Payment Method:"));
        formPanel.add(paymentMethod);
        formPanel.add(generateBtn);
        formPanel.add(payBtn);
        formPanel.add(backBtn);

        add(formPanel, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);

        // Action: Generate Invoice
        generateBtn.addActionListener(e -> {
            String roomNum = roomField.getText().trim();
            for (Booking b : Booking.readBookingDB()) {
                if (b.room.roomNumber.equals(roomNum)) {
                    if (!b.isPaid) {
                        output.setText("Invoice:\nRoom: " + b.room.roomNumber + " | Guest: " + b.guest.name +
                                "\nType: " + b.room.roomType + "\nTotal Price: $" + b.totalPrice);
                    } else {
                        output.setText("Room " + roomNum + " is already paid.");
                    }
                    return;
                }
            }
            output.setText("Booking not found for room " + roomNum);
        });

        // Action: Process Payment

        payBtn.addActionListener(e -> {
            String roomNum = roomField.getText().trim();
            String payMethod = paymentMethod.getSelectedItem().toString();
            int amount;

            try {
                amount = Integer.parseInt(amountField.getText().trim());
            } catch (NumberFormatException ex) {
                output.setText("Invalid amount entered.");
                return;
            }

            for (Booking b : Booking.readBookingDB()) {
                if (b.room.roomNumber.equals(roomNum) && !b.isPaid) {
                    try {
                        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                        Report report = (Report) registry.lookup("ReportService");

                        int newTotal = b.totalPrice - amount;
                        if (newTotal <= 0) {
                            b.isPaid = true;
                            newTotal = 0;
                        }

                        // Record the payment (amount paid, not remaining balance)
                        report.insertReportDB(b, payMethod, amount);

                        // Update booking price and paid status in DB
                        Booking.updatePriceDB(b.guest.name, roomNum, newTotal);

                        output.setText("Payment successful.\nRemaining balance: $" + newTotal);
                        // Optionally clear fields
                        // roomField.setText(""); amountField.setText("");
                        return;
                    } catch (Exception ex) {
                        output.setText("RMI error: " + ex.getMessage());
                        return;
                    }
                }
            }
            output.setText("Booking not found or already paid.");
        });

        // Action: Back
        backBtn.addActionListener(e -> layout.show(container, "MainMenu"));
    }
}
