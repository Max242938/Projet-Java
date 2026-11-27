package be.umons.projetjava.observer;
import be.umons.projetjava.domain.DeviceEvent;
public interface EventObserver {
    String getName();
    void onEvent(DeviceEvent event);
}
