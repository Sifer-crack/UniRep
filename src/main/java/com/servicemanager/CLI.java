package com.servicemanager;

import com.servicemanager.exception.*;
import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CLI {
    private static final String USAGE = 
        "Usage: java ServiceManager <command> [args]\n\n" +
        "Commands:\n" +
        "  list                           Show all services\n" +
        "  start <service-name>          Start a service\n" +
        "  stop <service-name>            Stop a service\n" +
        "  restart <service-name>         Restart a service\n" +
        "  status <service-name>          Show service status\n" +
        "  logs <service-name> [lines]    Show service logs\n" +
        "  help                           Show this help";
    
    private static final String MENU = 
        "=== SERVICE MANAGER ===\n" +
        "1. List all services\n" +
        "2. Start a service\n" +
        "3. Stop a service\n" +
        "4. Restart a service\n" +
        "5. View status\n" +
        "6. View logs\n" +
        "7. Help\n" +
        "0. Exit\n";

    public static void main(String[] args) {
        if (args.length == 0) {
            runInteractive();
            return;
        }

        String command = args[0];
        ServiceManager sm;

        try {
            sm = new ServiceManager();
        } catch (ConfigLoadException e) {
            System.err.println("Config Error: " + e.getMessage());
            System.err.println(e.getRecoveryHint());
            return;
        }

        try {
            String result;
            switch (command) {
                case "list":
                    result = sm.listServices();
                    System.out.print(result);
                    break;
                case "start":
                    validateArgs(args, 2);
                    result = sm.startService(args[1]);
                    System.out.println(result);
                    break;
                case "stop":
                    validateArgs(args, 2);
                    result = sm.stopService(args[1]);
                    System.out.println(result);
                    break;
                case "restart":
                    validateArgs(args, 2);
                    result = sm.restartService(args[1]);
                    System.out.println(result);
                    break;
                case "status":
                    validateArgs(args, 2);
                    result = sm.getServiceStatus(args[1]);
                    System.out.println(result);
                    break;
                case "logs":
                    validateArgs(args, 2);
                    int lines = args.length > 2 ? Integer.parseInt(args[2]) : 50;
                    result = sm.getServiceLogs(args[1], lines);
                    System.out.println(result);
                    break;
                case "help":
                case "-h":
                    System.out.println(USAGE);
                    break;
                default:
                    throw new InvalidCommandException(command);
            }
        } catch (ServiceException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Hint: " + e.getRecoveryHint());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void validateArgs(String[] args, int required) throws InvalidCommandException {
        if (args.length < required) {
            throw new InvalidCommandException(args[0]);
        }
    }

    private static void waitForEnter(Scanner scanner) {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void saveCustomService(String name, String command) {
        String customPath = "src/main/resources/custom.json";
        List<Map<String, String>> customServices = new ArrayList<>();

        File file = new File(customPath);
        if (file.exists()) {
            try (Reader reader = Files.newBufferedReader(Paths.get(customPath))) {
                JsonObject json = new Gson().fromJson(reader, JsonObject.class);
                JsonArray arr = json.getAsJsonArray("services");
                for (JsonElement e : arr) {
                    JsonObject s = e.getAsJsonObject();
                    Map<String, String> svc = new HashMap<>();
                    svc.put("name", s.get("name").getAsString());
                    svc.put("command", s.get("command").getAsString());
                    svc.put("workingDir", s.get("workingDir").getAsString());
                    customServices.add(svc);
                }
            } catch (Exception e) {
            }
        }

        Map<String, String> newSvc = new HashMap<>();
        newSvc.put("name", name);
        newSvc.put("command", command);
        newSvc.put("workingDir", "");
        customServices.add(newSvc);

        JsonArray arr = new JsonArray();
        for (Map<String, String> svc : customServices) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", svc.get("name"));
            obj.addProperty("command", svc.get("command"));
            obj.addProperty("workingDir", svc.get("workingDir"));
            arr.add(obj);
        }

        JsonObject root = new JsonObject();
        root.add("services", arr);

        try (Writer writer = Files.newBufferedWriter(Paths.get(customPath))) {
            writer.write(new Gson().toJson(root));
        } catch (Exception e) {
            System.err.println("Warning: Could not save custom service to file");
        }
    }

    private static void runInteractive() {
        Scanner scanner = new Scanner(System.in);
        ServiceManager sm = null;
        
        try {
            sm = new ServiceManager();
        } catch (ConfigLoadException e) {
            System.err.println("Config Error: " + e.getMessage());
            return;
        }
        
        while (true) {
            System.out.print(MENU + "\nEnter choice: ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) continue;
            
            try {
                int choice = Integer.parseInt(input);
                
                switch (choice) {
                    case 0:
                        System.out.println("Goodbye!");
                        return;
                    case 1:
                        System.out.print(sm.listServices());
                        waitForEnter(scanner);
                        break;
                    case 2:
                        System.out.println("1. Preloaded services");
                        System.out.println("2. Create custom service");
                        System.out.print("Choose option: ");
                        String svcChoice = scanner.nextLine().trim();

                        if ("2".equals(svcChoice)) {
                            System.out.print("Enter service name: ");
                            String customName = scanner.nextLine().trim();
                            System.out.print("Enter command: ");
                            String customCommand = scanner.nextLine().trim();
                            System.out.println("WARNING: You are responsible for writing valid commands!");
                            saveCustomService(customName, customCommand);
                            System.out.println("Custom service '" + customName + "' created.");
                        } else {
                            System.out.print("Service name: ");
                            String startName = scanner.nextLine().trim();
                            System.out.println(sm.startService(startName));
                        }
                        waitForEnter(scanner);
                        break;
                    case 3:
                        System.out.print("Service name: ");
                        String stopName = scanner.nextLine().trim();
                        System.out.println(sm.stopService(stopName));
                        waitForEnter(scanner);
                        break;
                    case 4:
                        System.out.print("Service name: ");
                        String restartName = scanner.nextLine().trim();
                        System.out.println(sm.restartService(restartName));
                        waitForEnter(scanner);
                        break;
                    case 5:
                        System.out.print("Service name: ");
                        String statusName = scanner.nextLine().trim();
                        System.out.println(sm.getServiceStatus(statusName));
                        waitForEnter(scanner);
                        break;
                    case 6:
                        System.out.print("Service name: ");
                        String logsName = scanner.nextLine().trim();
                        System.out.println(sm.getServiceLogs(logsName, 50));
                        waitForEnter(scanner);
                        break;
                    case 7:
                        System.out.println(USAGE);
                        waitForEnter(scanner);
                        break;
                    default:
                        System.out.println("Invalid choice");
                }
            } catch (ServiceException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}
