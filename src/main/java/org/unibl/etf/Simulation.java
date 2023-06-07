package main.java.org.unibl.etf;

import main.java.org.unibl.etf.gui.BorderCrossingFrame;
import main.java.org.unibl.etf.gui.QueueFrame;
import main.java.org.unibl.etf.models.passangers.Passenger;
import main.java.org.unibl.etf.models.terminals.CustomsTerminal;
import main.java.org.unibl.etf.models.terminals.PoliceTerminal;
import main.java.org.unibl.etf.models.terminals.TruckCustomsTerminal;
import main.java.org.unibl.etf.models.terminals.TruckPoliceTerminal;
import main.java.org.unibl.etf.models.vehichles.Bus;
import main.java.org.unibl.etf.models.vehichles.PersonalVehicle;
import main.java.org.unibl.etf.models.vehichles.Truck;
import main.java.org.unibl.etf.models.vehichles.Vehicle;

import java.io.*;
import java.sql.Time;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Simulation {
    private static final String LOGGER_PATH = "src/main/resources/logs/Simulation.log";
    private static final String CONFIG_PATH = "src/main/resources/config.properties";

    public static final String SERIALIZATION_FOLDER = "src/main/resources/PoliceTerminalRecords/";

    public static final String CUSTOMS_RECORDS_FOLDER = "src/main/resources/CustomsTerminalRecords/";

    public final ReentrantLock PAUSE_LOCK = new ReentrantLock();
    private volatile boolean pause = false;
    private static final int NUM_OF_BUSES = 5;
    private static final int NUM_OF_TRUCKS = 10;
    private static final int NUM_OF_PERSONAL_VEHICLES = 35;

    private Consumer<Vehicle> addVehicle;
    private Consumer<Vehicle> removeVehicle;

    private Consumer<Vehicle> addQueueVehicle;
    private Consumer<Vehicle> removeQueueVehicle;

    private Consumer<String> addMessage;
    public final ArrayList<Vehicle> vehicles;

    private HashMap<Vehicle,ArrayList<Passenger>> vehiclesToRemove;
    private HashMap<Vehicle,ArrayList<Passenger>> trucksToRemove;

    public static Object[][] MATRIX;
    public static final int POLICE_TERMINAL_ROW = 51;
    public static final int CUSTOMS_TERMINAL_ROW = 53;

    public static final int TRUCK_POLICE_TERMINAL_COLUMN = 4;
    public boolean isFinished = false;

    private final Random random = new Random();

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

    public Simulation() {
        vehiclesToRemove = new HashMap<>();
        trucksToRemove = new HashMap<>();
        MATRIX = new Object[55][5];
//        Properties properties = loadProperties();
//        boolean terminalOpen = Boolean.parseBoolean(properties.getProperty("policeTerminalIsInFunction1"));
        vehicles = generateVehicles();
 //       setVehicles(vehicles);
        setTerminals();

        // emptySerializationFolder();
    }

    public void startThreads() {
        for (Vehicle vehicle : vehicles) {
            vehicle.start();
        }
    }

    public void joinThreads(){
        for (Thread vehicleThread : vehicles) {
            try {
                vehicleThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isFinished = true;
    }

    public void removeVehicle(Vehicle vehicle){
       // getRemoveVehicle().accept(vehicle);
        MATRIX[vehicle.getY()][vehicle.getX()] = null;
    }

    private ArrayList<Vehicle> generateVehicles() {
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        for (int i = 0; i < NUM_OF_BUSES; i++) {
            vehicles.add(new Bus());
        }
        for (int i = 0; i < NUM_OF_TRUCKS; i++) {
            DecimalFormat df = new DecimalFormat("#.00");
            vehicles.add(new Truck(Double.parseDouble(df.format(random.nextDouble(9000) + 1000))));
        }
        for (int i = 0; i < NUM_OF_PERSONAL_VEHICLES; i++) {
            vehicles.add(new PersonalVehicle());
        }
        Collections.shuffle(vehicles);
        return vehicles;
    }

    public static boolean generateBool(int probability) {
        if (probability == 0) {
            return false;
        } else if (probability == 100) {
            return true;
        } else {
            int random = (int) (Math.random() * 100);
            if (random < probability) {
                return true;
            } else {
                return false;
            }
        }
    }

    private Properties loadProperties() {
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

    public void setVehicles(ArrayList<Vehicle> vehicles) {
        int i = 0;
        for (Vehicle vehicle : vehicles) {
            if (MATRIX[i][2] == null) {
                vehicle.setXY(2, i);
                MATRIX[i][2] = vehicle;
                if(i>=45) {
                    getAddVehicle().accept(vehicle);
                }else{
                    getAddQueueVehicle().accept(vehicle);
                }
                i++;
            }
        }
    }

    private void setTerminals() {
        MATRIX[POLICE_TERMINAL_ROW][0] = new PoliceTerminal("P1",true);
        MATRIX[POLICE_TERMINAL_ROW][2] = new PoliceTerminal("P2",true);
        MATRIX[POLICE_TERMINAL_ROW][4] = new TruckPoliceTerminal("PK",true);
        MATRIX[CUSTOMS_TERMINAL_ROW][0] = new CustomsTerminal("C1",true);
        MATRIX[CUSTOMS_TERMINAL_ROW][4] = new TruckCustomsTerminal("CK",true);
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

    public Consumer<Vehicle> getAddQueueVehicle() {
        return addQueueVehicle;
    }

    public void setAddQueueVehicle(Consumer<Vehicle> addQueueVehicle) {
        this.addQueueVehicle = addQueueVehicle;
    }

    public Consumer<Vehicle> getRemoveQueueVehicle() {
        return removeQueueVehicle;
    }

    public void setRemoveQueueVehicle(Consumer<Vehicle> removeQueueVehicle) {
        this.removeQueueVehicle = removeQueueVehicle;
    }

    public void setAddMessage(Consumer<String> addMessage) {
        this.addMessage = addMessage;
    }

    public synchronized Consumer<String> getAddMessage() {
        return addMessage;
    }


    public synchronized void addVehicleToRemove(Vehicle vehicle, ArrayList<Passenger> passengers) {
        vehiclesToRemove.put(vehicle, passengers);
    }

    public synchronized void addTruckToRemove(Vehicle vehicle, ArrayList<Passenger> passengers) {
        trucksToRemove.put(vehicle, passengers);
    }

    public HashMap<Vehicle,ArrayList<Passenger>> getVehiclesToRemove(){
        return vehiclesToRemove;
    }

    public void serializeVehicles(HashMap<Vehicle,ArrayList<Passenger>> vehiclesToRemove){
        try {
                // Serialize data object to a file
                // TODO : Add date to file name
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Simulation.SERIALIZATION_FOLDER + System.currentTimeMillis() +"_vehicles.ser"));
                out.writeObject(vehiclesToRemove);
                out.close();
            } catch (Exception e) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
            }
    }

    public String getVehicleDescription(int i,int j){
        if(MATRIX[i+45][j] instanceof Vehicle){
            return ((Vehicle) MATRIX[i+45][j]).toString();
        }
        return "";
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean state) {
        synchronized (PAUSE_LOCK) {
            if (!state)
                PAUSE_LOCK.notifyAll(); //Obavjestavanje svih niti
        }
        this.pause = state;
    }




}
