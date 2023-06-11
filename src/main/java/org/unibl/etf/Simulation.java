package main.java.org.unibl.etf;


import main.java.org.unibl.etf.models.terminals.*;
import main.java.org.unibl.etf.models.vehichles.Bus;
import main.java.org.unibl.etf.models.vehichles.PersonalVehicle;
import main.java.org.unibl.etf.models.vehichles.Truck;
import main.java.org.unibl.etf.models.vehichles.Vehicle;

import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Simulation {
    private static final String LOGGER_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "logs" + File.separator + "Simulation.log";
    public static final String CONFIG_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config";

    public static final String SERIALIZATION_FOLDER = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "PoliceTerminalRecords" + File.separator;

    public static final String CUSTOMS_RECORDS_FOLDER = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "CustomsTerminalRecords" + File.separator;

    public final ReentrantLock PAUSE_LOCK = new ReentrantLock();

    private final ArrayList<Terminal> terminals = new ArrayList<>();
    private volatile boolean pause = false;
    private static final int NUM_OF_BUSES = 5;
    public static final int NUM_OF_TRUCKS = 10;
    private static final int NUM_OF_PERSONAL_VEHICLES = 35;

    private boolean isSerialized = false;

    private Consumer<Vehicle> addVehicle;
    private Consumer<Vehicle> removeVehicle;

    private Consumer<Vehicle> addQueueVehicle;
    private Consumer<Vehicle> removeQueueVehicle;

    private Consumer<String> addMessage;
    public final ArrayList<Vehicle> vehicles;
    public static Object[][] MATRIX;
    public static final int POLICE_TERMINAL_ROW = 51;
    public static final int CUSTOMS_TERMINAL_ROW = 53;
    private String fileName;

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
        MATRIX = new Object[55][5];
        vehicles = generateVehicles();
        setTerminals();
        createSerializationFiles();
        // emptySerializationFolder();
    }

    public void startThreads() {
        for (Vehicle vehicle : vehicles) {
            vehicle.start();
        }
    }

    public void joinThreads() {
        for (Thread vehicleThread : vehicles) {
            try {
                vehicleThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isFinished = true;
    }

    public void removeVehicle(Vehicle vehicle) {
        getRemoveVehicle().accept(vehicle);
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
            return random < probability;
        }
    }

    public Properties loadProperties() {
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
                if (i >= 45) {
                    getAddVehicle().accept(vehicle);
                } else {
                    getAddQueueVehicle().accept(vehicle);
                }
                i++;
            }
        }
    }

    private void setTerminals() {
        PoliceTerminal P1 = new PoliceTerminal("P1", true);
        terminals.add(P1);
        MATRIX[POLICE_TERMINAL_ROW][0] = P1;
        PoliceTerminal P2 = new PoliceTerminal("P2", true);
        terminals.add(P2);
        MATRIX[POLICE_TERMINAL_ROW][2] = P2;
        TruckPoliceTerminal PK = new TruckPoliceTerminal("PK", true);
        terminals.add(PK);
        MATRIX[POLICE_TERMINAL_ROW][4] = PK;
        CustomsTerminal C1 = new CustomsTerminal("C1", true);
        terminals.add(C1);
        MATRIX[CUSTOMS_TERMINAL_ROW][0] = C1;
        TruckCustomsTerminal CK = new TruckCustomsTerminal("CK", true);
        terminals.add(CK);
        MATRIX[CUSTOMS_TERMINAL_ROW][4] = CK;
    }

    public void setFunctionOfTerminals(boolean[] config) {
        int i = 0;
        for (Terminal terminal : terminals) {
            terminal.setInFunction(config[i]);
            i++;
        }
        synchronized (Vehicle.LOCK) {
            Vehicle.LOCK.notifyAll();
        }
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

    private void createSerializationFiles() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
        fileName = currentDateTime.format(formatter);

        try {
            // Create an empty file for serialization
            File serializationFile = new File(SERIALIZATION_FOLDER + fileName + ".ser");
            boolean serializationFileCreated = serializationFile.createNewFile();
            if (!serializationFileCreated) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, "Serialization file not created");
            }
            // Create an empty file for text input
            File textInputFile = new File(CUSTOMS_RECORDS_FOLDER + fileName + ".txt");
            boolean textInputFileCreated = textInputFile.createNewFile();
            if (!textInputFileCreated) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, "Text input file not created");
            }
        } catch (IOException e) {
            Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
        }
    }

    public String getFileName() {
        return fileName;
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


    public synchronized void addVehicleToRemove(Vehicle vehicle, String description) {
        HashMap<Vehicle, String> vehiclesToRemove = deserializeVehicles();
        if (vehiclesToRemove.containsKey(vehicle)) {
            //Append description to existing description
            String existingDescription = vehiclesToRemove.get(vehicle);
            vehiclesToRemove.put(vehicle, existingDescription + "\n" + description);
            serializeVehicles(vehiclesToRemove);
        } else {
            vehiclesToRemove.put(vehicle, description);
            serializeVehicles(vehiclesToRemove);
        }
    }

    public HashMap<Vehicle, String> getSerializedVehicles() {
        return deserializeVehicles();
    }

    public void serializeVehicles(HashMap<Vehicle, String> vehiclesToRemove) {
        try {
            // Load existing serialized data from file, if any
            HashMap<Vehicle, String> existingData = deserializeVehicles();

            // Merge the existing data with the new data
            existingData.putAll(vehiclesToRemove);

            // Serialize the updated data
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(SERIALIZATION_FOLDER + fileName + ".ser"));
            outputStream.writeObject(existingData);
            isSerialized = true;
        } catch (Exception e) {
            Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
        }
    }

    private synchronized HashMap<Vehicle, String> deserializeVehicles() {
        HashMap<Vehicle, String> deserializedMap = new HashMap<>();
        if (isSerialized) {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(SERIALIZATION_FOLDER + fileName + ".ser"));
                deserializedMap = (HashMap<Vehicle, String>) inputStream.readObject();  // TODO: YOU KNOW WHAT TO DO
            } catch (FileNotFoundException e) {
                // Ignore if the file doesn't exist yet
            } catch (IOException | ClassNotFoundException e) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
            }
        }
        return deserializedMap;
    }

    public String getVehicleDescription(int i, int j) {
        if (MATRIX[i + 45][j] instanceof Vehicle) {
            return MATRIX[i + 45][j].toString();
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
