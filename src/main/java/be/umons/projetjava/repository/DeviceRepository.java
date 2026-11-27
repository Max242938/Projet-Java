package be.umons.projetjava.repository;
import be.umons.projetjava.domain.Device;
import be.umons.projetjava.domain.DeviceStatus;
import be.umons.projetjava.domain.DeviceType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByDeviceType(DeviceType deviceType);
    List<Device> findByStatus(DeviceStatus status);
}
