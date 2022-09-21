package com.github.libsm64.internal;

import java.util.function.Predicate;

enum OperatingSystem {
    LINUX((name) -> name.toLowerCase().contains("linux"), ".so"),
    ;
    
    private final Predicate<String> predicate;
    private final String extension;
    
    OperatingSystem(Predicate<String> predicate, String extension) {
        this.predicate = predicate;
        this.extension = extension;
    }
    
    public String extension() {
        return extension;
    }
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
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
