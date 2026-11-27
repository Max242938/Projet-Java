package be.umons.projetjava.visitor;
import be.umons.projetjava.domain.Device;
import be.umons.projetjava.domain.DeviceEvent;
import java.nio.file.Path;
import java.util.*;
import org.springframework.stereotype.Service;
@Service
public class VisitorManager {
    private final Map<String, DeviceVisitor> visitors; // injecte automatiquement par Spring (cle = nom du Bean)
    private final PdfReportExporter pdfExporter;
    public VisitorManager(Map<String, DeviceVisitor> visitors, PdfReportExporter pdfExporter) {
        this.visitors = visitors;
        this.pdfExporter = pdfExporter;
    }
    public List<String> getAvailableVisitorNames() { return new ArrayList<>(visitors.keySet()); }
    private String resolveBeanName(String operation) {
        if (operation == null) throw new IllegalArgumentException("Unknown operation: null");
        String trimmed = operation.trim();
        if (visitors.containsKey(trimmed)) return trimmed;
        for (String key : visitors.keySet()) {
            if (key.equalsIgnoreCase(trimmed) || key.toLowerCase().endsWith(trimmed.toLowerCase() + "visitor")) return key;
        }
        throw new IllegalArgumentException("Unknown operation: " + operation);
    }
    public void accept(Element element, String operation) {
        DeviceVisitor visitor = visitors.get(resolveBeanName(operation));
        element.accept(visitor);
    }
    public String executeVisitor(String operation, List<Device> devices, List<DeviceEvent> events) {
        DeviceVisitor visitor = visitors.get(resolveBeanName(operation));
        visitor.reset();
        if (devices != null) for (Device device : devices) device.accept(visitor);
        if (events != null) for (DeviceEvent event : events) event.accept(visitor);
        return visitor.getReportText();
    }
    public Path executeVisitorAndExportPdf(String operation, List<Device> devices, List<DeviceEvent> events) {
        String beanName = resolveBeanName(operation);
        DeviceVisitor visitor = visitors.get(beanName);
        return pdfExporter.exportToPdf(beanName, visitor.getTitle(), executeVisitor(beanName, devices, events));
    }
}
