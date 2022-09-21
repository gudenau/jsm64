package com.github.libsm64;

import com.github.libsm64.internal.Utils;

import java.lang.foreign.*;
import java.lang.invoke.VarHandle;
import java.util.Objects;

/*
struct SM64MarioState
{
    float position[3];
    float velocity[3];
    float faceAngle;
    int16_t health;
};
 */
public record SM64MarioState(MemorySegment segment) {
    static final MemoryLayout LAYOUT = Utils.struct(
        Utils.sequence(Utils.F32, "position", 3),
        Utils.sequence(Utils.F32, "velocity", 3),
        Utils.f32("faceAngle"),
        Utils.s16("health")
    );
    static final long BYTES = LAYOUT.byteSize();
    
    private static final long POSITION_OFFSET = Utils.offset(LAYOUT, "position");
    private static final long POSITION_BYTES = Utils.bytes(LAYOUT, "position");
    private static final long VELOCITY_OFFSET = Utils.offset(LAYOUT, "velocity");
    private static final long VELOCITY_BYTES = Utils.bytes(LAYOUT, "velocity");
    private static final VarHandle FACE_ANGLE = Utils.varHandle(LAYOUT, "faceAngle");
    private static final VarHandle HEALTH = Utils.varHandle(LAYOUT, "health");
    
    public SM64MarioState {
        Utils.nonNull(segment, "segment can't be null");
        Utils.validateSize(segment, BYTES, "segment was too small");
    }
    
    public SM64MarioState(SegmentAllocator allocator) {
        this(Objects.requireNonNull(allocator.allocate(LAYOUT)));
    }
    
    SM64MarioState(Addressable addressable) {
        this(MemorySegment.ofAddress(
            Utils.nonNull(addressable, "addressable can't be null").address(),
            BYTES,
            MemorySession.global()
        ));
    }
    
    public MemorySegment position() {
        return segment.asSlice(POSITION_OFFSET, POSITION_BYTES);
    }
    
    public MemorySegment velocity() {
        return segment.asSlice(VELOCITY_OFFSET, VELOCITY_BYTES);
    }
    
    public float faceAngle() {
        return (float) FACE_ANGLE.get(segment);
    }
    
    public short health() {
        return (short) HEALTH.get(segment);
    }
    
    public SM64MarioState position(MemorySegment value) {
        Utils.nonNull(value, "value can't be null");
        Utils.validateSize(value, POSITION_BYTES, "value was too small");
        position().copyFrom(value);
        return this;
    }
    
    public SM64MarioState velocity(MemorySegment value) {
        Utils.nonNull(value, "value can't be null");
        Utils.validateSize(value, VELOCITY_BYTES, "value was too small");
        velocity().copyFrom(value);
        return this;
    }
    
    public SM64MarioState faceAngle(float value) {
        FACE_ANGLE.set(segment, value);
        return this;
    }
    
    public SM64MarioState health(short value) {
        HEALTH.set(segment, value);
        return this;
    }
    
    Addressable address() {
        return segment.address();
    }
}
