package be.umons.projetjava.simulator;

import be.umons.projetjava.service.DeviceEventService;

public class DeviceSimulator {
    private final DeviceEventService eventService; 
  
    public DeviceSimulator(DeviceEventService eventService) { 
        this.eventService = eventService; 
    } 
  
    /*@Override 
    public void run(String... args) { 
        // Envoyer plusieurs DeviceMessage fictifs : 
        // mouvement, intrusion, température normale, 
        // variation importante, température anormale. 
        // Chaque message passe par eventService.process(...). 
    } */
}
