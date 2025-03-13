package com.demo.finance.app.config;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EnvLoader {

    /**
     * Loads environment variables from a .env file.
     *
     * @param filePath Path to the .env file (e.g., ".env", ".env.dev")
     * @return A map of key-value pairs from the .env file
     * @throws RuntimeException If the file is missing, malformed, or contains duplicate keys
     */
    public static Map<String, String> loadEnv(String filePath) {
        Map<String, String> envVars = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Split line into key-value pair
                String[] parts = line.split("=", 2);
                if (parts.length != 2) {
                    throw new RuntimeException("Malformed line in .env file at line " + lineNumber + ": " + line);
                }

                String key = parts[0].trim();
                String value = parts[1].trim();

                // Check for duplicate keys
                if (envVars.containsKey(key)) {
                    throw new RuntimeException("Duplicate key found in .env file: " + key);
                }

                envVars.put(key, value);
            }
        } catch (IOException e) {
            log.error("Error loading .env file: {}", filePath, e);
            throw new RuntimeException("Unable to find or read .env file: " + filePath, e);
        }

        log.info("Successfully loaded {} environment variables from {}", envVars.size(), filePath);
        return envVars;
    }
}