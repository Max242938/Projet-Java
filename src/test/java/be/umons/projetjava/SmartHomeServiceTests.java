package be.umons.projetjava;
import static org.junit.jupiter.api.Assertions.*;
import be.umons.projetjava.domain.*;
import be.umons.projetjava.observer.*;
import be.umons.projetjava.repository.*;
import be.umons.projetjava.service.*;
import be.umons.projetjava.visitor.*;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest
class SmartHomeServiceTests {
    @Autowired private DeviceService deviceService;
    @Autowired private DeviceEventService eventService;
    @Autowired private ObserverManager observerManager;
    @Autowired private VisitorManager visitorManager;
    @Autowired private DeviceStatusVisitor statusVisitor;
    @Autowired private DeviceRepository deviceRepository;
    @Autowired private DeviceEventRepository eventRepository;
    @BeforeEach void setUp() {
        eventRepository.deleteAll();
        deviceRepository.deleteAll();
        observerManager.enableObserver("emailObserver");
        observerManager.enableObserver("alarmObserver");
        observerManager.enableObserver("logObserver");
    }
    @Test void testCreateAndFindDevices() {
        deviceService.save(new CameraDevice("Camera Test", DeviceStatus.ACTIVE, LocalDate.now(), 50.0, 0.0));
        deviceService.save(new ThermometerDevice("Thermometre Test", DeviceStatus.ACTIVE, LocalDate.now(), 26.0, 20.0));
        assertEquals(2, deviceService.findAll().size());
        assertEquals("Camera Test", deviceService.findByType(DeviceType.CAMERA).get(0).getName());
        assertEquals("Thermometre Test", deviceService.findByType(DeviceType.THERMOMETER).get(0).getName());
    }
    @Test void testCameraAndThermometerEvents() {
        Device camera = deviceService.save(new CameraDevice("Camera Jardin", DeviceStatus.ACTIVE, LocalDate.now(), 50.0, 0.0));
        Device thermo = deviceService.save(new ThermometerDevice("Thermometre Cuisine", DeviceStatus.ACTIVE, LocalDate.now(), 25.0, 20.0));
        Device inactiveCam = deviceService.save(new CameraDevice("Camera Eteinte", DeviceStatus.INACTIVE, LocalDate.now(), 50.0, 0.0));
        assertNull(eventService.process(new DeviceMessage(camera.getId(), 15.0, null)));
        assertEquals(EventType.MOTION_DETECTED, eventService.process(new DeviceMessage(camera.getId(), 60.0, null)).getEventType());
        assertEquals(EventType.INTRUSION_DETECTED, eventService.process(new DeviceMessage(camera.getId(), 90.0, null)).getEventType());
        assertEquals(EventType.TEMPERATURE_CHANGED, eventService.process(new DeviceMessage(thermo.getId(), 23.0, null)).getEventType());
        assertEquals(EventType.TEMPERATURE_ABNORMAL, eventService.process(new DeviceMessage(thermo.getId(), 30.0, null)).getEventType());
        assertThrows(IllegalStateException.class, () -> eventService.process(new DeviceMessage(inactiveCam.getId(), 80.0, null)));
    }
    @Test void testObserverAttachAndDetach() {
        assertTrue(observerManager.isAttached("emailObserver"));
        observerManager.disableObserver("emailObserver");
        assertFalse(observerManager.isAttached("email"));
        assertFalse(observerManager.getActiveObserverNames().contains("emailObserver"));
        assertTrue(observerManager.attach("email"));
        assertTrue(observerManager.isAttached("emailObserver"));
        assertTrue(observerManager.detach("email"));
        assertFalse(observerManager.isAttached("emailObserver"));
        final int[] calls = {0};
        EventObserver fakeObserver = event -> calls[0]++;
        ObserverManager localManager = new ObserverManager(Map.of("testObserver", fakeObserver));
        DeviceEvent sampleEvent = new DeviceEvent(EventType.INTRUSION_DETECTED, null, 1L, 90.0, "Test");
        localManager.notifyAll(sampleEvent); assertEquals(1, calls[0]);
        localManager.disableObserver("testObserver"); localManager.notifyAll(sampleEvent); assertEquals(1, calls[0]);
        localManager.enableObserver("testObserver"); localManager.notifyAll(sampleEvent); assertEquals(2, calls[0]);
    }
    @Test void testVisitorsAndPdfExport() throws IOException {
        Device camera = deviceService.save(new CameraDevice("Camera Salon", DeviceStatus.ACTIVE, LocalDate.now(), 50.0, 0.0));
        Device thermo = deviceService.save(new ThermometerDevice("Thermometre Chambre", DeviceStatus.ACTIVE, LocalDate.now(), 25.0, 20.0));
        eventService.process(new DeviceMessage(camera.getId(), 65.0, "Mouvement"));
        eventService.process(new DeviceMessage(thermo.getId(), 23.0, "Hausse"));
        eventService.process(new DeviceMessage(thermo.getId(), 29.0, "Surchauffe"));
        List<Device> devices = deviceService.findAll();
        List<DeviceEvent> events = eventService.findAllEvents();
        statusVisitor.reset();
        visitorManager.accept(camera, "deviceStatusVisitor");
        assertTrue(statusVisitor.getReportText().contains("Camera Salon"));
        assertTrue(visitorManager.executeVisitor("status", devices, events).contains("Thermometre Chambre"));
        String statsReport = visitorManager.executeVisitor("eventReportVisitor", devices, events);
        assertTrue(statsReport.contains("Nombre total d'evenements : 3") && statsReport.contains("MOTION_DETECTED : 1")
                && statsReport.contains("TEMPERATURE_CHANGED : 1") && statsReport.contains("TEMPERATURE_ABNORMAL : 1")
                && statsReport.contains("Minimale") && statsReport.contains("Maximale") && statsReport.contains("Moyenne"));
        Path pdfPath = visitorManager.executeVisitorAndExportPdf("report", devices, events);
        assertTrue(Files.exists(pdfPath) && Files.size(pdfPath) > 0);
    }
}
