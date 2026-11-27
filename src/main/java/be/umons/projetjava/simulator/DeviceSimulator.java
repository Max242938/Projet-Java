package be.umons.projetjava.simulator;
import be.umons.projetjava.domain.*;
import be.umons.projetjava.service.*;
import java.time.LocalDate;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
@Component
@Order(1)
@ConditionalOnProperty(name = "smarthome.simulator.enabled", havingValue = "true", matchIfMissing = true)
public class DeviceSimulator implements CommandLineRunner {
    private final DeviceService deviceService;
    private final DeviceEventService eventService;
    private final Random random = new Random();
    @Value("${smarthome.simulator.loop-enabled:true}") private boolean loopEnabled;
    @Value("${smarthome.simulator.interval-ms:10000}") private long intervalMs;
    public DeviceSimulator(DeviceService deviceService, DeviceEventService eventService) {
        this.deviceService = deviceService;
        this.eventService = eventService;
    }
    @Override public void run(String... args) {
        System.out.println("\n=== [SIMULATEUR] Initialisation et simulation d'evenements ===");
        if (deviceService.findAll().isEmpty()) {
            deviceService.save(new CameraDevice("Camera Entree", DeviceStatus.ACTIVE, LocalDate.now(), 50.0, 0.0));
            deviceService.save(new ThermometerDevice("Thermometre Salon", DeviceStatus.ACTIVE, LocalDate.now(), 25.0, 20.0));
            deviceService.save(new CameraDevice("Camera Garage", DeviceStatus.MAINTENANCE, LocalDate.now(), 40.0, 0.0));
            System.out.println("[SIMULATEUR] 3 appareils crees en base de donnees.");
        }
        Device activeCamera = null, activeThermometer = null;
        for (Device device : deviceService.findAll()) {
            if (device.getStatus() == DeviceStatus.ACTIVE) {
                if (device.getDeviceType() == DeviceType.CAMERA && activeCamera == null) activeCamera = device;
                else if (device.getDeviceType() == DeviceType.THERMOMETER && activeThermometer == null) activeThermometer = device;
            }
        }
        if (activeCamera != null) {
            sendAndPrint(new DeviceMessage(activeCamera.getId(), 20.0, "Passage normal devant l'entree"));
            sendAndPrint(new DeviceMessage(activeCamera.getId(), 60.0, "Mouvement suspect devant l'entree"));
            sendAndPrint(new DeviceMessage(activeCamera.getId(), 85.0, "Intrusion detectee a l'entree !"));
        }
        if (activeThermometer != null) {
            sendAndPrint(new DeviceMessage(activeThermometer.getId(), 20.5, "Temperature ambiante normale"));
            sendAndPrint(new DeviceMessage(activeThermometer.getId(), 23.5, "Hausse rapide de la temperature"));
            sendAndPrint(new DeviceMessage(activeThermometer.getId(), 31.0, "Surchauffe detectee dans le salon !"));
        }
        System.out.println("=== [SIMULATEUR] Fin de la simulation initiale ===\n");
        if (loopEnabled) startBackgroundLoop(activeCamera, activeThermometer); // meme principe que MainSimulator (Exo 31)
    }
    private void startBackgroundLoop(Device camera, Device thermometer) {
        Thread simulatorThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(intervalMs);
                    if (camera != null && deviceService.findById(camera.getId()).getStatus() == DeviceStatus.ACTIVE) {
                        double motion = Math.round((10.0 + random.nextDouble() * 85.0) * 100.0) / 100.0;
                        eventService.process(new DeviceMessage(camera.getId(), motion, null));
                    }
                    if (thermometer != null && deviceService.findById(thermometer.getId()).getStatus() == DeviceStatus.ACTIVE) {
                        double temp = Math.round((15.0 + random.nextDouble() * 16.0) * 100.0) / 100.0;
                        eventService.process(new DeviceMessage(thermometer.getId(), temp, null));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception ignored) {}
            }
        }, "device-simulator");
        simulatorThread.setDaemon(true);
        simulatorThread.start();
    }
    private void sendAndPrint(DeviceMessage message) {
        DeviceEvent event = eventService.process(message);
        if (event == null) System.out.println("[SIMULATEUR] Mesure recue (appareil=" + message.getDeviceId() + ", valeur=" + message.getValue() + ") -> Situation normale.");
        else System.out.println("[SIMULATEUR] Evenement genere -> " + event.getEventType() + " (appareil=" + event.getDeviceId() + ", valeur=" + event.getValue() + ")");
    }
}
