package be.umons.projetjava.observer;
import be.umons.projetjava.domain.DeviceEvent;
import java.util.ArrayList;
import java.util.List;
public abstract class Subject {
    protected final List<EventObserver> observers = new ArrayList<>();
    abstract void notifyObservers(DeviceEvent event);
    public void addObserver(EventObserver observer) {
        if (!observers.contains(observer)) observers.add(observer);
    }
    public void removeObserver(EventObserver observer) { observers.remove(observer); }
}
