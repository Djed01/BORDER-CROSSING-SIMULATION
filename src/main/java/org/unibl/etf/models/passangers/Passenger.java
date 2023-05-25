package main.java.org.unibl.etf.models.passangers;

import main.java.org.unibl.etf.Simulation;

public class Passenger {
    private String name;
    private String surname;
    private IdentificationDocument identificationDocument;
    private Suitcase suitcase;
    public Passenger(String name, String surname){
        this.name = name;
        this.surname = surname;
        this.identificationDocument = new IdentificationDocument(name, surname);
        if(Simulation.generateBool(70)){
            this.suitcase = new Suitcase();
        }else{
            this.suitcase = null;
        }
    }

    public Passenger(){}

    public Suitcase getSuitcase() {
        return suitcase;
    }

    public boolean hasSuitcase(){
        return suitcase != null;
    }

    public boolean hasValidIdentificationDocument(){
        return identificationDocument.isValid();
    }

    public String getName(){
        return name;
    }

    public String getSurname(){
        return surname;
    }


}
