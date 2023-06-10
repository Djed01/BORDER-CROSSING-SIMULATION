package main.java.org.unibl.etf.models.passangers;

import main.java.org.unibl.etf.Simulation;

import java.io.Serializable;

public class Passenger implements Serializable {
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

    public boolean hasNotValidIdentificationDocument(){
        return identificationDocument.isNotValid();
    }

    public String getName(){
        return name;
    }

    public String getSurname(){
        return surname;
    }


    @Override
    public String toString() {
        return "Passenger{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\''+
                ", suitcase='" + suitcase + '\''+
                '}';
    }

}
