package main.java.org.unibl.etf.models.terminals;

import main.java.org.unibl.etf.Simulation;
import main.java.org.unibl.etf.models.passangers.Passenger;
import main.java.org.unibl.etf.models.vehichles.Bus;
import main.java.org.unibl.etf.models.vehichles.PersonalVehicle;
import main.java.org.unibl.etf.models.vehichles.Truck;
import main.java.org.unibl.etf.models.vehichles.Vehicle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static main.java.org.unibl.etf.Main.simulation;

public class CustomsTerminal extends Terminal {
    protected int TIME_TO_CHECK_PASSENGER;
    private String TERMINAL_NAME = "CUSTOMS TERMINAL ";

    public CustomsTerminal() {
        super();
    }

    public CustomsTerminal(String alias, boolean isInFunction) {
        super(isInFunction);
        this.alias = alias;
    }

    public CustomsTerminal(boolean isInFunction) {
        super(isInFunction);
    }

    public void checkPassengers(Vehicle vehicle) {
        StringBuilder description = new StringBuilder();
        ArrayList<Passenger> passengersToRemove = new ArrayList<>();
        if (vehicle instanceof Bus) {
            TIME_TO_CHECK_PASSENGER = 100;
            simulation.getAddMessage().accept(TERMINAL_NAME + alias + ": Checking passengers in vehicle: " + vehicle.getLabel() + " " + vehicle.getVehicleId());
            description.append(vehicle.getLabel()).append(" ").append(vehicle.getVehicleId()).append(":\n\n");
            for (Passenger passenger : vehicle.getPassengers()) {
                checkIfPause();
                if (passenger.hasSuitcase()) {
                    if (passenger.getSuitcase().hasNotAllowedStuff()) {
                        simulation.getAddMessage().accept(TERMINAL_NAME + alias + ": Passenger has not allowed stuff, removing passenger: " + passenger + "in vehicle: " + vehicle.getLabel() + " " + vehicle.getVehicleId());
                        description.append(TERMINAL_NAME).append(alias).append(": ").append(passenger).append(" had not allowed stuff. Removed from vehicle!\n");
                        passengersToRemove.add(passenger);
                    }
                }
                try {
                    Thread.sleep(TIME_TO_CHECK_PASSENGER);
                } catch (InterruptedException e) {
                    Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
                }
            }


            if (passengersToRemove.size() > 0) {
                for (Passenger passenger : passengersToRemove) {
                    vehicle.getPassengers().remove(passenger);
                    vehicle.decrementNumOfPassengers();
                }

                synchronized (SERIALIZATION_LOCK) {
                    writeRecords(vehicle, passengersToRemove);
                }
                simulation.addVehicleToRemove(vehicle, description.toString());
            }
        } else if (vehicle instanceof PersonalVehicle) {
            TIME_TO_CHECK_PASSENGER = 2000;
            simulation.getAddMessage().accept(TERMINAL_NAME + alias + ": Checking passengers in vehicle: " + vehicle.getLabel() + " " + vehicle.getVehicleId());
            try {
                Thread.sleep(TIME_TO_CHECK_PASSENGER);
            } catch (InterruptedException e) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
            }
        }
    }


    private synchronized void writeRecords(Vehicle vehicle, ArrayList<Passenger> passengersToRemove) {
        try {
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(new File(Simulation.CUSTOMS_RECORDS_FOLDER + simulation.getFileName() + ".txt"), true));
            printWriter.println("\n" + TERMINAL_NAME + alias + " RECORDS FOR VEHICLE " + vehicle.getLabel() + " " + vehicle.getVehicleId() + ":\n");
            for (Passenger passenger : passengersToRemove) {
                printWriter.println(passenger + " had not allowed stuff.");
            }
            printWriter.close();
        } catch (FileNotFoundException e) {
            Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
        }
    }


}
