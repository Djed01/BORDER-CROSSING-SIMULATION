package main.java.org.unibl.etf;

import main.java.org.unibl.etf.gui.BorderCrossingFrame;
import main.java.org.unibl.etf.models.terminals.CustomsTerminal;
import main.java.org.unibl.etf.models.terminals.PoliceTerminal;
import main.java.org.unibl.etf.models.terminals.TruckCustomsTerminal;
import main.java.org.unibl.etf.models.terminals.TruckPoliceTerminal;
import main.java.org.unibl.etf.models.vehichles.Bus;
import main.java.org.unibl.etf.models.vehichles.PersonalVehicle;
import main.java.org.unibl.etf.models.vehichles.Truck;
import main.java.org.unibl.etf.models.vehichles.Vehicle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Simulation {
    private static final String LOGGER_PATH = "src/main/resources/logs/Simulation.log";
    private static final String CONFIG_PATH = "src/main/resources/config.properties";

    public static final String SERIALIZATION_FOLDER = "src/main/resources/PoliceTerminalRecords/";

    public static  final String CUSTOMS_RECORDS_FOLDER = "src/main/resources/CustomsTerminalRecords/";


    private static final int NUM_OF_BUSES = 5;
    private static final int NUM_OF_TRUCKS = 10;
    private static final int NUM_OF_PERSONAL_VEHICLES = 35;

    private Consumer<Vehicle> addVehicle;
    private Consumer<Vehicle> removeVehicle;
    private final ArrayList<Vehicle> vehicles;

    public static Object[][] MATRIX;
    public static final int POLICE_TERMINAL_ROW = 51;
    public static final int CUSTOMS_TERMINAL_ROW = 53;

    public static final int TRUCK_POLICE_TERMINAL_COLUMN = 4;

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
        MATRIX = new Object[55][5];
//        Properties properties = loadProperties();
//        boolean terminalOpen = Boolean.parseBoolean(properties.getProperty("policeTerminalIsInFunction1"));
        vehicles = generateVehicles();
        setVehicles(vehicles);
        setTerminals();




       // emptySerializationFolder();
    }

    public void startThreads(){
        for(Vehicle vehicle:vehicles){
            vehicle.start();
        }

        for (Thread vehicleThread : vehicles) {
            try {
                vehicleThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<Vehicle> generateVehicles(){
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        for(int i = 0;i < NUM_OF_BUSES;i++){
            vehicles.add(new Bus());
        }
        for(int i=0;i<NUM_OF_TRUCKS;i++){
            DecimalFormat df = new DecimalFormat("#.00");
            vehicles.add(new Truck( Double.parseDouble(df.format(random.nextDouble(9000)+1000))));
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

    private void setVehicles(ArrayList<Vehicle> vehicles){
        int i=0;
        for(Vehicle vehicle:vehicles){
            if(MATRIX[i][2]==null){
                vehicle.setXY(2,i);
                MATRIX[i][2]=vehicle;
                i++;
            }
        }
    }

    private void setTerminals(){
        MATRIX[POLICE_TERMINAL_ROW][0] = new PoliceTerminal();
        MATRIX[POLICE_TERMINAL_ROW][2] = new PoliceTerminal();
        MATRIX[POLICE_TERMINAL_ROW][4] = new TruckPoliceTerminal();
        MATRIX[CUSTOMS_TERMINAL_ROW][0] = new CustomsTerminal();
        MATRIX[CUSTOMS_TERMINAL_ROW][4] = new TruckCustomsTerminal();
    }


    public static void deleteFiles(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFiles(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    private void emptySerializationFolder() {
        //Delete all files from serialization folder
        deleteFiles(new File(SERIALIZATION_FOLDER));
    }

    private void emptyCustomsRecordsFolder() {
        //Delete all files from serialization folder
        deleteFiles(new File(CUSTOMS_RECORDS_FOLDER));
    }


    public Consumer<Vehicle> getAddVehicle() {
        return addVehicle;
    }

    public void setAddVehicle(Consumer<Vehicle> addVehicle) {
        this.addVehicle = addVehicle;
    }

    public Consumer<Vehicle> getRemoveVehicle() {
        return removeVehicle;
    }

    public void setRemoveVehicle(Consumer<Vehicle> removeVehicle) {
        this.removeVehicle = removeVehicle;
    }
}
