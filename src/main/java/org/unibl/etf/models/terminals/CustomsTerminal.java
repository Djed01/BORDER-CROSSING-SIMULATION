package main.java.org.unibl.etf.models.terminals;

import main.java.org.unibl.etf.Simulation;
import main.java.org.unibl.etf.models.passangers.Passenger;
import main.java.org.unibl.etf.models.vehichles.Bus;
import main.java.org.unibl.etf.models.vehichles.PersonalVehicle;
import main.java.org.unibl.etf.models.vehichles.Truck;
import main.java.org.unibl.etf.models.vehichles.Vehicle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                    Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
                }
            }
            for(Passenger passenger: passengersToRemove) {
                vehicle.getPassengers().remove(passenger);
            }

            if(passengersToRemove.size() > 0) {
                try {
                    PrintWriter printWriter = new PrintWriter(new File(Simulation.CUSTOMS_RECORDS_FOLDER + "Vehicle" + vehicle.getVehicleId() + ".txt"));
                    for (Passenger passenger : passengersToRemove) {
                        printWriter.println(passenger + " had not allowed stuff.");
                    }
                    printWriter.close();
                } catch (FileNotFoundException e) {
                    Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
                }
            }
        }else if(vehicle instanceof Truck){
            TIME_TO_CHECK_PASSENGER = 500;

            if(((Truck) vehicle).isNeedToGenerateDocumentation()){
                ((Truck) vehicle).generateDocumentation();
            }
            if(((Truck) vehicle).getActualWeight() > ((Truck) vehicle).getDeclaredWeight()){
                try {
                    PrintWriter printWriter = new PrintWriter(new File(Simulation.CUSTOMS_RECORDS_FOLDER+"Truck"+vehicle.getVehicleId()+".txt"));
                    printWriter.println("Truck has more weight than declared, removing truck: " + vehicle);
                    printWriter.close();
                } catch (FileNotFoundException e) {
                    Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
                }
                // TODO: Remove truck but how?
            }
            try {
                Thread.sleep(TIME_TO_CHECK_PASSENGER);
            } catch (InterruptedException e) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
            }
        }else if(vehicle instanceof PersonalVehicle){
            TIME_TO_CHECK_PASSENGER = 2000;
            try {
                Thread.sleep(TIME_TO_CHECK_PASSENGER);
            } catch (InterruptedException e) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
            }
        }
    }


}
