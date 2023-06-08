package main.java.org.unibl.etf.models.watcher;

import main.java.org.unibl.etf.Simulation;

import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static main.java.org.unibl.etf.Main.simulation;

public class PropertyChecker extends Thread {
    private String propertyFilePath;
    private volatile boolean stopRequested;

    public PropertyChecker(String propertyFilePath) {
        this.propertyFilePath = propertyFilePath;
        this.stopRequested = false;
    }

    public void stopWatching() {
        stopRequested = true;
    }

    @Override
    public void run() {
        while (!stopRequested) {
            Properties properties = simulation.loadProperties();
            boolean[] config = new boolean[5];
            config[0] = Boolean.parseBoolean(properties.getProperty("policeTerminalIsInFunction1"));
            config[1] = Boolean.parseBoolean(properties.getProperty("policeTerminalIsInFunction2"));
            config[2] = Boolean.parseBoolean(properties.getProperty("policeTerminalIsInFunction3"));
            config[3] = Boolean.parseBoolean(properties.getProperty("customsTerminalIsInFunction1"));
            config[4] = Boolean.parseBoolean(properties.getProperty("customsTerminalIsInFunction2"));
            simulation.setFunctionOfTerminals(config);

            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
            }
        }
    }
}
