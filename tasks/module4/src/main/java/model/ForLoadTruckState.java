package model;

public class ForLoadTruckState implements TruckState {

    private final TruckStateType type = TruckStateType.FOR_LOAD;

    @Override
    public void process(Truck truck) throws InterruptedException {
        if (truck.tryToLoad()) {
            TruckState state = new ProcessedTruckState();
            truck.setState(state);
        }
    }

    @Override
    public TruckStateType getType() {
        return type;
    }

}
