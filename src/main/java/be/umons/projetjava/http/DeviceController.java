package be.umons.projetjava.http;
import be.umons.projetjava.domain.*;
import be.umons.projetjava.observer.ObserverManager;
import be.umons.projetjava.service.*;
import be.umons.projetjava.visitor.VisitorManager;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    private final DeviceService deviceService;
    private final DeviceEventService eventService;
    private final ObserverManager observerManager;
    private final VisitorManager visitorManager;
    public DeviceController(DeviceService deviceService, DeviceEventService eventService, ObserverManager observerManager, VisitorManager visitorManager) {
        this.deviceService = deviceService;
        this.eventService = eventService;
        this.observerManager = observerManager;
        this.visitorManager = visitorManager;
    }
    @GetMapping 
    public List<Device> getAllDevices() { return deviceService.findAll(); }
    @GetMapping("/{id}") 
    public ResponseEntity<?> getDeviceById(@PathVariable Long id) {
        try { return ResponseEntity.ok(deviceService.findById(id)); }
        catch (IllegalArgumentException ex) { return ResponseEntity.notFound().build(); }
    }
    @PostMapping("/camera") 
    public Device createCamera(@RequestParam String name, @RequestParam(defaultValue = "50.0") double sensitivity) {
        return deviceService.save(new CameraDevice(name, DeviceStatus.ACTIVE, LocalDate.now(), sensitivity, 0.0));
    }
    @PostMapping("/thermometer") 
    public Device createThermometer(@RequestParam String name, @RequestParam(defaultValue = "25.0") double threshold, @RequestParam(defaultValue = "20.0") double initialTemp) {
        return deviceService.save(new ThermometerDevice(name, DeviceStatus.ACTIVE, LocalDate.now(), threshold, initialTemp));
    }
    @PatchMapping("/{id}/status") 
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam DeviceStatus status) {
        try { return ResponseEntity.ok(deviceService.updateStatus(id, status)); }
        catch (IllegalArgumentException ex) { return ResponseEntity.notFound().build(); }
    }
    @GetMapping("/events") 
    public List<DeviceEvent> getAllEvents() { return eventService.findAllEvents(); }
    @GetMapping("/observers") 
    public Map<String, List<String>> getObservers() {
        Map<String, List<String>> result = new LinkedHashMap<>();
        result.put("available", observerManager.getAvailableObserverNames());
        result.put("active", observerManager.getActiveObserverNames());
        return result;
    }
    @PostMapping("/observers/{name}/attach") 
    public ResponseEntity<?> attachObserver(@PathVariable String name) {
        if (!observerManager.attach(name)) return ResponseEntity.badRequest().body("Observateur inconnu : " + name);
        return ResponseEntity.ok(observerManager.getActiveObserverNames());
    }
    @PostMapping("/observers/{name}/detach")
    public ResponseEntity<?> detachObserver(@PathVariable String name) {
        if (!observerManager.detach(name)) return ResponseEntity.badRequest().body("Observateur introuvable ou deja detache : " + name);
        return ResponseEntity.ok(observerManager.getActiveObserverNames());
    }
    @PostMapping("/visitors/{name}") 
    public ResponseEntity<?> runVisitor(@PathVariable String name, @RequestParam(required = false) Long deviceId) {
        try {
            List<Device> devices = (deviceId == null) ? deviceService.findAll() : Collections.singletonList(deviceService.findById(deviceId));
            List<DeviceEvent> events = (deviceId == null) ? eventService.findAllEvents() : eventService.findByDeviceId(deviceId);
            String report = visitorManager.executeVisitor(name, devices, events);
            Path pdfPath = visitorManager.executeVisitorAndExportPdf(name, devices, events);
            Map<String, String> response = new LinkedHashMap<>();
            response.put("pdfPath", pdfPath.toAbsolutePath().toString());
            response.put("report", report);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) { return ResponseEntity.badRequest().body(ex.getMessage()); }
    }
}
