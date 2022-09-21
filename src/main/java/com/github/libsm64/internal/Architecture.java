package com.github.libsm64.internal;

import java.util.function.Predicate;

enum Architecture {
    AMD64((name) -> name.equals("amd64")),
    ;
    
    private final Predicate<String> predicate;
    
    Architecture(Predicate<String> predicate) {
        this.predicate = predicate;
    }
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
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
