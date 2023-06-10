package main.java.org.unibl.etf.models.terminals;

import main.java.org.unibl.etf.Simulation;
import main.java.org.unibl.etf.models.passangers.Driver;
import main.java.org.unibl.etf.models.passangers.Passenger;
import main.java.org.unibl.etf.models.vehichles.Bus;
import main.java.org.unibl.etf.models.vehichles.Vehicle;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static main.java.org.unibl.etf.Main.simulation;

public class PoliceTerminal extends Terminal{
    private static String TERMINAL_NAME = "POLICE TERMINAL ";
    public PoliceTerminal() {
        super();
    }

    public PoliceTerminal(String alias, boolean isInFunction) {
        super(isInFunction);
        this.alias = alias;
    }

    public PoliceTerminal(boolean isInFunction) {
        super(isInFunction);
    }

    public void checkPassengers(Vehicle vehicle) {
        StringBuilder description = new StringBuilder();
        int TIME_TO_CHECK_PASSENGER;
        ArrayList<Passenger> passengersToRemove = new ArrayList<>();
        if (vehicle instanceof Bus) {
            TIME_TO_CHECK_PASSENGER = 100;
        } else {
            TIME_TO_CHECK_PASSENGER = 500;
        }

        simulation.getAddMessage().accept(TERMINAL_NAME+alias+": Checking passengers in vehicle: " + vehicle.getLabel() + " " + vehicle.getVehicleId());
        description.append(vehicle.getLabel()).append(" ").append(vehicle.getVehicleId()).append(":\n\n");
        for (Passenger passenger : vehicle.getPassengers()) {
            checkIfPause();

            if (passenger.hasNotValidIdentificationDocument()) {
                if (passenger instanceof Driver) {
                    description.append(TERMINAL_NAME).append(alias).append(": Driver didn't had valid identification document, vehicle suspended from broder!\n\n");
                    simulation.getAddMessage().accept(TERMINAL_NAME+alias+": Driver doesn't have valid identification document, stopping vehicle: " + vehicle.getLabel() + " " + vehicle.getVehicleId());
                    passengersToRemove.add(passenger);
                    vehicle.suspendVehicle();
                    // TODO: stop vehicle
                } else {
                    description.append(TERMINAL_NAME).append(alias).append(": ").append(passenger.toString()).append(" didn't had valid identification document and removed from vehicle!\n\n");
                    simulation.getAddMessage().accept(TERMINAL_NAME+alias+": Passenger doesn't have valid identification document, removing passenger: " + passenger + " from vehicle: " + vehicle.getLabel() + " " + vehicle.getVehicleId());
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
        }

        if (passengersToRemove.size() > 0) {
            simulation.addVehicleToRemove(vehicle, description.toString());
        }

    }
}
