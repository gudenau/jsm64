package com.github.libsm64.internal;

import java.io.IOException;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class Binder {
    private static Binder binder;
    
    public static Binder load() {
        if (binder == null) {
            try {
                binder = new Binder(extractNatives());
            } catch (IOException e) {
                throw new RuntimeException("Failed to extract libsm64 natives");
            }
        }
        return binder;
    }
    
    private static Path extractNatives() throws IOException {
        var os = OperatingSystem.get();
        var arch = Architecture.get();
        
        var input = Binder.class.getResourceAsStream("/natives/libsm64/" + os + '/' + arch + "/libsm64.so");
        if (input == null) {
            throw new RuntimeException("Unsupported operating system/architecture combination: " + os + ", " + arch);
        }
    
        try (input) {
            var tempFile = Files.createTempFile("libsm64", os.extension());
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
            return tempFile;
        }
    }
    
    private final Linker linker;
    private final SymbolLookup lookup;
    private final MemorySession session = MemorySession.openShared();
    
    private Binder(Path natives) {
        if (binder != null) {
            throw new AssertionError();
        }
    
        linker = Linker.nativeLinker();
        lookup = SymbolLookup.libraryLookup(natives, session);
        
        try {
            Files.delete(natives);
        } catch (IOException ignored) {
            // Windows is weird, deal with the weirdness.
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                session.close();
                try {
                    Files.delete(natives);
                } catch (IOException ignored2) {}
            }, "jSM64 Cleanup"));
        }
    }
    
    public MethodHandle downcall(String name, FunctionDescriptor descriptor) {
        if (name == null) {
            return linker.downcallHandle(descriptor);
        }
        
        return lookup.lookup(name)
            .map((symbol) -> linker.downcallHandle(symbol, descriptor))
            .orElseThrow(() -> new IllegalArgumentException("Failed to find symbol " + name));
    }
    
    public MethodHandle downcall(String name, MemoryLayout result, MemoryLayout... arguments) {
        return downcall(name, result == null ? FunctionDescriptor.ofVoid(arguments) : FunctionDescriptor.of(result, arguments));
    }
    
    public MemorySegment upcall(MethodHandle handle, FunctionDescriptor descriptor, MemorySession session) {
        return linker.upcallStub(handle, descriptor, session);
    }
}
