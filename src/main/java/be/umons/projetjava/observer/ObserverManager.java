package be.umons.projetjava.observer;
import be.umons.projetjava.domain.DeviceEvent;
import java.util.*;
import org.springframework.stereotype.Service;
@Service
public class ObserverManager {
    private final Map<String, EventObserver> availableObservers = new LinkedHashMap<>();
    private final List<EventObserver> activeObservers = new ArrayList<>();
    public ObserverManager(List<EventObserver> observers) {
        for (EventObserver observer : observers) {
            availableObservers.put(observer.getName().toLowerCase(), observer);
            activeObservers.add(observer);
        }
    }
    public boolean attach(String name) {
        EventObserver observer = (name == null) ? null : availableObservers.get(name.trim().toLowerCase());
        if (observer == null) return false;
        if (!activeObservers.contains(observer)) activeObservers.add(observer);
        return true;
    }
    public boolean detach(String name) {
        EventObserver observer = (name == null) ? null : availableObservers.get(name.trim().toLowerCase());
        return observer != null && activeObservers.remove(observer);
    }
    public boolean isAttached(String name) {
        EventObserver observer = (name == null) ? null : availableObservers.get(name.trim().toLowerCase());
        return observer != null && activeObservers.contains(observer);
    }
    public void notifyAllObservers(DeviceEvent event) {
        for (EventObserver observer : activeObservers) observer.onEvent(event);
    }
    public List<String> getAvailableObserverNames() { return new ArrayList<>(availableObservers.keySet()); }
    public List<String> getActiveObserverNames() {
        List<String> names = new ArrayList<>();
        for (EventObserver observer : activeObservers) names.add(observer.getName());
        return names;
    }
}
