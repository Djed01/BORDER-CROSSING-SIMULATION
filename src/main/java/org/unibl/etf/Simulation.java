package main.java.org.unibl.etf;

import main.java.org.unibl.etf.models.terminals.CustomsTerminal;
import main.java.org.unibl.etf.models.terminals.PoliceTerminal;
import main.java.org.unibl.etf.models.vehichles.Bus;
import main.java.org.unibl.etf.models.vehichles.PersonalVehicle;
import main.java.org.unibl.etf.models.vehichles.Truck;
import main.java.org.unibl.etf.models.vehichles.Vehicle;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Simulation {
    private static final String LOGGER_PATH = "src/main/resources/logs/Simulation.log";
    private static final String CONFIG_PATH = "src/main/resources/config.properties";
    private static final int NUM_OF_BUSES = 5;
    private static final int NUM_OF_TRUCKS = 10;
    private static final int NUM_OF_PERSONAL_VEHICLES = 35;

    private Random random = new Random();

    //Podesavanje loggera
    static {
        try {
            Handler fileHandler = new FileHandler(LOGGER_PATH, true);
            Logger.getLogger(Simulation.class.getName()).setUseParentHandlers(false);
            Logger.getLogger(Simulation.class.getName()).addHandler(fileHandler);
        } catch (IOException e) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, e.fillInStackTrace().toString());
        }
    }

    public Simulation(){
        Properties properties = loadProperties();
        boolean terminalOpen = Boolean.parseBoolean(properties.getProperty("policeTerminalIsInFunction1"));
//        System.out.println(terminalOpen);

        ArrayList<Vehicle> vehicles = generateVehicles();
        PoliceTerminal policeTerminal = new PoliceTerminal(terminalOpen);
        CustomsTerminal customsTerminal = new CustomsTerminal();
        for(Vehicle vehicle : vehicles){
             policeTerminal.checkPassengers(vehicle);
                customsTerminal.checkPassengers(vehicle);
        }
    }

    private ArrayList<Vehicle> generateVehicles(){
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        for(int i = 0;i < NUM_OF_BUSES;i++){
            vehicles.add(new Bus());
        }
        for(int i=0;i<NUM_OF_TRUCKS;i++){
            vehicles.add(new Truck(random.nextDouble(9000)+1000));
        }
        for(int i=0;i<NUM_OF_PERSONAL_VEHICLES;i++){
            vehicles.add(new PersonalVehicle());
        }
        Collections.shuffle(vehicles);
        return vehicles;
    }
    public static boolean generateBool(int probability) {
        if(probability == 0) {
            return false;
        }
        else if(probability == 100) {
            return true;
        }
        else {
            int random = (int) (Math.random() * 100);
            if(random < probability) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    private Properties loadProperties(){
        Properties properties = new Properties();
        FileInputStream fip;
        try {
            fip = new FileInputStream(CONFIG_PATH);
            properties.load(fip);
        } catch (IOException e) {
            Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
        }
        return properties;
    }


}
