package com.servicemanager;

import com.servicemanager.exception.*;
import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * ConfigLoader.java - STUB VERSION
 * 
 * TASK: Read services.json and create Service objects.
 * 
 * Concepts: FILE I/O + COLLECTIONS
 * ============================================
 * FILE I/O: Reading files from disk
 * COLLECTIONS: ArrayList to store multiple Services
 * JSON PARSING: Using Gson library
 * 
 * What you need:
 * =============
 * 1. FIELD for Gson parser
 * 2. FIELD for file path
 * 3. METHOD loadServices() returns List<Service>
 *    - Read file
 *    - Parse JSON
 *    - Create Service objects
 *    - Return list
 * 
 * Pseudocode:
 * ==========
 * CLASS ConfigLoader:
 *     FIELD gson: Gson
 *     FIELD filePath: String
 * 
 *     METHOD loadServices() RETURN List<Service>:
 *         1. Create FileReader for filePath
 *         2. Use gson to parse JSON
 *         3. FOR each service in JSON:
 *              - Get name
 *              - Get command
 *              - Get workingDir
 *              - Create Service(name, command, workingDir)
 *              - Add to list
 *         4. RETURN list
 */
public class ConfigLoader {
    
    private final Gson gson;
    private final String filePath;
    
    public ConfigLoader() {
        this("services.json");
    }
    
    public ConfigLoader(String filePath) {
        this.gson = new Gson();
        this.filePath = filePath;
    }
    
    public List<Service> loadServices() throws ConfigLoadException {
        // TODO: Implement - read file, parse JSON, create Service objects
        return new ArrayList<>();
    }
    
}