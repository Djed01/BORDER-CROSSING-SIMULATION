package main.java.org.unibl.etf.models.terminals;

import main.java.org.unibl.etf.models.passangers.Driver;
import main.java.org.unibl.etf.models.passangers.Passenger;
import main.java.org.unibl.etf.models.vehichles.Bus;
import main.java.org.unibl.etf.models.vehichles.Vehicle;

import java.util.ArrayList;

public class PoliceTerminal extends Terminal{
    public PoliceTerminal() {
        super();
    }

    public PoliceTerminal(boolean isInFunction) {
        super(isInFunction);
    }

    public void checkPassengers(Vehicle vehicle){
        int TIME_TO_CHECK_PASSENGER;
        ArrayList<Passenger> passengersToRemove = new ArrayList<>();
        if(vehicle instanceof Bus){
            TIME_TO_CHECK_PASSENGER = 100;
        }else{
            TIME_TO_CHECK_PASSENGER = 500;
        }
        System.out.println("Checking passengers in vehicle: " + vehicle);
        for(Passenger passenger: vehicle.getPassengers()){
            System.out.println("Checking passenger: " + passenger);
            if(!passenger.hasValidIdentificationDocument()){
                if(passenger instanceof Driver){
                    System.out.println("Driver doesn't have valid identification document, stopping vehicle: " + vehicle);
                    // TODO: stop vehicle
                }else{
                    System.out.println("Passenger doesn't have valid identification document, removing passenger: " + passenger);
                    passengersToRemove.add(passenger);
                    // Binary serialize passenger
                }
            }
            try {
                Thread.sleep(TIME_TO_CHECK_PASSENGER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(Passenger passenger: passengersToRemove){
            vehicle.getPassengers().remove(passenger);
            //TODO: BINARY SERIALIZATION
        }
    }
}
