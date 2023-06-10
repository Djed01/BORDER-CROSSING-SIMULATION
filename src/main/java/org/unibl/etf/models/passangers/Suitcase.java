package main.java.org.unibl.etf.models.passangers;

import main.java.org.unibl.etf.Simulation;

import java.io.Serializable;

public class Suitcase implements Serializable {
    private boolean notAllowedStuff;

    public Suitcase(){
        if(Simulation.generateBool(10)){
            this.notAllowedStuff = true;
        }else{
            this.notAllowedStuff = false;
        }
    }

    public boolean hasNotAllowedStuff(){
        return notAllowedStuff;
    }

    @Override
    public String toString() {
        return "Suitcase{" +
                "notAllowedStuff='" + notAllowedStuff + '\'' +
                '}';
    }
}
