package main.java.org.unibl.etf.gui;

import main.java.org.unibl.etf.models.vehichles.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import static main.java.org.unibl.etf.Main.simulation;

public class SuspendedVehiclesFrame extends JFrame {
    private   Thread mapReader;
    private JPanel contentPane;
    private JLabel[][] matrixLabel;

    private JTextArea vehicleDescription;
    public SuspendedVehiclesFrame() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 50, 900, 900);
        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        vehicleDescription = new JTextArea();
        JScrollPane scrollPane1 = new JScrollPane(vehicleDescription);
        scrollPane1.setBounds(70, 500, 760, 200);
        vehicleDescription.setLineWrap(true);
        vehicleDescription.setWrapStyleWord(true);
        vehicleDescription.setEditable(false);
        vehicleDescription.setVisible(true);
        getContentPane().add(scrollPane1);

        JPanel centralPanel = new JPanel(new GridLayout(5, 10));
        centralPanel.setBackground(Color.white);
        centralPanel.setBounds(133, 50, 633, 400);
        centralPanel.setVisible(true);
        matrixLabel = new JLabel[5][10];
        for (int i = 4; i >= 0; i--)
            for (int j = 0; j < 10; j++) {
                matrixLabel[i][j] = new JLabel("-");
                matrixLabel[i][j].setHorizontalAlignment(JLabel.CENTER);
                matrixLabel[i][j].setVerticalAlignment(JLabel.CENTER);
                matrixLabel[i][j].setVisible(true);
                centralPanel.add(matrixLabel[i][j]);
            }
        this.getContentPane().add(centralPanel);

        mapReader = mapReader();

    }

    public void startReading(){
        mapReader.start();
    }

    public void showVehicles(HashMap<Vehicle,String> vehicleMap){
        int i=0;
        int j=0;
        for(Vehicle vehicle:vehicleMap.keySet()){
            synchronized (matrixLabel) {
                matrixLabel[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        vehicleDescription.setText(vehicleMap.get(vehicle));
                    }
                });
                JLabel labelUp = matrixLabel[i][j];
                labelUp.setText(vehicle.getLabel());
                labelUp.setOpaque(true);
                labelUp.setBackground(vehicle.getColor());
                j++;
                if(j%10==0){
                    j=0;
                    i++;
                }
            }
        }
    }


    private Thread mapReader() {
        //Mjerenje vremena na nacin da nit postavimo u sleep mode jednu sekundu te nakon toga povecavamo brojace
        return new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    showVehicles(simulation.getSerializedVehicles());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        }
}
