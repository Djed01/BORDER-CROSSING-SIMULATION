package main.java.org.unibl.etf.models.vehichles;

import java.awt.*;

public class PersonalVehicle extends Vehicle {
    private static final int MAX_NUM_OF_PASSENGERS = 5;

    public PersonalVehicle() {
        super(MAX_NUM_OF_PASSENGERS);
        color = Color.red;
    }

}
