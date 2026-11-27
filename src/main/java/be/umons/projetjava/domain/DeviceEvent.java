package be.umons.projetjava.domain;
import be.umons.projetjava.visitor.DeviceVisitor;
import be.umons.projetjava.visitor.Element;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "device_events")
public class DeviceEvent implements Element {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Enumerated(EnumType.STRING) private EventType eventType;
    private LocalDateTime timestamp;
    private Long deviceId;
    @Column(name = "event_value") private Double value; // evite le mot reserve SQL "VALUE"
    private String message;
    public DeviceEvent() {}
    public DeviceEvent(EventType eventType, LocalDateTime timestamp, Long deviceId, Double value, String message) {
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.deviceId = deviceId;
        this.value = value;
        this.message = message;
    }
    @Override public void accept(DeviceVisitor v) { v.visit(this); }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    @Override public String toString() {
        return "DeviceEvent{id=" + id + ", eventType=" + eventType + ", timestamp=" + timestamp + ", deviceId=" + deviceId + ", value=" + value + ", message='" + message + "'}";
    }
}
