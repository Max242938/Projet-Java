package be.umons.projetjava.service;
import be.umons.projetjava.domain.*;
import be.umons.projetjava.observer.ObserverManager;
import be.umons.projetjava.repository.DeviceEventRepository;
import be.umons.projetjava.repository.DeviceRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class DeviceEventService {
    private final DeviceRepository deviceRepository;
    private final DeviceEventRepository eventRepository;
    private final ObserverManager observerManager;
    public DeviceEventService(DeviceRepository deviceRepository, DeviceEventRepository eventRepository, ObserverManager observerManager) {
        this.deviceRepository = deviceRepository;
        this.eventRepository = eventRepository;
        this.observerManager = observerManager;
    }
    @Transactional
    public DeviceEvent process(DeviceMessage msg) {
        if (msg == null || msg.getDeviceId() == null) throw new IllegalArgumentException("Le message et l'identifiant de l'appareil sont obligatoires");
        if (msg.getValue() == null) throw new IllegalArgumentException("La valeur mesuree est obligatoire");
        Device device = deviceRepository.findById(msg.getDeviceId()).orElseThrow(() -> new IllegalArgumentException("Appareil inconnu : " + msg.getDeviceId()));
        if (device.getStatus() != DeviceStatus.ACTIVE) throw new IllegalStateException("Appareil non actif (" + device.getStatus() + ") : " + device.getName());
        EventType eventType = null;
        String defaultMessage = null;
        if (device.getDeviceType() == DeviceType.CAMERA) {
            double sensitivity = device.getParameter1(), motion = msg.getValue();
            if (motion >= sensitivity * 1.5) {
                eventType = EventType.INTRUSION_DETECTED;
                defaultMessage = "Intrusion detectee (mouvement=" + motion + ", sensibilite=" + sensitivity + ")";
            } else if (motion >= sensitivity) {
                eventType = EventType.MOTION_DETECTED;
                defaultMessage = "Mouvement detecte (mouvement=" + motion + ", sensibilite=" + sensitivity + ")";
            }
        } else if (device.getDeviceType() == DeviceType.THERMOMETER) {
            double threshold = device.getParameter1(), previousTemp = device.getMeasurement1(), newTemp = msg.getValue();
            if (newTemp > threshold || newTemp < 5.0) {
                eventType = EventType.TEMPERATURE_ABNORMAL;
                defaultMessage = "Temperature anormale : " + newTemp + " C (seuil max=" + threshold + " C)";
            } else if (Math.abs(newTemp - previousTemp) >= 2.0) {
                eventType = EventType.TEMPERATURE_CHANGED;
                defaultMessage = "Variation de temperature importante : " + previousTemp + " C -> " + newTemp + " C";
            }
        }
        device.setMeasurement1(msg.getValue());
        deviceRepository.save(device);
        if (eventType == null) return null;
        LocalDateTime eventTime = (msg.getTimestamp() != null) ? msg.getTimestamp() : LocalDateTime.now();
        String eventMessage = (msg.getMessage() != null && !msg.getMessage().isBlank()) ? msg.getMessage() : defaultMessage;
        DeviceEvent savedEvent = eventRepository.save(new DeviceEvent(eventType, eventTime, device.getId(), msg.getValue(), eventMessage));
        observerManager.notifyAll(savedEvent);
        return savedEvent;
    }
    public List<DeviceEvent> findAllEvents() { return eventRepository.findAll(); }
    public List<DeviceEvent> findByDeviceId(Long deviceId) { return eventRepository.findByDeviceId(deviceId); }
    public List<DeviceEvent> findByEventType(EventType eventType) { return eventRepository.findByEventType(eventType); }
    public List<DeviceEvent> findByPeriod(LocalDateTime from, LocalDateTime to) { return eventRepository.findByTimestampBetween(from, to); }
}
