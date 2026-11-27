package be.umons.projetjava.visitor;
import be.umons.projetjava.domain.Device;
import be.umons.projetjava.domain.DeviceEvent;
import java.nio.file.Path;
import java.util.*;
import org.springframework.stereotype.Service;
@Service
public class VisitorManager {
    private final Map<String, DeviceVisitor> availableVisitors = new LinkedHashMap<>();
    private final PdfReportExporter pdfExporter;
    public VisitorManager(List<DeviceVisitor> visitors, PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
        for (DeviceVisitor visitor : visitors) availableVisitors.put(visitor.getName().toLowerCase(), visitor);
    }
    public List<String> getAvailableVisitorNames() { return new ArrayList<>(availableVisitors.keySet()); }
    private DeviceVisitor findVisitor(String visitorName) {
        DeviceVisitor visitor = (visitorName == null) ? null : availableVisitors.get(visitorName.trim().toLowerCase());
        if (visitor == null) throw new IllegalArgumentException("Visiteur inconnu : " + visitorName);
        return visitor;
    }
    public String executeVisitor(String visitorName, List<Device> devices, List<DeviceEvent> events) {
        DeviceVisitor visitor = findVisitor(visitorName);
        visitor.reset();
        if (devices != null) for (Device device : devices) device.accept(visitor);
        if (events != null) for (DeviceEvent event : events) event.accept(visitor);
        return visitor.getReportText();
    }
    public Path executeVisitorAndExportPdf(String visitorName, List<Device> devices, List<DeviceEvent> events) {
        DeviceVisitor visitor = findVisitor(visitorName);
        return pdfExporter.exportToPdf(visitor.getName(), visitor.getTitle(), executeVisitor(visitorName, devices, events));
    }
}
