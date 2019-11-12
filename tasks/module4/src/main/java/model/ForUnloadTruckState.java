package model;

public class ForUnloadTruckState implements TruckState {

    private final TruckStateType type = TruckStateType.FOR_UNLOAD;

    @Override
    public void process(Truck truck) throws InterruptedException {
        if (truck.tryToUnload()) {
            TruckState state = new ProcessedTruckState();
            truck.setState(state);
        }
    }

    @Override
    public TruckStateType getType() {
        return type;
    }

}
