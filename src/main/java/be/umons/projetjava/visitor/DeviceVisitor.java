package be.umons.projetjava.visitor;
import be.umons.projetjava.domain.CameraDevice;
import be.umons.projetjava.domain.DeviceEvent;
import be.umons.projetjava.domain.ThermometerDevice;
public interface DeviceVisitor {
    String getName();
    String getTitle();
    void reset();
    void visitCamera(CameraDevice camera);
    void visitThermometer(ThermometerDevice thermometer);
    void visitEvent(DeviceEvent event);
    String getReportText();
}
