package main.java.org.unibl.etf.models.terminals;

import main.java.org.unibl.etf.models.vehichles.Vehicle;

public abstract class Terminal {
        private boolean isInFunction;

        public Terminal(){
                this.isInFunction = true;
        }

        public Terminal(boolean isInFunction){
                this.isInFunction = isInFunction;
        }

        public boolean isInFunction(){
                return isInFunction;
        }

}
