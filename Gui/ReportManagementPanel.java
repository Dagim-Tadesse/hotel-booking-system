
package Gui;

import classes.Report;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;

public class ReportManagementPanel extends JPanel {
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JButton viewHistoryButton, dailyReportButton, backButton, refreshButton;
    private JTextField dateField;
    private JLabel totalSalesLabel, averageSalesLabel;

    public ReportManagementPanel(CardLayout cardLayout, JPanel mainContainer) {
        setLayout(new BorderLayout());

        // Table setup for payment history
        String[] columns = { "Guest Info", "Room Info", "Check-in -- Check-out", "Total Price & Method",
                "Payment Date" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reportTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reportTable);

        // Controls panel
        JPanel controlsPanel = new JPanel();
        viewHistoryButton = new JButton("View Payment History");
        dailyReportButton = new JButton("Generate Daily Sales Report");
        backButton = new JButton("Back");
        refreshButton = new JButton("Refresh");
        dateField = new JTextField(10);
        dateField.setToolTipText("yyyy-MM-dd");
        controlsPanel.add(viewHistoryButton);
        controlsPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        controlsPanel.add(dateField);
        controlsPanel.add(dailyReportButton);
        controlsPanel.add(refreshButton);
        controlsPanel.add(backButton);

        // Sales report labels
        JPanel salesPanel = new JPanel();
        totalSalesLabel = new JLabel("Total Sales: ");
        averageSalesLabel = new JLabel("Average Sales: ");
        salesPanel.add(totalSalesLabel);
        salesPanel.add(averageSalesLabel);

        add(scrollPane, BorderLayout.CENTER);
        add(controlsPanel, BorderLayout.NORTH);
        add(salesPanel, BorderLayout.SOUTH);

        // Button actions
        viewHistoryButton.addActionListener(e -> loadPaymentHistory());
        dailyReportButton.addActionListener(e -> generateDailySalesReport());
        refreshButton.addActionListener(e -> loadPaymentHistory());
        backButton.addActionListener(e -> cardLayout.show(mainContainer, "MainMenu"));
    }

    private void loadPaymentHistory() {
        tableModel.setRowCount(0);
        totalSalesLabel.setText("Total Sales: ");
        averageSalesLabel.setText("Average Sales: ");
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Report report = (Report) registry.lookup("ReportService");
            ArrayList<String> reports = report.readReportDB();

            for (String r : reports) {
                // Split the string into lines for table columns
                String[] lines = r.split("\n");
                String guestInfo = lines.length > 0 ? lines[0] : "";
                String roomInfo = lines.length > 1 ? lines[1] : "";
                String checkInOut = lines.length > 2 ? lines[2] : "";
                String totalPriceMethod = lines.length > 3 ? lines[3] : "";
                String paymentDate = lines.length > 4 ? lines[4] : "";

                // Remove label if present, keep only the value after the first colon
                guestInfo = guestInfo.contains(":") ? guestInfo.substring(guestInfo.indexOf(":") + 1).trim()
                        : guestInfo;
                roomInfo = roomInfo.contains(":") ? roomInfo.substring(roomInfo.indexOf(":") + 1).trim() : roomInfo;
                checkInOut = checkInOut.contains(":") ? checkInOut.substring(checkInOut.indexOf(":") + 1).trim()
                        : checkInOut;
                totalPriceMethod = totalPriceMethod.contains(":")
                        ? totalPriceMethod.substring(totalPriceMethod.indexOf(":") + 1).trim()
                        : totalPriceMethod;
                paymentDate = paymentDate.contains(":") ? paymentDate.substring(paymentDate.indexOf(":") + 1).trim()
                        : paymentDate;

                tableModel.addRow(new Object[] { guestInfo, roomInfo, checkInOut, totalPriceMethod, paymentDate });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "RMI server not available, reports not loaded.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateDailySalesReport() {
        String dateStr = dateField.getText().trim();
        if (!isValidDate(dateStr)) {
            JOptionPane.showMessageDialog(this, "Enter a valid date in yyyy-MM-dd format.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Report report = (Report) registry.lookup("ReportService");
            int totalSales = report.dailyReportDB(dateStr);
            int count = report.countRow(dateStr);
            double averageSales = 0;
            if (count > 0) {
                averageSales = (double) totalSales / count;
            }
            totalSalesLabel.setText("Total Sales: " + totalSales + "$");
            averageSalesLabel.setText("Average Sales: " + String.format("%.2f", averageSales) + "$");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "RMI server not available, reports not loaded.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
