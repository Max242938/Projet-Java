package be.umons.projetjava.domain;
import java.time.LocalDateTime;
public class DeviceMessage {
    private Long deviceId;
    private Double value;
    private String message;
    private LocalDateTime timestamp;
    public DeviceMessage() {}
    public DeviceMessage(Long deviceId, Double value, String message) { this(deviceId, value, message, LocalDateTime.now()); }
    public DeviceMessage(Long deviceId, Double value, String message, LocalDateTime timestamp) {
        this.deviceId = deviceId;
        this.value = value;
        this.message = message;
        this.timestamp = timestamp;
    }
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
