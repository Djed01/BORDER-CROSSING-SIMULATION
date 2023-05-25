package main.java.org.unibl.etf.models.terminals;

import main.java.org.unibl.etf.models.passangers.Passenger;
import main.java.org.unibl.etf.models.vehichles.Bus;
import main.java.org.unibl.etf.models.vehichles.PersonalVehicle;
import main.java.org.unibl.etf.models.vehichles.Truck;
import main.java.org.unibl.etf.models.vehichles.Vehicle;

import java.util.ArrayList;

public class CustomsTerminal extends Terminal{
    public CustomsTerminal() {
        super();
    }

    public CustomsTerminal(boolean isInFunction) {
        super(isInFunction);
    }

    public void checkPassengers(Vehicle vehicle){
        ArrayList<Passenger> passengersToRemove = new ArrayList<>();
        int TIME_TO_CHECK_PASSENGER;
        if(vehicle instanceof Bus){
            TIME_TO_CHECK_PASSENGER = 100;
            for(Passenger passenger: vehicle.getPassengers()){
                System.out.println("Checking passenger: " + passenger);
                if(passenger.hasSuitcase()){
                    if(passenger.getSuitcase().hasNotAllowedStuff()){
                        System.out.println("Passenger has not allowed stuff, removing passenger: " + passenger);
                        passengersToRemove.add(passenger);
                        // TODO: Process passenger
                    }
                }
                try {
                    Thread.sleep(TIME_TO_CHECK_PASSENGER);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for(Passenger passenger: passengersToRemove) {
                vehicle.getPassengers().remove(passenger);
            }
        }else if(vehicle instanceof Truck){
            TIME_TO_CHECK_PASSENGER = 500;

            if(((Truck) vehicle).isNeedToGenerateDocumentation()){
                ((Truck) vehicle).generateDocumentation();
            }
            if(((Truck) vehicle).getActualWeight() > ((Truck) vehicle).getDeclaredWeight()){
                System.out.println("Truck has more weight than declared, removing truck: " + vehicle);
                // TODO: Remove truck but how?
            }
            try {
                Thread.sleep(TIME_TO_CHECK_PASSENGER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else if(vehicle instanceof PersonalVehicle){
            TIME_TO_CHECK_PASSENGER = 2000;
            try {
                Thread.sleep(TIME_TO_CHECK_PASSENGER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
