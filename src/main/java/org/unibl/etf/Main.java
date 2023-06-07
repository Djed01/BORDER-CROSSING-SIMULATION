package main.java.org.unibl.etf;

import main.java.org.unibl.etf.gui.BorderCrossingFrame;
import main.java.org.unibl.etf.gui.QueueFrame;
import main.java.org.unibl.etf.models.vehichles.Truck;

public class Main {
    public static Simulation simulation;
    public static void main(String[] args) {
        simulation = new Simulation();
        BorderCrossingFrame borderCrossingFrame = new BorderCrossingFrame();
        borderCrossingFrame.setVisible(true);
        simulation.setVehicles(simulation.vehicles);

        while(!simulation.isFinished){
            if(borderCrossingFrame.startStopBtnClicked==1){
                simulation.joinThreads();
            }
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
//        simulation.isFinished = true;
//        simulation.serializeVehicles(simulation.getVehiclesToRemove());
    }
}