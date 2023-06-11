package main.java.org.unibl.etf;

import main.java.org.unibl.etf.gui.BorderCrossingFrame;
import main.java.org.unibl.etf.gui.QueueFrame;
import main.java.org.unibl.etf.gui.SuspendedVehiclesFrame;
import main.java.org.unibl.etf.models.vehichles.Truck;
import main.java.org.unibl.etf.models.vehichles.Vehicle;
import main.java.org.unibl.etf.models.watcher.PropertyChecker;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static Simulation simulation;

    public static void main(String[] args) {
        simulation = new Simulation();
        BorderCrossingFrame borderCrossingFrame = new BorderCrossingFrame();
        borderCrossingFrame.setVisible(true);
        simulation.setVehicles(simulation.vehicles);
        PropertyChecker watcher = new PropertyChecker(Simulation.CONFIG_PATH);
        watcher.start();

        while (!simulation.isFinished) {
            if (borderCrossingFrame.startStopBtnClicked == 1) {
                simulation.joinThreads();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
            }
        }
        watcher.stopWatching();
        simulation.isFinished = true;
    }
}