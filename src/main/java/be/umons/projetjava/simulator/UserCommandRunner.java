package be.umons.projetjava.simulator;
import be.umons.projetjava.domain.*;
import be.umons.projetjava.observer.ObserverManager;
import be.umons.projetjava.service.*;
import be.umons.projetjava.visitor.VisitorManager;
import java.nio.file.Path;
import java.util.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
@Component
@Order(2)
@ConditionalOnProperty(name = "smarthome.console.enabled", havingValue = "true", matchIfMissing = true)
public class UserCommandRunner implements CommandLineRunner {
    private final DeviceService deviceService;
    private final DeviceEventService eventService;
    private final ObserverManager observerManager;
    private final VisitorManager visitorManager;
    private final ConfigurableApplicationContext context;
    public UserCommandRunner(DeviceService deviceService, DeviceEventService eventService, ObserverManager observerManager, VisitorManager visitorManager, ConfigurableApplicationContext context) {
        this.deviceService = deviceService;
        this.eventService = eventService;
        this.observerManager = observerManager;
        this.visitorManager = visitorManager;
        this.context = context;
    }
    @Override 
    public void run(String... args) { new Thread(this::runInteractiveMenu, "smarthome-console").start(); }
    private void runInteractiveMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            printMenu();
            String choice;
            try {
                if (!scanner.hasNextLine()) break;
                choice = scanner.nextLine().trim();
            } catch (NoSuchElementException | IllegalStateException ex) { break; }
            try {
                switch (choice) {
                    case "1" -> listDevices();
                    case "2" -> listEvents();
                    case "3" -> sendSimulatedMessage(scanner);
                    case "4" -> changeDeviceStatus(scanner);
                    case "5" -> manageObservers(scanner);
                    case "6" -> runVisitorMenu(scanner);
                    case "0" -> { running = false; System.out.println("Arret de l'application Smart Home..."); SpringApplication.exit(context, () -> 0); }
                    default -> System.out.println("Choix invalide. Entrez un chiffre entre 0 et 6.");
                }
            } catch (Exception ex) { System.out.println("Erreur : " + ex.getMessage()); }
        }
    }
    private void printMenu() {
        System.out.println("\n================ MENU SMART HOME ================");
        System.out.println("1. Lister les appareils\n2. Lister les evenements\n3. Envoyer un message simule\n4. Changer l'etat d'un appareil\n5. Gerer les observateurs (attach / detach)\n6. Executer un visiteur et exporter en PDF\n0. Quitter");
        System.out.print("Votre choix : ");
    }
    private void listDevices() {
        List<Device> devices = deviceService.findAll();
        System.out.println("\n--- Appareils installes ---");
        if (devices.isEmpty()) System.out.println("(Aucun appareil)");
        else for (Device d : devices) System.out.println(d);
    }
    private void listEvents() {
        List<DeviceEvent> events = eventService.findAllEvents();
        System.out.println("\n--- Evenements enregistres (" + events.size() + ") ---");
        if (events.isEmpty()) System.out.println("(Aucun evenement)");
        else for (DeviceEvent e : events) System.out.println(e);
    }
    private void sendSimulatedMessage(Scanner scanner) {
        listDevices();
        System.out.print("ID de l'appareil : ");
        Long deviceId = Long.parseLong(scanner.nextLine().trim());
        System.out.print("Valeur mesuree (ex: 75.0 ou 29.5) : ");
        Double value = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Message facultatif (Entree pour defaut) : ");
        DeviceEvent event = eventService.process(new DeviceMessage(deviceId, value, scanner.nextLine().trim()));
        System.out.println(event == null ? "Mesure mise a jour : situation normale (aucun evenement cree)." : "Evenement cree : " + event);
    }
    private void changeDeviceStatus(Scanner scanner) {
        listDevices();
        System.out.print("ID de l'appareil : ");
        Long deviceId = Long.parseLong(scanner.nextLine().trim());
        System.out.print("Nouvel etat (ACTIVE, INACTIVE, MAINTENANCE) : ");
        System.out.println("Appareil mis a jour : " + deviceService.updateStatus(deviceId, DeviceStatus.valueOf(scanner.nextLine().trim().toUpperCase())));
    }
    private void manageObservers(Scanner scanner) {
        System.out.println("\nObservateurs disponibles : " + observerManager.getAvailableObserverNames());
        System.out.println("Observateurs actifs      : " + observerManager.getActiveObserverNames());
        System.out.print("Action (attach / detach / retour) : ");
        String action = scanner.nextLine().trim().toLowerCase();
        if ("retour".equals(action) || action.isEmpty()) return;
        System.out.print("Nom de l'observateur (email / alarm / log) : ");
        String name = scanner.nextLine().trim();
        if ("attach".equals(action)) System.out.println(observerManager.attach(name) ? "Observateur attache." : "Observateur inconnu.");
        else if ("detach".equals(action)) System.out.println(observerManager.detach(name) ? "Observateur detache." : "Observateur introuvable ou deja detache.");
        else System.out.println("Action inconnue.");
        System.out.println("Observateurs actifs : " + observerManager.getActiveObserverNames());
    }
    private void runVisitorMenu(Scanner scanner) {
        System.out.println("\nVisiteurs disponibles : " + visitorManager.getAvailableVisitorNames());
        System.out.print("Nom du visiteur (status / report) : ");
        String visitorName = scanner.nextLine().trim();
        System.out.print("ID d'un appareil precis (ou Entree pour tous les appareils) : ");
        String idInput = scanner.nextLine().trim();
        List<Device> devices = idInput.isEmpty() ? deviceService.findAll() : Collections.singletonList(deviceService.findById(Long.parseLong(idInput)));
        List<DeviceEvent> events = idInput.isEmpty() ? eventService.findAllEvents() : eventService.findByDeviceId(Long.parseLong(idInput));
        System.out.println("\n" + visitorManager.executeVisitor(visitorName, devices, events));
        Path pdfPath = visitorManager.executeVisitorAndExportPdf(visitorName, devices, events);
        System.out.println("Rapport PDF sauvegarde dans : " + pdfPath.toAbsolutePath());
    }
}
