abstract class Drone {
    private String model;
    private int maxFlightRange;
    private String type;

    public Drone(String model, int maxFlightRange, String type) {
        this.model = model;
        this.maxFlightRange = maxFlightRange;
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public int getMaxFlightRange() {
        return maxFlightRange;
    }

    public String getType() {
        return type;
    }

    public abstract void displayInfo();
}
