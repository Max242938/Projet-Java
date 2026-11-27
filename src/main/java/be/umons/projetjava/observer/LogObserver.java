package be.umons.projetjava.observer;
import be.umons.projetjava.domain.DeviceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component
public class LogObserver implements EventObserver {
    private static final Logger log = LoggerFactory.getLogger(LogObserver.class);
    @Override 
    public String getName() { return "log"; }
    @Override 
    public void onEvent(DeviceEvent event) {
        log.info("[JOURNAL] Evenement enregistre : id={}, deviceId={}, type={}, valeur={}, message={}",
                event.getId(), event.getDeviceId(), event.getEventType(), event.getValue(), event.getMessage());
    }
}
