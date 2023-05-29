package main.java.org.unibl.etf.models.vehichles;

import main.java.org.unibl.etf.Simulation;

import java.text.DecimalFormat;
import java.util.Random;

public class Truck extends Vehicle implements ICargoVehicle {
    public static final int PROBABILITY_OF_DOCUMENT_GENERATION = 50;
    public static final int PROBABILITY_OF_OVERLOADING = 20;

    private static final int MAX_NUM_OF_PASSENGERS = 3;
    private boolean needToGenerateDocumentation;

    private Random random = new Random();
    private double declaredWeight;
    private double actualWeight;

    public Truck(double declaredWeight) {
        super(MAX_NUM_OF_PASSENGERS);
        this.declaredWeight = declaredWeight;
        Random random = new Random();
        DecimalFormat df = new DecimalFormat("#.00"); // Setting the format of the double to 2 decimal places
        if(Simulation.generateBool(PROBABILITY_OF_DOCUMENT_GENERATION)) {
            this.needToGenerateDocumentation = true;
        }
        else {
            this.needToGenerateDocumentation = false;
        }

        if(Simulation.generateBool(PROBABILITY_OF_OVERLOADING)) {
            this.actualWeight =  Double.parseDouble(df.format(declaredWeight +random.nextDouble(declaredWeight*0.3)));
        }
        else {
            this.actualWeight = Double.parseDouble(df.format(random.nextDouble(declaredWeight)));
        }

    }

    public boolean isNeedToGenerateDocumentation() {
        return needToGenerateDocumentation;
    }

    public void generateDocumentation(){
        System.out.println("Documentation generated");
    }

    public double getDeclaredWeight() {
        return declaredWeight;
    }

    public double getActualWeight() {
        return actualWeight;
    }

    @Override
    public String toString() {
        return "Truck{" + "ID="+getVehicleId()+
                " ,needToGenerateDocumentation=" + needToGenerateDocumentation +
                ", declaredWeight=" + declaredWeight +
                ", actualWeight=" + actualWeight +
                '}';
    }
}
