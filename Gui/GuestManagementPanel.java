package Gui;

import classes.Guest;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GuestManagementPanel extends JPanel {

    private JTextField nameField, contactField;
    private DefaultListModel<String> guestListModel;
    private JList<String> guestList;

    public GuestManagementPanel(CardLayout cardLayout, JPanel parentPanel) {
        setLayout(new BorderLayout());

        // Top: Form
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Contact Info:"));
        contactField = new JTextField();
        formPanel.add(contactField);

        JButton addButton = new JButton("Add Guest");
        formPanel.add(addButton);

        JButton updateButton = new JButton("Update Guest");
        formPanel.add(updateButton);

        add(formPanel, BorderLayout.NORTH);

        // Center: Guest list
        guestListModel = new DefaultListModel<>();
        guestList = new JList<>(guestListModel);
        refreshGuestList();

        JScrollPane scrollPane = new JScrollPane(guestList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Guests"));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom: Buttons
        JPanel bottomPanel = new JPanel();

        JButton deleteButton = new JButton("Delete Selected Guest");
        JButton backButton = new JButton("Back to Menu");

        bottomPanel.add(deleteButton);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim().toLowerCase();
            String contact = contactField.getText().trim();
            if (!name.isEmpty() && !contact.isEmpty()) {
                new Guest(name, contact, true);
                guestListModel.addElement(name + " - " + contact);
                nameField.setText("");
                contactField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Both fields are required.");
            }
        });

        updateButton.addActionListener(e -> {
            int selected = guestList.getSelectedIndex();
            if (selected >= 0) {
                String selectedValue = guestListModel.getElementAt(selected);
                String oldName = selectedValue.split(" - ")[0];

                String newName = nameField.getText().trim().toLowerCase();
                String newContact = contactField.getText().trim();

                if (!newName.isEmpty() && !newContact.isEmpty()) {
                    // Update DB and list
                    Guest.updateGuestDB(oldName, "guestName", newName);
                    Guest.updateGuestDB(newName, "phoneNumber", newContact);
                    refreshGuestList();
                    JOptionPane.showMessageDialog(this, "Guest updated.");
                } else {
                    JOptionPane.showMessageDialog(this, "Name and contact cannot be empty.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a guest to update.");
            }
        });

        deleteButton.addActionListener(e -> {
            int selected = guestList.getSelectedIndex();
            if (selected >= 0) {
                String selectedValue = guestListModel.getElementAt(selected);
                String name = selectedValue.split(" - ")[0];
                Guest.deleteGuestDB(name);
                guestListModel.remove(selected);
                JOptionPane.showMessageDialog(this, "Guest deleted.");
            } else {
                JOptionPane.showMessageDialog(this, "Please select a guest to delete.");
            }
        });

        guestList.addListSelectionListener(e -> {
            int index = guestList.getSelectedIndex();
            if (index >= 0) {
                String selectedValue = guestListModel.getElementAt(index);
                String[] parts = selectedValue.split(" - ");
                if (parts.length == 2) {
                    nameField.setText(parts[0]);
                    contactField.setText(parts[1]);
                }
            }
        });

        backButton.addActionListener(e -> cardLayout.show(parentPanel, "MainMenu"));
    }

    private void refreshGuestList() {
        guestListModel.clear();
        ArrayList<Guest> guests = Guest.readGuestDB();
        for (Guest g : guests) {
            guestListModel.addElement(g.name + " - " + g.contactInfo);
        }
    }
}
