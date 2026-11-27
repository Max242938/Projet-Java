package be.umons.projetjava.visitor;
import be.umons.projetjava.domain.*;
public interface DeviceVisitor {
    void visit(CameraDevice camera);
    void visit(ThermometerDevice thermometer);
    void visit(DeviceEvent event);
    void reset();
    String getTitle();
    String getReportText();
}
