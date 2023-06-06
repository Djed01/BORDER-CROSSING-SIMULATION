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
    private static final String TERMINAL_NAME = "POLICE TERMINAL ";
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
        int TIME_TO_CHECK_PASSENGER;
        ArrayList<Passenger> passengersToRemove = new ArrayList<>();
        if (vehicle instanceof Bus) {
            TIME_TO_CHECK_PASSENGER = 100;
        } else {
            TIME_TO_CHECK_PASSENGER = 500;
        }
       /* System.out.println("Checking passengers in vehicle: " + vehicle);*/

        simulation.getAddMessage().accept(TERMINAL_NAME+alias+": Checking passengers in vehicle: " + vehicle.getLabel() + " " + vehicle.getVehicleId());
        for (Passenger passenger : vehicle.getPassengers()) {
            System.out.println("Checking passenger: " + passenger);
            if (!passenger.hasValidIdentificationDocument()) {
                if (passenger instanceof Driver) {
                    simulation.getAddMessage().accept(TERMINAL_NAME+alias+": Driver doesn't have valid identification document, stopping vehicle: " + vehicle.getLabel() + " " + vehicle.getVehicleId());
                    // TODO: stop vehicle
                } else {
                    simulation.getAddMessage().accept(TERMINAL_NAME+alias+": Passenger doesn't have valid identification document, removing passenger: " + passenger + " from vehicle: " + vehicle.getLabel() + " " + vehicle.getVehicleId());
                   // System.out.println("Passenger doesn't have valid identification document, removing passenger: " + passenger);
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
            }
        }

        if (passengersToRemove.size() > 0) {
            simulation.addVehicleToRemove(vehicle, passengersToRemove);
        }

    }
}
