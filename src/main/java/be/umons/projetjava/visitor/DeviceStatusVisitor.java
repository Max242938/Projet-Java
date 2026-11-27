package be.umons.projetjava.visitor;
import be.umons.projetjava.domain.*;
import org.springframework.stereotype.Component;
@Component
public class DeviceStatusVisitor implements DeviceVisitor {
    private StringBuilder details = new StringBuilder();
    private int totalDevices = 0, activeCount = 0, inactiveCount = 0, maintenanceCount = 0;
    @Override public String getName() { return "status"; }
    @Override public String getTitle() { return "Rapport d'etat des appareils"; }
    @Override public void reset() {
        details = new StringBuilder();
        totalDevices = 0; activeCount = 0; inactiveCount = 0; maintenanceCount = 0;
    }
    private void countStatus(DeviceStatus status) {
        totalDevices++;
        if (status == DeviceStatus.ACTIVE) activeCount++;
        else if (status == DeviceStatus.INACTIVE) inactiveCount++;
        else if (status == DeviceStatus.MAINTENANCE) maintenanceCount++;
    }
    @Override public void visitCamera(CameraDevice camera) {
        countStatus(camera.getStatus());
        details.append("- [CAMERA] ").append(camera.getName()).append(" (ID=").append(camera.getId()).append(")")
                .append(" | Etat=").append(camera.getStatus()).append(" | Install=").append(camera.getInstallationDate())
                .append(" | Sensibilite=").append(camera.getSensitivity()).append(" | Mouvement=").append(camera.getMotionLevel()).append("\n");
    }
    @Override public void visitThermometer(ThermometerDevice thermometer) {
        countStatus(thermometer.getStatus());
        details.append("- [THERMOMETRE] ").append(thermometer.getName()).append(" (ID=").append(thermometer.getId()).append(")")
                .append(" | Etat=").append(thermometer.getStatus()).append(" | Install=").append(thermometer.getInstallationDate())
                .append(" | Seuil=").append(thermometer.getTemperatureThreshold()).append(" C")
                .append(" | Temperature=").append(thermometer.getCurrentTemperature()).append(" C\n");
    }
    @Override public void visitEvent(DeviceEvent event) {}
    @Override public String getReportText() {
        StringBuilder report = new StringBuilder();
        report.append("Total appareils inspectes : ").append(totalDevices).append("\n")
                .append("  Actifs      : ").append(activeCount).append("\n")
                .append("  Inactifs    : ").append(inactiveCount).append("\n")
                .append("  Maintenance : ").append(maintenanceCount).append("\n\nDetail des appareils :\n");
        report.append(totalDevices == 0 ? "(Aucun appareil)\n" : details);
        return report.toString();
    }
}
