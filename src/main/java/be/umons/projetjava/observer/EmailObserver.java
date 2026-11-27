package be.umons.projetjava.observer;
import be.umons.projetjava.domain.DeviceEvent;
import be.umons.projetjava.domain.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component
public class EmailObserver implements EventObserver {
    private static final Logger log = LoggerFactory.getLogger(EmailObserver.class);
    @Override 
    public String getName() { return "email"; }
    @Override 
    public void onEvent(DeviceEvent event) {
        if (event.getEventType() == EventType.INTRUSION_DETECTED || event.getEventType() == EventType.TEMPERATURE_ABNORMAL) {
            String text = "[EMAIL] Notification urgente envoyee : appareil=" + event.getDeviceId() + ", type=" + event.getEventType() + ", valeur=" + event.getValue() + ", message=" + event.getMessage();
            log.info(text);
            System.out.println(text);
        }
    }
}
