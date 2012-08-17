package de.arnohaase.androidspielerei.person;


public enum Sex {
    m("male"), f("female");
    
    private final String label;
    
    private Sex(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
