package com.github.libsm64.internal;

import java.io.IOException;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * A simple helper for creating Java bindings to the libsm64 library.
 */
public final class Binder {
    /**
     * The cached binder instance, or `null` if not yet loaded.
     */
    private static Binder binder;
    
    /**
     * Creates a new instance of binder, or gets the existing one.
     *
     * @return A binder instance
     * @throws RuntimeException If the natives could not be loaded
     */
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
    
    /**
     * Extracts the natives for the current platform into a temporary directory.
     *
     * @return The {@link Path Path} of the extracted natives
     * @throws IOException If the natives could not be written
     * @throws RuntimeException If the current platform and/or architecture is not supported
     */
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
    
    /**
     * The native linker for this binder.
     */
    private final Linker linker;
    
    /**
     * The symbol looking for the libsm64 natives.
     */
    private final SymbolLookup lookup;
    
    /**
     * The session for the {@link #lookup lookup}.
     */
    private final MemorySession session = MemorySession.openShared();
    
    /**
     * Opens the provided libsm64 natives. If on Windows the natives file will be deleted on JVM shutdown, on *nix
     * platforms it will be deleted after loading.
     *
     * @param natives The natives to read
     */
    private Binder(Path natives) {
        // Only allow a single instance
        if (binder != null) {
            throw new AssertionError("Only one Binder instance is allowed");
        }
    
        // Get the native stuff
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
    
    /**
     * Creates a native {@link MethodHandle MethodHandle} for a native function. If name is `null` the handle will
     * require a pointer to be bound or provided as the first argument.
     *
     * @param name The symbol name or null
     * @param descriptor The descriptor of the symbol
     * @return The native {@link MethodHandle MethodHandle}
     */
    public MethodHandle downcall(String name, FunctionDescriptor descriptor) {
        if (name == null) {
            return linker.downcallHandle(descriptor);
        }
        
        return lookup.lookup(name)
            .map((symbol) -> linker.downcallHandle(symbol, descriptor))
            .orElseThrow(() -> new IllegalArgumentException("Failed to find symbol " + name));
    }
    
    /**
     * Creates a native {@link MethodHandle MethodHandle} for a native function. If name is `null` the handle will
     * require a pointer to the bound or provided as the first argument. If result is `null` the function will return
     * `void`.
     *
     * @param name The symbol name or null
     * @param result The result or null for void
     * @param arguments The argument list
     * @return A native {@link MethodHandle MethodHandle}
     */
    public MethodHandle downcall(String name, MemoryLayout result, MemoryLayout... arguments) {
        return downcall(name, result == null ? FunctionDescriptor.ofVoid(arguments) : FunctionDescriptor.of(result, arguments));
    }
    
    /**
     * Creates a {@link MemorySegment MemorySegment} that points to a native callback.
     *
     * @param handle The Java handle to call
     * @param descriptor The descriptor of the callback
     * @param session The {@link MemorySession MemorySession} to use for allocations
     * @return A new segment with a length of 0
     */
    public MemorySegment upcall(MethodHandle handle, FunctionDescriptor descriptor, MemorySession session) {
        return linker.upcallStub(handle, descriptor, session);
    }
}
