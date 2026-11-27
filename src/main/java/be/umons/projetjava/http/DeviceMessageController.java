package be.umons.projetjava.http;
import be.umons.projetjava.domain.DeviceEvent;
import be.umons.projetjava.domain.DeviceMessage;
import be.umons.projetjava.service.DeviceEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/messages")
public class DeviceMessageController {
    private final DeviceEventService eventService;
    public DeviceMessageController(DeviceEventService eventService) { this.eventService = eventService; }
    @PostMapping
    public ResponseEntity<?> receiveMessage(@RequestBody DeviceMessage message) {
        try {
            DeviceEvent event = eventService.process(message);
            return ResponseEntity.ok(event == null ? "Mesure mise a jour : situation normale (aucun evenement cree)." : event);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
