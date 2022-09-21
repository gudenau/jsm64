package com.github.libsm64;

import com.github.libsm64.internal.Binder;
import com.github.libsm64.internal.Utils;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public final class LibSM64 {
    private LibSM64() {
        throw new AssertionError();
    }
    
    public static final int SM64_TEXTURE_WIDTH = 64 * 11;
    public static final int SM64_TEXTURE_HEIGHT = 64;
    public static final int SM64_GEO_MAX_TRIANGLES = 1024;
    
    private static final MethodHandle SM64_GLOBAL_INIT;
    private static final MethodHandle SM64_GLOBAL_TERMINATE;
    private static final MethodHandle SM64_STATIC_SURFACES_LOAD;
    private static final MethodHandle SM64_MARIO_CREATE;
    private static final MethodHandle SM64_MARIO_TICK;
    private static final MethodHandle SM64_MARIO_DELETE;
    private static final MethodHandle SM64_SURFACE_OBJECT_CREATE;
    private static final MethodHandle SM64_SURFACE_OBJECT_MOVE;
    private static final MethodHandle SM64_SURFACE_OBJECT_DELETE;
    
    static final FunctionDescriptor SM64_DEBUG_PRINT_FUNCTION_PTR_DESCRIPTOR = FunctionDescriptor.ofVoid(Utils.ADDRESS);
    static final MethodHandle SM64_DEBUG_PRINT_FUNCTION_PTR_JAVA_HANDLE;
    static final MethodHandle SM64_DEBUG_PRINT_FUNCTION_PTR_NATIVE_HANDLE;
    
    static {
        var binder = Binder.load();
        SM64_GLOBAL_INIT = binder.downcall("sm64_global_init", null, Utils.ADDRESS, Utils.ADDRESS, Utils.ADDRESS);
        SM64_GLOBAL_TERMINATE = binder.downcall("sm64_global_terminate", null, new MemoryLayout[0]);
        SM64_STATIC_SURFACES_LOAD = binder.downcall("sm64_static_surfaces_load", null, Utils.ADDRESS, Utils.U32);
        SM64_MARIO_CREATE = binder.downcall("sm64_mario_create", Utils.S32, Utils.S16, Utils.S16, Utils.S16);
        SM64_MARIO_TICK = binder.downcall("sm64_mario_tick", null, Utils.S32, Utils.ADDRESS, Utils.ADDRESS, Utils.ADDRESS);
        SM64_MARIO_DELETE = binder.downcall("sm64_mario_delete", null, Utils.S32);
        SM64_SURFACE_OBJECT_CREATE = binder.downcall("sm64_surface_object_create", Utils.U32, Utils.ADDRESS);
        SM64_SURFACE_OBJECT_MOVE = binder.downcall("sm64_surface_object_move", null, Utils.U32, Utils.ADDRESS);
        SM64_SURFACE_OBJECT_DELETE = binder.downcall("sm64_surface_object_delete", null, Utils.U32);
    
        // We only have one callback, might as well not make helpers.
        try {
            SM64_DEBUG_PRINT_FUNCTION_PTR_JAVA_HANDLE = MethodHandles.lookup().findVirtual(
                SM64DebugPrintFunctionPtr.class,
                "invoke",
                Linker.upcallType(SM64_DEBUG_PRINT_FUNCTION_PTR_DESCRIPTOR)
            );
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("Failed to find base handle for SM64DebugPrintFunctionPtr", e);
        }
    
        SM64_DEBUG_PRINT_FUNCTION_PTR_NATIVE_HANDLE = binder.downcall(null, FunctionDescriptor.ofVoid(Utils.ADDRESS));
    }
    
    public static void sm64_global_init(Addressable rom, Addressable outTexture, Addressable debugPrintFunction) {
        try {
            SM64_GLOBAL_INIT.invokeExact(rom, outTexture, debugPrintFunction);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to execute sm64_global_init", e);
        }
    }
    
    public static void sm64_global_terminate() {
        try {
            SM64_GLOBAL_TERMINATE.invokeExact();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to execute sm64_global_terminate", e);
        }
    }
    
    public static void sm64_static_surfaces_load(SM64Surface.Buffer surfaceArray) {
        try {
            SM64_STATIC_SURFACES_LOAD.invokeExact(surfaceArray.address(), (int) (surfaceArray.size() & 0x00000000_FFFFFFFFL));
        } catch (Throwable e) {
            throw new RuntimeException("Failed to execute sm64_static_surfaces_load", e);
        }
    }
    
    public static int sm64_mario_create(short x, short y, short z) {
        try {
            return (int) SM64_MARIO_CREATE.invokeExact(x, y, z);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to execute sm64_mario_create", e);
        }
    }
    
    public static void sm64_mario_tick(int marioId, SM64MarioInputs inputs, SM64MarioState outState, SM64MarioGeometryBuffers outBuffers) {
        try {
            SM64_MARIO_TICK.invokeExact(marioId, inputs.address(), outState.address(), outBuffers.address());
        } catch (Throwable e) {
            throw new RuntimeException("Failed to execute sm64_mario_tick", e);
        }
    }
    
    public static void sm64_mario_delete(int marioId) {
        try {
            SM64_MARIO_DELETE.invokeExact(marioId);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to execute sm64_mario_delete", e);
        }
    }
    
    public static long sm64_surface_object_create(SM64SurfaceObject surfaceObject) {
        try {
            return Integer.toUnsignedLong((int) SM64_SURFACE_OBJECT_CREATE.invokeExact(surfaceObject.address()));
        } catch (Throwable e) {
            throw new RuntimeException("Failed to execute sm64_surface_object_create", e);
        }
    }
    
    public static void sm64_surface_object_move(long objectId, SM64ObjectTransform transform) {
        try {
            SM64_SURFACE_OBJECT_MOVE.invokeExact((int) (objectId & 0x00000000_FFFFFFFFL), transform.address());
        } catch (Throwable e) {
            throw new RuntimeException("Failed to execute sm64_surface_object_move", e);
        }
    }
    
    public static void sm64_surface_object_delete(long objectId) {
        try {
            SM64_SURFACE_OBJECT_DELETE.invokeExact((int) (objectId & 0x00000000_FFFFFFFFL));
        } catch (Throwable e) {
            throw new RuntimeException("Failed to execute sm64_surface_object_delete", e);
        }
    }
}
