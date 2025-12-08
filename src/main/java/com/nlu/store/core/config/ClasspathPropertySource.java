package com.nlu.store.core.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Concrete implementation of {@link PropertySource} that loads properties
 * from a file in the classpath.
 */
@ApplicationScoped
public class ClasspathPropertySource extends PropertySource {

    private final Properties properties;
    private final String resourceName;

    public ClasspathPropertySource() {
        this("application.properties");
    }

    public ClasspathPropertySource(String resourceName) {
        this.resourceName = resourceName;
        this.properties = new Properties();
        loadProperties(resourceName);
    }

    private void loadProperties(String fileName) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                System.err.println("Warning: Property source '" + fileName + "' not found in classpath.");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load property source: " + fileName, ex);
        }
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
