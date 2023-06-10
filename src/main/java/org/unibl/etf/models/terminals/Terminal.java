package main.java.org.unibl.etf.models.terminals;

import main.java.org.unibl.etf.Simulation;
import main.java.org.unibl.etf.models.vehichles.Vehicle;

import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import static main.java.org.unibl.etf.Main.simulation;

public abstract class Terminal {

        protected static final ReentrantLock SERIALIZATION_LOCK = new ReentrantLock();
        private boolean isInFunction;
        protected String alias="";

        public Terminal(){
                this.isInFunction = true;
        }

        public Terminal(boolean isInFunction){
                this.isInFunction = isInFunction;
        }

        public boolean isInFunction(){
                return isInFunction;
        }

        public void setInFunction(boolean config){
                isInFunction = config;
        }

        protected void checkIfPause(){
                //Ukoliko se igra pauzira, pauziramo i kretanje figure
                synchronized (simulation.PAUSE_LOCK) {
                        try {
                                if (simulation.isPause())
                                        simulation.PAUSE_LOCK.wait(); //Cekamo dok se igra ne pokrene
                        } catch (InterruptedException e) {
                                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
                        }
                }
        }
}
