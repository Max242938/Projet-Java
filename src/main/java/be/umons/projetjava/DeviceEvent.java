package be.umons.projetjava;

public class DeviceEvent {
    Long id;
    String event_type;
    String timestamps;
    String deviceld;//relation ManyToOne vers Device
    String value;
    String message;
    String[] type_event_allow ={"MOTION_DETECTED","INTRUSION_DETECTED","TEMPERATURE_CHANGED","TEMPERATURE_ABNORMAL"};

    
}
