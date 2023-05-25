package main.java.org.unibl.etf.models.vehichles;

import main.java.org.unibl.etf.models.passangers.Driver;
import main.java.org.unibl.etf.models.passangers.Passenger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Vehicle {
    private int numOfPassengers;
    private List<Passenger> passengers = new ArrayList<>();

    public Vehicle(int maxNumOfPassengers) {
        Random random = new Random();
        this.numOfPassengers = random.nextInt(maxNumOfPassengers) + 1;
        for(int i = 0; i < numOfPassengers; i++){
            String name = (char) (random.nextInt(26) + 'A') + "name";
            String surname = (char) (random.nextInt(26) + 'A') + "surname";
            if(i==0){
                passengers.add(new Driver(name, surname));
            }else{
                passengers.add(new Passenger(name, surname));
            }
        }
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }
}
