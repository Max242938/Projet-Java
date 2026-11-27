package be.umons.projetjava.domain;
import be.umons.projetjava.visitor.DeviceVisitor;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity
@Table(name = "devices")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Device {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name;
    @Enumerated(EnumType.STRING) private DeviceType deviceType;
    @Enumerated(EnumType.STRING) private DeviceStatus status;
    private LocalDate installationDate;
    private double parameter1; // sensibilite (camera) ou seuil max (thermometre)
    private double measurement1; // niveau mouvement ou temperature courante
    protected Device() {}
    public Device(String name, DeviceType deviceType, DeviceStatus status, LocalDate installationDate, double parameter1, double measurement1) {
        this.name = name;
        this.deviceType = deviceType;
        this.status = status;
        this.installationDate = installationDate;
        this.parameter1 = parameter1;
        this.measurement1 = measurement1;
    }
    public abstract void accept(DeviceVisitor visitor);
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public DeviceType getDeviceType() { return deviceType; }
    public void setDeviceType(DeviceType deviceType) { this.deviceType = deviceType; }
    public DeviceStatus getStatus() { return status; }
    public void setStatus(DeviceStatus status) { this.status = status; }
    public LocalDate getInstallationDate() { return installationDate; }
    public void setInstallationDate(LocalDate installationDate) { this.installationDate = installationDate; }
    public double getParameter1() { return parameter1; }
    public void setParameter1(double parameter1) { this.parameter1 = parameter1; }
    public double getMeasurement1() { return measurement1; }
    public void setMeasurement1(double measurement1) { this.measurement1 = measurement1; }
    @Override public String toString() {
        return "Device{id=" + id + ", name='" + name + "', type=" + deviceType + ", status=" + status + ", parameter1=" + parameter1 + ", measurement1=" + measurement1 + "}";
    }
}
