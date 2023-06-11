package main.java.org.unibl.etf.models.terminals;

import main.java.org.unibl.etf.Simulation;
import main.java.org.unibl.etf.models.passangers.Passenger;
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

public class TruckCustomsTerminal extends CustomsTerminal {
    private String TERMINAL_NAME = "TRUCK CUSTOMS TERMINAL ";

    public TruckCustomsTerminal(String alias, boolean isInFunction) {
        super(alias, isInFunction);
    }

    @Override
    public void checkPassengers(Vehicle vehicle) {
        if (vehicle instanceof Truck) {
            StringBuilder description = new StringBuilder();
            TIME_TO_CHECK_PASSENGER = 500;
            checkIfPause();
            if (((Truck) vehicle).isNeedToGenerateDocumentation()) {
                ((Truck) vehicle).generateDocumentation();
                simulation.getAddMessage().accept(TERMINAL_NAME + " " + alias + " generated documentation for: " + vehicle.getLabel() + " " + vehicle.getVehicleId());
            }
            if (((Truck) vehicle).getActualWeight() > ((Truck) vehicle).getDeclaredWeight()) {
                checkIfPause();
                description.append(TERMINAL_NAME).append(alias).append(": \n").append(vehicle.getLabel()).append(" ").append(vehicle.getVehicleId()).append(" had more weight than declared and removed from border!\n");
                simulation.getAddMessage().accept(TERMINAL_NAME + alias + ": Truck has more weight than declared, removing truck: : " + vehicle.getLabel() + " " + vehicle.getVehicleId());
                vehicle.suspendVehicle();
                simulation.addVehicleToRemove(vehicle, description.toString());
                synchronized (SERIALIZATION_LOCK) {
                    writeRecords(vehicle);
                }
            }
            try {
                Thread.sleep(TIME_TO_CHECK_PASSENGER);
            } catch (InterruptedException e) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
            }
        }
    }

    private synchronized void writeRecords(Vehicle vehicle) {
        try {
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(new File(Simulation.CUSTOMS_RECORDS_FOLDER + simulation.getFileName() + ".txt"), true));
            printWriter.println("\n"+TERMINAL_NAME + alias + " RECORDS FOR VEHICLE" + vehicle.getLabel() + " " + vehicle.getVehicleId() + ":\n");
            printWriter.println(vehicle.getLabel() + " " + vehicle.getVehicleId() + " had more weight than declared!\n");
            printWriter.close();
        } catch (FileNotFoundException e) {
            Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
        }
    }
}
