package main.java.org.unibl.etf.models.terminals;

import main.java.org.unibl.etf.Simulation;
import main.java.org.unibl.etf.models.vehichles.Truck;
import main.java.org.unibl.etf.models.vehichles.Vehicle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import static main.java.org.unibl.etf.Main.simulation;

public class TruckCustomsTerminal extends CustomsTerminal {
    public TruckCustomsTerminal(String alias, boolean isInFunction) {
        super(alias, isInFunction);
    }

    @Override
    public void checkPassengers(Vehicle vehicle) {
        if (vehicle instanceof Truck) {
            TIME_TO_CHECK_PASSENGER = 500;
            checkIfPause();
            if (((Truck) vehicle).isNeedToGenerateDocumentation()) {
                ((Truck) vehicle).generateDocumentation();
            }
            if (((Truck) vehicle).getActualWeight() > ((Truck) vehicle).getDeclaredWeight()) {
                checkIfPause();
                simulation.getAddMessage().accept("TRUCK CUSTOMS TERMINAL" + alias + ": Truck has more weight than declared, removing truck: : " + vehicle.getLabel() + " " + vehicle.getVehicleId());
                vehicle.suspendVehicle();
                try {
                    PrintWriter printWriter = new PrintWriter(new File(Simulation.CUSTOMS_RECORDS_FOLDER + "Truck" + vehicle.getVehicleId() + ".txt"));
                    printWriter.println(alias+": Truck has more weight than declared, removing truck: " + vehicle);
                    printWriter.close();
                } catch (FileNotFoundException e) {
                    Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
                }
            }
            try {
                Thread.sleep(TIME_TO_CHECK_PASSENGER);
            } catch (InterruptedException e) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
            }
        }
    }
}
