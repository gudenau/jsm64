package com.github.libsm64;

import com.github.libsm64.internal.Binder;
import com.github.libsm64.internal.Utils;

import java.lang.foreign.Addressable;
import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.invoke.MethodHandle;
import java.util.Objects;

@FunctionalInterface
public interface SM64DebugPrintFunctionPtr {
    default void invoke(MemoryAddress message) {
        invoke(message.equals(MemoryAddress.NULL) ? null : message.getUtf8String(0));
    }
    
    void invoke(String message);
    
    static MemorySegment allocate(SM64DebugPrintFunctionPtr callback, MemorySession session) {
        Objects.requireNonNull(callback, "callback can't be null");
        return Binder.load().upcall(
            LibSM64.SM64_DEBUG_PRINT_FUNCTION_PTR_JAVA_HANDLE.bindTo(callback),
            LibSM64.SM64_DEBUG_PRINT_FUNCTION_PTR_DESCRIPTOR,
            session
        );
    }
    
    static SM64DebugPrintFunctionPtr ofAddress(Addressable addressable) {
        Utils.nonNull(addressable, "addressable can't be null");
        return new SM64DebugPrintFunctionPtr() {
            private final MethodHandle handle = LibSM64.SM64_DEBUG_PRINT_FUNCTION_PTR_NATIVE_HANDLE.bindTo(addressable);
            
            @Override
            public void invoke(MemoryAddress message) {
                try {
                    handle.invokeExact((Addressable) message);
                } catch (Throwable e) {
                    throw new RuntimeException("Failed to execute SM64DebugPrintFunctionPtr", e);
                }
            }
    
            @Override
            public void invoke(String message) {
                try (var session = MemorySession.openConfined()) {
                    invoke(message == null ? MemoryAddress.NULL : session.allocateUtf8String(message).address());
                }
            }
        };
    }
}
