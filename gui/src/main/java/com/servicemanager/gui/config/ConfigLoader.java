package com.servicemanager.gui.config;

import com.google.gson.*;
import com.servicemanager.gui.exception.ConfigLoadException;
import com.servicemanager.gui.model.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/** Loads service definitions from a JSON configuration file.
 *  Supports loading from the classpath by default (for runtime)
 *  or from the filesystem path (for testing with custom files).
 *  The JSON format is: { "services": [{ "name": "...", "command": "..." }] } */
public class ConfigLoader {

    private final Gson gson;
    private final String filePath;
    private final boolean useClasspath;

    public ConfigLoader() {
        this.gson = new Gson();
        this.filePath = "services.json";
        this.useClasspath = true;
    }

    public ConfigLoader(String filePath) {
        this.gson = new Gson();
        this.filePath = filePath;
        this.useClasspath = false;
    }

    public List<Service> loadServices() throws ConfigLoadException {
        if (useClasspath) {
            return loadFromClasspath(filePath);
        }
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        return loadFromFile(filePath);
    }

    private List<Service> loadFromClasspath(String resource) throws ConfigLoadException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resource)) {
            if (is == null) {
                return new ArrayList<>();
            }
            try (Reader reader = new InputStreamReader(is)) {
                return parseServices(reader);
            }
        } catch (Exception e) {
            throw new ConfigLoadException("Failed to load services from classpath: " + resource);
        }
    }

    private List<Service> loadFromFile(String path) throws ConfigLoadException {
        try (Reader reader = Files.newBufferedReader(Paths.get(path))) {
            return parseServices(reader);
        } catch (FileNotFoundException e) {
            throw new ConfigLoadException("Config file not found: " + path);
        } catch (Exception e) {
            throw new ConfigLoadException("Failed to load services from config file: " + path);
        }
    }

    private List<Service> parseServices(Reader reader) {
        List<Service> services = new ArrayList<>();
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

        if (jsonObject == null || !jsonObject.has("services")) {
            return services;
        }

        JsonArray servicesArray = jsonObject.getAsJsonArray("services");

        for (JsonElement element : servicesArray) {
            JsonObject serviceObject = element.getAsJsonObject();

            String name = serviceObject.get("name").getAsString();
            String command = serviceObject.get("command").getAsString();
            String workingDir = "";
            if (serviceObject.has("workingDir") && !serviceObject.get("workingDir").isJsonNull()) {
                workingDir = serviceObject.get("workingDir").getAsString();
            }

            services.add(new Service(name, command, workingDir));
        }

        return services;
    }
}
