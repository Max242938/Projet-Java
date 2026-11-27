package be.umons.projetjava.observer;
import be.umons.projetjava.domain.DeviceEvent;
import java.util.*;
import org.springframework.stereotype.Service;
@Service
public class ObserverManager extends Subject {
    private final Map<String, EventObserver> availableObservers; // injecte automatiquement par Spring (cle = nom du Bean)
    private final List<String> observersEnabled = new ArrayList<>();
    public ObserverManager(Map<String, EventObserver> availableObservers) {
        this.availableObservers = availableObservers;
        for (String beanName : availableObservers.keySet()) enableObserver(beanName); // tous actifs au demarrage
    }
    private String resolveBeanName(String name) {
        if (name == null) return null;
        String trimmed = name.trim();
        if (availableObservers.containsKey(trimmed)) return trimmed;
        for (String key : availableObservers.keySet()) {
            if (key.equalsIgnoreCase(trimmed) || key.equalsIgnoreCase(trimmed + "Observer")) return key;
        }
        return null;
    }
    public void enableObserver(String name) {
        String beanName = resolveBeanName(name);
        if (beanName == null) throw new IllegalArgumentException("Unknown observer: " + name);
        addObserver(availableObservers.get(beanName));
        if (!observersEnabled.contains(beanName)) observersEnabled.add(beanName);
    }
    public void disableObserver(String name) {
        String beanName = resolveBeanName(name);
        if (beanName == null) throw new IllegalArgumentException("Unknown observer: " + name);
        removeObserver(availableObservers.get(beanName));
        observersEnabled.remove(beanName);
    }
    public boolean attach(String name) {
        if (resolveBeanName(name) == null) return false;
        enableObserver(name);
        return true;
    }
    public boolean detach(String name) {
        String beanName = resolveBeanName(name);
        if (beanName == null || !observersEnabled.contains(beanName)) return false;
        disableObserver(name);
        return true;
    }
    public boolean isAttached(String name) {
        String beanName = resolveBeanName(name);
        return beanName != null && observersEnabled.contains(beanName);
    }
    @Override public void notifyObservers(DeviceEvent event) {
        for (EventObserver observer : observers) observer.update(event);
    }
    public void notifyAll(DeviceEvent event) { notifyObservers(event); }
    public List<String> getAvailableObserverNames() { return new ArrayList<>(availableObservers.keySet()); }
    public List<String> getActiveObserverNames() { return new ArrayList<>(observersEnabled); }
}
