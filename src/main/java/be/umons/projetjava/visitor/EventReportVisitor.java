package be.umons.projetjava.visitor;
import be.umons.projetjava.domain.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.springframework.stereotype.Component;
@Component
public class EventReportVisitor implements DeviceVisitor {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private Map<Long, String> deviceNames = new LinkedHashMap<>();
    private int totalEvents = 0, tempCount = 0, motionCount = 0, intrusionCount = 0;
    private LocalDateTime firstEventDate = null, lastEventDate = null;
    private Map<EventType, Integer> countByType = new LinkedHashMap<>();
    private Map<Long, Integer> countByDevice = new LinkedHashMap<>();
    private double tempSum = 0.0, tempMin = 0.0, tempMax = 0.0, maxMotionValue = 0.0;
    public EventReportVisitor() { reset(); }
    @Override public String getName() { return "report"; }
    @Override public String getTitle() { return "Rapport statistique des evenements"; }
    @Override public void reset() {
        deviceNames = new LinkedHashMap<>();
        totalEvents = 0; tempCount = 0; motionCount = 0; intrusionCount = 0;
        firstEventDate = null; lastEventDate = null;
        countByType = new LinkedHashMap<>();
        for (EventType type : EventType.values()) countByType.put(type, 0);
        countByDevice = new LinkedHashMap<>();
        tempSum = 0.0; tempMin = 0.0; tempMax = 0.0; maxMotionValue = 0.0;
    }
    @Override public void visitCamera(CameraDevice camera) {
        if (camera.getId() != null) deviceNames.put(camera.getId(), camera.getName());
    }
    @Override public void visitThermometer(ThermometerDevice thermometer) {
        if (thermometer.getId() != null) deviceNames.put(thermometer.getId(), thermometer.getName());
    }
    @Override public void visitEvent(DeviceEvent event) {
        totalEvents++;
        LocalDateTime ts = event.getTimestamp();
        if (ts != null) {
            if (firstEventDate == null || ts.isBefore(firstEventDate)) firstEventDate = ts;
            if (lastEventDate == null || ts.isAfter(lastEventDate)) lastEventDate = ts;
        }
        EventType type = event.getEventType();
        if (type != null) countByType.put(type, countByType.getOrDefault(type, 0) + 1);
        Long deviceId = event.getDeviceId();
        if (deviceId != null) countByDevice.put(deviceId, countByDevice.getOrDefault(deviceId, 0) + 1);
        Double val = event.getValue();
        if ((type == EventType.TEMPERATURE_CHANGED || type == EventType.TEMPERATURE_ABNORMAL) && val != null) {
            if (tempCount == 0) { tempMin = val; tempMax = val; }
            else { if (val < tempMin) tempMin = val; if (val > tempMax) tempMax = val; }
            tempSum += val;
            tempCount++;
        } else if (type == EventType.MOTION_DETECTED || type == EventType.INTRUSION_DETECTED) {
            if (type == EventType.MOTION_DETECTED) motionCount++;
            else intrusionCount++;
            if (val != null && val > maxMotionValue) maxMotionValue = val;
        }
    }
    @Override public String getReportText() {
        StringBuilder sb = new StringBuilder();
        if (firstEventDate != null && lastEventDate != null) {
            sb.append("Periode couverte : du ").append(firstEventDate.format(FORMATTER)).append(" au ").append(lastEventDate.format(FORMATTER)).append("\n");
        } else {
            sb.append("Periode couverte : aucun evenement enregistre\n");
        }
        sb.append("Nombre total d'evenements : ").append(totalEvents).append("\n\nRepartition par type d'evenement :\n");
        for (Map.Entry<EventType, Integer> entry : countByType.entrySet()) {
            sb.append("  - ").append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
        }
        sb.append("\nRepartition par appareil :\n");
        if (countByDevice.isEmpty()) sb.append("  (Aucun evenement)\n");
        else for (Map.Entry<Long, Integer> entry : countByDevice.entrySet()) {
            String name = deviceNames.getOrDefault(entry.getKey(), "Appareil #" + entry.getKey());
            sb.append("  - ").append(name).append(" (ID=").append(entry.getKey()).append(") : ").append(entry.getValue()).append(" evenement(s)\n");
        }
        sb.append("\nStatistiques de temperature :\n");
        if (tempCount == 0) sb.append("  Aucune mesure de temperature enregistree\n");
        else {
            sb.append("  - Nombre de releves : ").append(tempCount).append("\n")
                    .append("  - Minimale : ").append(String.format("%.2f C", tempMin)).append("\n")
                    .append("  - Maximale : ").append(String.format("%.2f C", tempMax)).append("\n")
                    .append("  - Moyenne  : ").append(String.format("%.2f C", tempSum / tempCount)).append("\n");
        }
        sb.append("\nStatistiques de detection camera :\n")
                .append("  - Detections de mouvement : ").append(motionCount).append("\n")
                .append("  - Alertes d'intrusion     : ").append(intrusionCount).append("\n")
                .append("  - Niveau max observe      : ").append(String.format("%.2f", maxMotionValue)).append("\n");
        return sb.toString();
    }
}
