package com.github.libsm64.internal;

import java.util.function.Predicate;

/**
 * A simple OS enumeration.
 */
enum OperatingSystem {
    LINUX((name) -> name.toLowerCase().contains("linux"), ".so"),
    ;
    
    /**
     * The predicate used to figure out the current operating system.
     */
    private final Predicate<String> predicate;
    
    /**
     * The extension used by the OS for natives.
     */
    private final String extension;
    
    OperatingSystem(Predicate<String> predicate, String extension) {
        this.predicate = predicate;
        this.extension = extension;
    }
    
    /**
     * The extension that the OS uses for natives.
     *
     * @return The native extension
     */
    public String extension() {
        return extension;
    }
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
    /**
     * Gets the operating system of the current JVM.
     *
     * @return The current OS
     */
    public static OperatingSystem get() {
        var os = System.getProperty("os.name");
        
        for (var value : values()) {
            if (value.predicate.test(os)) {
                return value;
            }
        }
    
        throw new RuntimeException("Unsupported operating system: " + os);
    }
}
