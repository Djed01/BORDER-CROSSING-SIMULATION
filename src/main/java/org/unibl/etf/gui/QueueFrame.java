package main.java.org.unibl.etf.gui;

import main.java.org.unibl.etf.Main;
import main.java.org.unibl.etf.models.vehichles.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class QueueFrame extends JFrame {
    private JPanel contentPane;
    private JLabel[][] matrixLabel;

    public QueueFrame() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 50, 900, 900);
        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JPanel centralPanel = new JPanel(new GridLayout(45, 5));
        centralPanel.setBackground(Color.white);
        centralPanel.setBounds(133, 50, 633, 800);
        centralPanel.setVisible(true);
        matrixLabel = new JLabel[45][5];
        for (int i = 44; i >= 0; i--)
            for (int j = 0; j < 5; j++) {
                matrixLabel[i][j] = new JLabel("-");
                matrixLabel[i][j].setHorizontalAlignment(JLabel.CENTER);
                matrixLabel[i][j].setVerticalAlignment(JLabel.CENTER);
                matrixLabel[i][j].setVisible(true);
                centralPanel.add(matrixLabel[i][j]);
            }
        this.getContentPane().add(centralPanel);

        Main.simulation.setAddQueueVehicle(addVehicleQueueConsumer);
        Main.simulation.setRemoveQueueVehicle(removeVehicleQueueConsumer);
    }

    //Prikaz vozila na GUI-u
    Consumer<Vehicle> addVehicleQueueConsumer = (vehicle) -> SwingUtilities.invokeLater(() -> {
        synchronized (matrixLabel) {
            JLabel labelUp = matrixLabel[vehicle.getY()][vehicle.getX()];
            labelUp.setText(vehicle.getLabel());
            labelUp.setOpaque(true);
            labelUp.setBackground(vehicle.getColor());
        }
    });

    //Uklanjanje vozila sa GUI-a
    Consumer<Vehicle> removeVehicleQueueConsumer = (vehicle) -> {
        synchronized (matrixLabel) {
            JLabel labelUp = matrixLabel[vehicle.getY()][vehicle.getX()];
            labelUp.setText("-");
            labelUp.setOpaque(true);
            labelUp.setBackground(null);
        }
    };
}