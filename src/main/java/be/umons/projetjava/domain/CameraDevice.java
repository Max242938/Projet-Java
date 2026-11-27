package be.umons.projetjava.domain;
import be.umons.projetjava.visitor.DeviceVisitor;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;
@Entity
@DiscriminatorValue("CAMERA")
public class CameraDevice extends Device {
    public CameraDevice() { super(); setDeviceType(DeviceType.CAMERA); }
    public CameraDevice(String name, DeviceStatus status, LocalDate installationDate, double sensitivity, double initialMotion) {
        super(name, DeviceType.CAMERA, status, installationDate, sensitivity, initialMotion);
    }
    public double getSensitivity() { return getParameter1(); }
    public void setSensitivity(double sensitivity) { setParameter1(sensitivity); }
    public double getMotionLevel() { return getMeasurement1(); }
    public void setMotionLevel(double motionLevel) { setMeasurement1(motionLevel); }
    @Override public void accept(DeviceVisitor v) { v.visit(this); }
}
