package main.java.org.unibl.etf.models.vehichles;

import main.java.org.unibl.etf.Simulation;
import main.java.org.unibl.etf.models.passangers.Driver;
import main.java.org.unibl.etf.models.passangers.Passenger;
import main.java.org.unibl.etf.models.terminals.CustomsTerminal;
import main.java.org.unibl.etf.models.terminals.PoliceTerminal;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import static main.java.org.unibl.etf.Main.simulation;

public abstract class Vehicle extends Thread implements Serializable {
    protected boolean isFinished = false;

    protected Color color;
    protected int x;
    protected int y;

    protected boolean processedAtPolice = false;
    protected boolean processedAtCustoms = false;
    private static int counter = 0;
    protected int vehicleId = 0;
    private int numOfPassengers;
    private List<Passenger> passengers = new ArrayList<>();

    public static final ReentrantLock LOCK = new ReentrantLock();

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

    public Color getColor() {
        return color;
    }

    public String getLabel() {
        return getClass().getSimpleName().substring(0, 1);
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setXY(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public void run(){
        while(!isFinished) {
            try {
                Thread.sleep(100);
            }catch (InterruptedException ex){
                Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (Simulation.MATRIX[y + 1][x] == null && this.y < Simulation.POLICE_TERMINAL_ROW - 2) {
                if(y>=45) {
                    // Update GUI
                    synchronized (LOCK) {
                        simulation.getRemoveVehicle().accept(this);
                        Simulation.MATRIX[y + 1][x] = this;
                        Simulation.MATRIX[y][x] = null;
                        this.y++;
                        simulation.getAddVehicle().accept(this);
                        System.out.println("Vehicle " + this.vehicleId + " moved to [" + this.y + "] [" + this.x + "]");
                        LOCK.notifyAll();
                    }
                }else if(y==44){
                    synchronized (LOCK) {
                        simulation.getRemoveQueueVehicle().accept(this);
                        Simulation.MATRIX[y + 1][x] = this;
                        Simulation.MATRIX[y][x] = null;
                        this.y++;
                        simulation.getAddVehicle().accept(this);
                        System.out.println("Vehicle " + this.vehicleId + " moved to [" + this.y + "] [" + this.x + "]");
                        LOCK.notifyAll();
                    }
                }else {
                    synchronized (LOCK) {
                        simulation.getRemoveQueueVehicle().accept(this);
                        Simulation.MATRIX[y + 1][x] = this;
                        Simulation.MATRIX[y][x] = null;
                        this.y++;
                        simulation.getAddQueueVehicle().accept(this);
                        System.out.println("Vehicle " + this.vehicleId + " moved to [" + this.y + "] [" + this.x + "]");
                        LOCK.notifyAll();
                    }
                }
            } else if (this.y == Simulation.POLICE_TERMINAL_ROW - 2) {
                if(Simulation.MATRIX[y+2][0] instanceof PoliceTerminal && Simulation.MATRIX[y+2][2] instanceof PoliceTerminal){
                    if(Simulation.MATRIX[y+1][0] == null){
                        synchronized (LOCK) {
                            simulation.getRemoveVehicle().accept(this);
                            Simulation.MATRIX[y + 1][0] = this;
                            Simulation.MATRIX[y][x] = null;
                            this.x = 0;
                            this.y++;
                            simulation.getAddVehicle().accept(this);
                            System.out.println("Vehicle " + this.vehicleId + " moved to [" + this.y + "] [" + this.x + "]");
                            LOCK.notifyAll();
                        }
                    }else if(Simulation.MATRIX[y+1][2] == null){
                        synchronized (LOCK) {
                            simulation.getRemoveVehicle().accept(this);
                            Simulation.MATRIX[y + 1][2] = this;
                            Simulation.MATRIX[y][x] = null;
                            this.x = 2;
                            this.y++;
                            simulation.getAddVehicle().accept(this);
                            System.out.println("Vehicle " + this.vehicleId + " moved to [" + this.y + "] [" + this.x + "]");
                            LOCK.notifyAll();
                        }
                    }
                    }


            } else if(this.y == Simulation.POLICE_TERMINAL_ROW-1){
                // Finished checking on police terminal
                if(!this.processedAtPolice){
                    PoliceTerminal policeTerminal = (PoliceTerminal)(Simulation.MATRIX[y+1][x]);
                    policeTerminal.checkPassengers(this);
                    System.out.println("Vehicle " + this.vehicleId + " finished checking at Police terminal");
                    this.processedAtPolice = true;
                }
                if(Simulation.MATRIX[y+2][0] != null){
                    synchronized (LOCK) {
                        try {
                            LOCK.wait();
                        } catch (InterruptedException e) {
                            Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
                        }
                    }
                }else {
                    synchronized (LOCK) {
                        simulation.getRemoveVehicle().accept(this);
                        Simulation.MATRIX[y + 2][0] = this;
                        Simulation.MATRIX[y][x] = null;
                        this.y = y + 2;
                        this.x = 0;
                        simulation.getAddVehicle().accept(this);
                        System.out.println("Vehicle " + this.vehicleId + " moved to [" + this.y + "] [" + this.x + "]");
                        LOCK.notifyAll();
                    }
                }
            }else if(this.y == Simulation.CUSTOMS_TERMINAL_ROW-1){

                CustomsTerminal customsTerminal = (CustomsTerminal) (Simulation.MATRIX[Simulation.CUSTOMS_TERMINAL_ROW][x]);
                customsTerminal.checkPassengers(this);
                System.out.println("Vehicle " + this.vehicleId + " finished checking at Customs terminal");

                // Finished checking on customs terminal
                synchronized (LOCK){
                simulation.getRemoveVehicle().accept(this);
                Simulation.MATRIX[y+2][x] = this;
                Simulation.MATRIX[y][x] = null;
                this.y = y+2;
                simulation.getAddVehicle().accept(this);
                System.out.println("Vehicle " + this.vehicleId + " CROSSING BORDER");
                isFinished = true;
                    LOCK.notifyAll();
                }
            }else {
                synchronized (LOCK){
                    try {
                        LOCK.wait();
                    }catch (InterruptedException e){
                        Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
                    }
                }
            }
        }
    }

}
