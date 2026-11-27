package be.umons.projetjava.domain;
import be.umons.projetjava.visitor.DeviceVisitor;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;
@Entity
@DiscriminatorValue("THERMOMETER")
public class ThermometerDevice extends Device {
    public ThermometerDevice() { super(); setDeviceType(DeviceType.THERMOMETER); }
    public ThermometerDevice(String name, DeviceStatus status, LocalDate installationDate, double maxTemperatureThreshold, double initialTemperature) {
        super(name, DeviceType.THERMOMETER, status, installationDate, maxTemperatureThreshold, initialTemperature);
    }
    public double getTemperatureThreshold() { return getParameter1(); }
    public void setTemperatureThreshold(double threshold) { setParameter1(threshold); }
    public double getCurrentTemperature() { return getMeasurement1(); }
    public void setCurrentTemperature(double temperature) { setMeasurement1(temperature); }
    @Override public void accept(DeviceVisitor v) { v.visit(this); }
}
