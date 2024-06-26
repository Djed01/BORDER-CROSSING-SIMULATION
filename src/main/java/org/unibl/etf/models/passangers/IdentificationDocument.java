package main.java.org.unibl.etf.models.passangers;

import main.java.org.unibl.etf.Simulation;

import java.io.Serializable;

public class IdentificationDocument implements Serializable {
    private String name;
    private String surname;
    private boolean isNotValid;

    public IdentificationDocument(String name, String surname) {
        this.name = name;
        this.surname = surname;
        this.isNotValid = Simulation.generateBool(3);
    }

    public boolean isNotValid() {
        return isNotValid;
    }
}
