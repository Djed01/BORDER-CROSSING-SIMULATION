package main.java.org.unibl.etf.models.terminals;

import main.java.org.unibl.etf.models.vehichles.Vehicle;

public class TruckPoliceTerminal extends PoliceTerminal{
    public TruckPoliceTerminal(String alias, boolean isInFunction){
        super(alias, isInFunction);
    }

    @Override
    public void checkPassengers(Vehicle vehicle) {
        super.checkPassengers(vehicle);
    }
}
