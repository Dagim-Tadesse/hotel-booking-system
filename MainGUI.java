
import Gui.BookingManagementPanel;
import Gui.GuestManagementPanel;
import Gui.LoginPanel;
import Gui.RoomManagementPanel;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContainer;

    public MainGUI() {
        setTitle("Hotel Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // CardLayout container
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Main menu panel
        JPanel mainPanel = createMainMenuPanel();

        // Login panel
        JPanel loginPanel = new LoginPanel(cardLayout, mainContainer);

        // Room management panel
        RoomManagementPanel roomPanel = new RoomManagementPanel(cardLayout, mainContainer);

        // Guest management panel
        GuestManagementPanel guestPanel = new GuestManagementPanel(cardLayout, mainContainer);

        // Only Booking Management Panel is functional
        BookingManagementPanel bookingPanel = new BookingManagementPanel(cardLayout, mainContainer);

        // Add panels to the card layout
        mainContainer.add(mainPanel, "MainMenu");
        mainContainer.add(loginPanel, "Login");
        mainContainer.add(roomPanel, "RoomManagement");
        mainContainer.add(guestPanel, "GuestManagement");
        mainContainer.add(bookingPanel, "BookingManagement");

        add(mainContainer);
        setVisible(true);

        cardLayout.show(mainContainer, "Login");
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel titleLabel = new JLabel("Main Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        // Notice
        JLabel noticeLabel = new JLabel("Note: More functionality is available in the CLI version.",
                SwingConstants.CENTER);
        noticeLabel.setForeground(Color.DARK_GRAY);
        noticeLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        // Buttons
        JButton roomButton = new JButton("Room Management");
        JButton guestButton = new JButton("Guest Management");
        JButton bookingButton = new JButton("Booking Management");
        JButton billingButton = new JButton("Billing and Payments");
        JButton reportButton = new JButton("Reports and Analytics");
        JButton exitButton = new JButton("Exit");

        // Add components
        panel.add(titleLabel);
        panel.add(noticeLabel);
        panel.add(roomButton);
        panel.add(guestButton);
        panel.add(bookingButton);
        panel.add(billingButton);
        panel.add(reportButton);
        panel.add(exitButton);

        // Action Listeners
        roomButton.addActionListener(e -> cardLayout.show(mainContainer, "RoomManagement"));
        guestButton.addActionListener(e -> cardLayout.show(mainContainer, "GuestManagement"));
        bookingButton.addActionListener(e -> cardLayout.show(mainContainer, "BookingManagement"));
        billingButton
                .addActionListener(e -> JOptionPane.showMessageDialog(this, "Billing and Payments - Coming Soon."));
        reportButton
                .addActionListener(e -> JOptionPane.showMessageDialog(this, "Reports and Analytics - Coming Soon."));
        exitButton.addActionListener(e -> System.exit(0));

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
    }
}
