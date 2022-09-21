package com.github.libsm64.internal;

import java.util.function.Predicate;

/**
 * A simple architecture enumeration.
 */
enum Architecture {
    /**
     * AMD64, it goes by many names such as x64 or x86_64...
     */
    AMD64((name) -> name.equals("amd64")),
    ;
    
    /**
     * The predicate used to figure out what architecture the JVM is using.
     */
    private final Predicate<String> predicate;
    
    Architecture(Predicate<String> predicate) {
        this.predicate = predicate;
    }
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
    /**
     * Gets the architecture of the current JVM.
     *
     * @return The current JVM architecture
     * @throws RuntimeException If the current architecture is unknown to this library
     */
    public static Architecture get() {
        var arch = System.getProperty("os.arch");
        
        for (var value : values()) {
            if (value.predicate.test(arch)) {
                return value;
            }
        }
    
        throw new RuntimeException("Unsupported architecture: " + arch);
    }
}
