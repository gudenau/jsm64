package com.github.libsm64;

import com.github.libsm64.internal.Utils;

import java.lang.foreign.*;
import java.lang.invoke.VarHandle;
import java.util.Objects;

/*
struct SM64MarioInputs
{
    float camLookX, camLookZ;
    float stickX, stickY;
    uint8_t buttonA, buttonB, buttonZ;
};
 */
public record SM64MarioInputs(MemorySegment segment) {
    static final MemoryLayout LAYOUT = Utils.struct(
        Utils.f32("camLookX"),
        Utils.f32("camLookY"),
        Utils.f32("stickX"),
        Utils.f32("stickY"),
        Utils.u8("buttonA"),
        Utils.u8("buttonB"),
        Utils.u8("buttonZ")
    );
    static final long BYTES = LAYOUT.byteSize();
    
    private static final VarHandle CAM_LOOK_X = Utils.varHandle(LAYOUT, "camLookX");
    private static final VarHandle CAM_LOOK_Y = Utils.varHandle(LAYOUT, "camLookY");
    private static final VarHandle STICK_X = Utils.varHandle(LAYOUT, "stickX");
    private static final VarHandle STICK_Y = Utils.varHandle(LAYOUT, "stickY");
    private static final VarHandle BUTTON_A = Utils.varHandle(LAYOUT, "buttonA");
    private static final VarHandle BUTTON_B = Utils.varHandle(LAYOUT, "buttonB");
    private static final VarHandle BUTTON_Z = Utils.varHandle(LAYOUT, "buttonZ");
    
    public SM64MarioInputs {
        Utils.nonNull(segment, "segment can't be null");
        Utils.validateSize(segment, BYTES, "segment was too small");
    }
    
    public SM64MarioInputs(SegmentAllocator allocator) {
        this(Objects.requireNonNull(allocator.allocate(LAYOUT)));
    }
    
    SM64MarioInputs(Addressable addressable) {
        this(MemorySegment.ofAddress(
            Utils.nonNull(addressable, "addressable can't be null").address(),
            BYTES,
            MemorySession.global()
        ));
    }
    
    public float camLookX() {
        return (float) CAM_LOOK_X.get(segment);
    }
    
    public float camLookY() {
        return (float) CAM_LOOK_Y.get(segment);
    }
    
    public float stickX() {
        return (float) STICK_X.get(segment);
    }
    
    public float stickY() {
        return (float) STICK_Y.get(segment);
    }
    
    public boolean buttonA() {
        return (byte) BUTTON_A.get(segment) != 0;
    }
    
    public boolean buttonB() {
        return (byte) BUTTON_B.get(segment) != 0;
    }
    
    public boolean buttonZ() {
        return (byte) BUTTON_Z.get(segment) != 0;
    }
    
    public SM64MarioInputs camLookX(float value) {
        CAM_LOOK_X.set(segment, value);
        return this;
    }
    
    public SM64MarioInputs camLookY(float value) {
        CAM_LOOK_Y.set(segment, value);
        return this;
    }
    
    public SM64MarioInputs stickX(float value) {
        STICK_X.set(segment, value);
        return this;
    }
    
    public SM64MarioInputs stickY(float value) {
        STICK_Y.set(segment, value);
        return this;
    }
    
    public SM64MarioInputs buttonA(boolean value) {
        BUTTON_A.set(segment, (byte) (value ? 1 : 0));
        return this;
    }
    
    public SM64MarioInputs buttonB(boolean value) {
        BUTTON_B.set(segment, (byte) (value ? 1 : 0));
        return this;
    }
    
    public SM64MarioInputs buttonZ(boolean value) {
        BUTTON_Z.set(segment, (byte) (value ? 1 : 0));
        return this;
    }
    
    Addressable address() {
        return segment.address();
    }
}
