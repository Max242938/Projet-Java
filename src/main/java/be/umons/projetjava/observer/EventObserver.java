package be.umons.projetjava.observer;
import be.umons.projetjava.domain.DeviceEvent;
public interface EventObserver {
    void update(DeviceEvent event);
}
