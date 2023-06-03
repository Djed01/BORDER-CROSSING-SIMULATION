package main.java.org.unibl.etf.models.vehichles;

import main.java.org.unibl.etf.Simulation;
import main.java.org.unibl.etf.models.terminals.TruckCustomsTerminal;
import main.java.org.unibl.etf.models.terminals.TruckPoliceTerminal;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static main.java.org.unibl.etf.Main.simulation;

public class Truck extends Vehicle {
    public static final int PROBABILITY_OF_DOCUMENT_GENERATION = 50;
    public static final int PROBABILITY_OF_OVERLOADING = 20;

    private static final int MAX_NUM_OF_PASSENGERS = 3;
    private boolean needToGenerateDocumentation;

    private Random random = new Random();
    private double declaredWeight;
    private double actualWeight;

    public Truck(double declaredWeight) {
        super(MAX_NUM_OF_PASSENGERS);
        color = Color.GREEN;
        this.declaredWeight = declaredWeight;
        Random random = new Random();
        DecimalFormat df = new DecimalFormat("#.00"); // Setting the format of the double to 2 decimal places
        if (Simulation.generateBool(PROBABILITY_OF_DOCUMENT_GENERATION)) {
            this.needToGenerateDocumentation = true;
        } else {
            this.needToGenerateDocumentation = false;
        }

        if (Simulation.generateBool(PROBABILITY_OF_OVERLOADING)) {
            this.actualWeight = Double.parseDouble(df.format(declaredWeight + random.nextDouble(declaredWeight * 0.3)));
        } else {
            this.actualWeight = Double.parseDouble(df.format(random.nextDouble(declaredWeight)));
        }

    }

    public boolean isNeedToGenerateDocumentation() {
        return needToGenerateDocumentation;
    }

    public void generateDocumentation() {
        System.out.println("Documentation generated");
    }

    public double getDeclaredWeight() {
        return declaredWeight;
    }

    public double getActualWeight() {
        return actualWeight;
    }

    @Override
    public String toString() {
        return "Truck{" + "ID=" + getVehicleId() +
                " ,needToGenerateDocumentation=" + needToGenerateDocumentation +
                ", declaredWeight=" + declaredWeight +
                ", actualWeight=" + actualWeight +
                '}';
    }

    @Override
    public void run() {
        while (!isFinished) {
            try {
                Thread.sleep(100);
            }catch (InterruptedException e){
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
            }


            if (Simulation.MATRIX[y + 1][x] == null && this.y != Simulation.POLICE_TERMINAL_ROW - 2) {
                if(y>=45) {
                    // Update GUI
                    synchronized (LOCK) {
                        simulation.getRemoveVehicle().accept(this);
                        Simulation.MATRIX[y + 1][x] = this;
                        Simulation.MATRIX[y][x] = null;
                        this.y++;
                        simulation.getAddVehicle().accept(this);
                        System.out.println("Vehicle " + this.vehicleId + " moved to [" + this.y + "] [" + this.x + "]");
                        LOCK.notifyAll();
                    }
                }else if(y==44){
                    synchronized (LOCK) {
                        Simulation.MATRIX[y + 1][x] = this;
                        Simulation.MATRIX[y][x] = null;
                        this.y++;
                        simulation.getAddVehicle().accept(this);
                        System.out.println("Vehicle " + this.vehicleId + " moved to [" + this.y + "] [" + this.x + "]");
                        LOCK.notifyAll();
                    }
                }else {
                    synchronized (LOCK) {
                        Simulation.MATRIX[y + 1][x] = this;
                        Simulation.MATRIX[y][x] = null;
                        this.y++;
                        System.out.println("Vehicle " + this.vehicleId + " moved to [" + this.y + "] [" + this.x + "]");
                        LOCK.notifyAll();
                    }
                }
            } else if (this.y == Simulation.POLICE_TERMINAL_ROW - 2) {
                if (Simulation.MATRIX[y + 2][Simulation.TRUCK_POLICE_TERMINAL_COLUMN] instanceof TruckPoliceTerminal
                        && Simulation.MATRIX[y + 1][Simulation.TRUCK_POLICE_TERMINAL_COLUMN] == null) {
                    synchronized (LOCK) {
                        simulation.getRemoveVehicle().accept(this);
                        Simulation.MATRIX[y + 1][Simulation.TRUCK_POLICE_TERMINAL_COLUMN] = this;
                        Simulation.MATRIX[y][x] = null;
                        this.x = Simulation.TRUCK_POLICE_TERMINAL_COLUMN;
                        this.y++;
                        simulation.getAddVehicle().accept(this);
                        System.out.println("Vehicle " + this.vehicleId + " moved to [" + this.y + "] [" + this.x + "]");
                        LOCK.notifyAll();
                    }
                }
            } else if (this.y == Simulation.POLICE_TERMINAL_ROW - 1) {
                    // Finished checking on police terminal
                    if (!this.processedAtPolice) {
                        TruckPoliceTerminal truckPoliceTerminal = (TruckPoliceTerminal) (Simulation.MATRIX[y + 1][Simulation.TRUCK_POLICE_TERMINAL_COLUMN]);
                        truckPoliceTerminal.checkPassengers(this);
                        System.out.println("Vehicle " + this.vehicleId + " finished checking at Police terminal");
                        this.processedAtPolice = true;
                    }
                    if (Simulation.MATRIX[y + 2][x] != null) {
                        synchronized (LOCK) {
                            try {
                                LOCK.wait();
                            } catch (InterruptedException e) {
                                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
                            }
                        }
                    } else {

                        synchronized (LOCK) {
                            simulation.getRemoveVehicle().accept(this);
                            Simulation.MATRIX[y + 2][x] = this;
                            Simulation.MATRIX[y][x] = null;
                            this.y = y + 2;
                            simulation.getAddVehicle().accept(this);
                            System.out.println("Vehicle " + this.vehicleId + " moved to [" + this.y + "] [" + this.x + "]");
                            LOCK.notifyAll();
                        }
                    }
                } else if (this.y == Simulation.CUSTOMS_TERMINAL_ROW - 1) {

                    TruckCustomsTerminal truckCustomsTerminal = (TruckCustomsTerminal) (Simulation.MATRIX[Simulation.CUSTOMS_TERMINAL_ROW][x]);
                    truckCustomsTerminal.checkPassengers(this);
                    System.out.println("Vehicle " + this.vehicleId + " finished checking at Customs terminal");

                    synchronized (LOCK) {
                        // Finished checking on customs terminal
                        simulation.getRemoveVehicle().accept(this);
                        Simulation.MATRIX[y + 2][x] = this;
                        Simulation.MATRIX[y][x] = null;
                        this.y = y + 2;
                        simulation.getAddVehicle().accept(this);
                        System.out.println("Vehicle " + this.vehicleId + " CROSSING BORDER");
                        isFinished = true;
                        LOCK.notifyAll();
                    }
                } else {
                    synchronized (LOCK) {
                        try {
                            LOCK.wait();
                        } catch (InterruptedException e) {
                            Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
                        }
                    }
                }
            }
        }
    }

