package main.java.org.unibl.etf.models.vehichles;

import main.java.org.unibl.etf.Simulation;
import main.java.org.unibl.etf.models.passangers.Driver;
import main.java.org.unibl.etf.models.passangers.Passenger;
import main.java.org.unibl.etf.models.terminals.TruckCustomsTerminal;
import main.java.org.unibl.etf.models.terminals.TruckPoliceTerminal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Vehicle extends Thread{
    private boolean isFinished = false;
    private int x;
    private int y;
    private static int counter = 0;
    private int vehicleId = 0;
    private int numOfPassengers;
    private List<Passenger> passengers = new ArrayList<>();

    public final ReentrantLock LOCK = new ReentrantLock();

    public Vehicle(int maxNumOfPassengers) {
        Random random = new Random();
        counter++;
        this.vehicleId = counter;
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

    public int getVehicleId() {
        return vehicleId;
    }

    public void setXY(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public void run(){
        while(!isFinished) {
            synchronized (LOCK) {
                if (Simulation.MATRIX[y + 1][x] == null && this.y != Simulation.POLICE_TERMINAL_ROW - 2) {
                    Simulation.MATRIX[y][x] = null;
                    Simulation.MATRIX[y + 1][x] = this;
                    this.y++;
                    System.out.println("Vehicle " + this.vehicleId + " moved to [" + this.y + "] [" + this.x+"]");
                    LOCK.notifyAll();
                } else if (this.y == Simulation.POLICE_TERMINAL_ROW - 2) {
                    if (this instanceof ICargoVehicle) {
                        if(Simulation.MATRIX[y+2][Simulation.TRUCK_POLICE_TERMINAL_COLUMN] instanceof TruckPoliceTerminal
                            && Simulation.MATRIX[y+1][Simulation.TRUCK_POLICE_TERMINAL_COLUMN] == null){
                            Simulation.MATRIX[y][x] = null;
                            Simulation.MATRIX[y+1][Simulation.TRUCK_POLICE_TERMINAL_COLUMN] = this;
                            this.x = Simulation.TRUCK_POLICE_TERMINAL_COLUMN;
                            this.y++;
                            System.out.println("Vehicle " + this.vehicleId + " moved to [" + this.y + "] [" + this.x+"]");
                            // TODO:Update GUI
                            LOCK.notifyAll();
                        }
                    } else {
                        //TODO: Other vehicles movement
                    }
                } else if(this.y == Simulation.POLICE_TERMINAL_ROW-1){
                    // Finished checking on police terminal
                    if(this instanceof ICargoVehicle){
                        TruckPoliceTerminal truckPoliceTerminal = (TruckPoliceTerminal)(Simulation.MATRIX[y+1][Simulation.TRUCK_POLICE_TERMINAL_COLUMN]);
                        truckPoliceTerminal.checkPassengers(this);
                        System.out.println("Vehicle " + this.vehicleId + " finished checking at Police terminal");
                        if(Simulation.MATRIX[y+2][x] == null){
                            Simulation.MATRIX[y][x] = null;
                            Simulation.MATRIX[y+2][x] = this;
                            this.y = y+2;
                            System.out.println("Vehicle " + this.vehicleId + " moved to [" + this.y + "] [" + this.x+"]");
                            LOCK.notifyAll();
                        }else{
                            try{
                                LOCK.wait();
                            } catch (InterruptedException e) {
                                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
                            }
                        }
                    }else{
                        //TODO: Other vehicles movement
                    }
                }else if(this.y == Simulation.CUSTOMS_TERMINAL_ROW-1){
                    TruckCustomsTerminal truckCustomsTerminal = (TruckCustomsTerminal)(Simulation.MATRIX[Simulation.CUSTOMS_TERMINAL_ROW][x]);
                    truckCustomsTerminal.checkPassengers(this);
                    // Finished checking on customs terminal
                    Simulation.MATRIX[y][x] = null;
                    Simulation.MATRIX[y+2][x] = this;
                    this.y = y+2;
                    System.out.println("Vehicle " + this.vehicleId + " CROSSING BORDER");
                    LOCK.notifyAll();
                    isFinished = true;
                }
            }
        }
    }

}
