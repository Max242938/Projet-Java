package be.umons.projetjava.service;
import be.umons.projetjava.domain.*;
import be.umons.projetjava.repository.DeviceRepository;
import java.util.List;
import org.springframework.stereotype.Service;
@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    public DeviceService(DeviceRepository deviceRepository) { this.deviceRepository = deviceRepository; }
    public List<Device> findAll() { return deviceRepository.findAll(); }
    public Device findById(Long id) {
        return deviceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Appareil inconnu : " + id));
    }
    public List<Device> findByType(DeviceType type) { return deviceRepository.findByDeviceType(type); }
    public List<Device> findByStatus(DeviceStatus status) { return deviceRepository.findByStatus(status); }
    public Device save(Device device) { return deviceRepository.save(device); }
    public Device updateStatus(Long id, DeviceStatus newStatus) {
        Device device = findById(id);
        device.setStatus(newStatus);
        return deviceRepository.save(device);
    }
    public Device updateParameter(Long id, double newParameter) {
        Device device = findById(id);
        device.setParameter1(newParameter);
        return deviceRepository.save(device);
    }
}
