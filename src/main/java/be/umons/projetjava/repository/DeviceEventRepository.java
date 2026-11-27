package be.umons.projetjava.repository;
import be.umons.projetjava.domain.DeviceEvent;
import be.umons.projetjava.domain.EventType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DeviceEventRepository extends JpaRepository<DeviceEvent, Long> {
    List<DeviceEvent> findByDeviceId(Long deviceId);
    List<DeviceEvent> findByDeviceIdAndTimestampBetween(Long deviceId, LocalDateTime from, LocalDateTime to);
    List<DeviceEvent> findByEventType(EventType eventType);
    List<DeviceEvent> findByTimestampBetween(LocalDateTime from, LocalDateTime to);
}
