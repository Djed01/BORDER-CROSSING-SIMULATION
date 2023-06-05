package main.java.org.unibl.etf.gui;

import main.java.org.unibl.etf.Main;
import main.java.org.unibl.etf.Simulation;
import main.java.org.unibl.etf.models.vehichles.Vehicle;

import javax.swing.*;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class BorderCrossingFrame extends JFrame {
    private JPanel contentPane;
    private JLabel title;
    private JTextArea console;

    private JLabel[][] matrixLabel;

    private JButton showQueueButton;

    private QueueFrame queueFrame;


    public BorderCrossingFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1100, 800);
        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JPanel centralPanel = new JPanel(new GridLayout(10, 5));
        centralPanel.setBackground(Color.white);
        centralPanel.setBounds(233, 83, 633, 478);
        centralPanel.setVisible(true);
        matrixLabel = new JLabel[10][5];
        for (int i = 9; i >= 0; i--)
            for (int j = 0; j < 5; j++) {
                matrixLabel[i][j] = new JLabel("-");
                matrixLabel[i][j].setHorizontalAlignment(JLabel.CENTER);
                matrixLabel[i][j].setVerticalAlignment(JLabel.CENTER);
                matrixLabel[i][j].setVisible(true);
                centralPanel.add(matrixLabel[i][j]);
            }
        setTerminals();
        this.getContentPane().add(centralPanel);

        title = new JLabel("Border Crossing Simulation");
        title.setBounds(233, 0, 633, 72);
        this.getContentPane().add(title);
        title.setForeground(new Color(161, 2, 2));
        title.setFont(new Font("Serif", Font.BOLD, 45));
        title.setVisible(true);

        console = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setBounds(233, 600, 633, 150);
        console.setEditable(false);
        console.setVisible(true);
        getContentPane().add(scrollPane);

        queueFrame = new QueueFrame();
        queueFrame.setVisible(false);

        showQueueButton = new JButton("Show queue");
        showQueueButton.setBounds(10, 150, 213, 72);
        showQueueButton.addActionListener(e -> {
            EventQueue.invokeLater(() -> {
                try {
                    queueFrame.setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(BorderCrossingFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });
        getContentPane().add(showQueueButton);

        repaint();


        Main.simulation.setAddVehicle(addVehicleConsumer);
        Main.simulation.setRemoveVehicle(removeVehicleConsumer);
        Main.simulation.setAddMessage(addMessageConsumer);
    }

    //Prikaz automobila na GUI-u
    Consumer<Vehicle> addVehicleConsumer = (vehicle) -> SwingUtilities.invokeLater(() -> {
        synchronized (matrixLabel) {
            JLabel labelUp = matrixLabel[vehicle.getY() - 45][vehicle.getX()];
            labelUp.setText(vehicle.getLabel());
            labelUp.setOpaque(true);
            labelUp.setBackground(vehicle.getColor());
        }
    });

    //Uklanjanje figure sa GUI-a
    Consumer<Vehicle> removeVehicleConsumer = (vehicle) -> {
        synchronized (matrixLabel) {
            JLabel labelUp = matrixLabel[vehicle.getY() - 45][vehicle.getX()];
            labelUp.setText("-");
            labelUp.setOpaque(true);
            labelUp.setBackground(null);
        }
    };

    Consumer<String> addMessageConsumer = (message) -> SwingUtilities.invokeLater(() -> {
        console.append(message + "\n");
    });

    private void setTerminals() {
        matrixLabel[6][0].setText("P1");
        matrixLabel[6][0].setOpaque(true);
        matrixLabel[6][0].setBackground(Color.cyan);
        matrixLabel[6][0].setForeground(Color.black);

        matrixLabel[6][2].setText("P2");
        matrixLabel[6][2].setOpaque(true);
        matrixLabel[6][2].setBackground(Color.cyan);
        matrixLabel[6][2].setForeground(Color.black);

        matrixLabel[6][4].setText("PK");
        matrixLabel[6][4].setOpaque(true);
        matrixLabel[6][4].setBackground(Color.cyan);
        matrixLabel[6][4].setForeground(Color.black);

        matrixLabel[8][0].setText("C1");
        matrixLabel[8][0].setOpaque(true);
        matrixLabel[8][0].setBackground(Color.gray);

        matrixLabel[8][4].setText("CK");
        matrixLabel[8][4].setOpaque(true);
        matrixLabel[8][4].setBackground(Color.gray);
    }


}
