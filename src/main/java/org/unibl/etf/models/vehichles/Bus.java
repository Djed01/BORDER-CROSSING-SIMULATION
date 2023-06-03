package main.java.org.unibl.etf.models.vehichles;

import main.java.org.unibl.etf.models.passangers.Passenger;
import main.java.org.unibl.etf.models.passangers.Suitcase;

import java.awt.*;
import java.util.HashMap;

public class Bus extends Vehicle{
    private static final int MAX_NUM_OF_PASSENGERS = 52;


    private HashMap<Passenger, Suitcase> CargoSpace = new HashMap<>();
    public Bus(){
        super(MAX_NUM_OF_PASSENGERS);
        color = Color.YELLOW;
        for(Passenger passenger : super.getPassengers()){
            if(passenger.getSuitcase() != null){
                CargoSpace.put(passenger, passenger.getSuitcase());
            }
        }
    }

    @Override
    public String toString() {
        return "Bus{" +
                "CargoSpace=" + CargoSpace +
                '}';
    }

}
