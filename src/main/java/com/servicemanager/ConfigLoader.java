package com.servicemanager;

import com.servicemanager.exception.*;
import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ConfigLoader {

    private final Gson gson;
    private final String filePath;
    private static final String CUSTOM_CONFIG_PATH = "src/main/resources/custom.json";

    public ConfigLoader() {
        this("src/main/resources/services.json");
    }

    public ConfigLoader(String filePath) {
        this.gson = new Gson();
        this.filePath = filePath;
    }

    public List<Service> loadServices() throws ConfigLoadException {
		
		List<Service> preloaded = loadFromFile(filePath);
		List<Service> services = new ArrayList<>(preloaded);

        if (Files.exists(Paths.get(CUSTOM_CONFIG_PATH))) {
            try {
                List<Service> custom = loadFromFile(CUSTOM_CONFIG_PATH);
                services.addAll(custom);
            } catch (ConfigLoadException e) {
                // Custom file exists but has errors - just skip it
            }
        }

        return services;
    }

    private List<Service> loadFromFile(String path) throws ConfigLoadException {
        List<Service> services = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(Paths.get(path))) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            JsonArray servicesArray = jsonObject.getAsJsonArray("services");

            for (JsonElement element : servicesArray) {
                JsonObject serviceObject = element.getAsJsonObject();

                String name = serviceObject.get("name").getAsString();
                String command = serviceObject.get("command").getAsString();
                String workingDir = serviceObject.get("workingDir").getAsString();

                Service service = new Service(name, command, workingDir);
                services.add(service);
            }

            return services;

        } catch (Exception e) {
            throw new ConfigLoadException("Failed to load services from config file: " + path);
        }
    }
} 