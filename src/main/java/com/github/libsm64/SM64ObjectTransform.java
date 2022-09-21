package com.github.libsm64;

import com.github.libsm64.internal.Utils;

import java.lang.foreign.*;
import java.util.Objects;

/*
struct SM64ObjectTransform
{
    float position[3];
    float eulerRotation[3];
};
 */
public record SM64ObjectTransform(MemorySegment segment) {
    final static MemoryLayout LAYOUT = Utils.struct(
        Utils.sequence(Utils.F32, "position", 3),
        Utils.sequence(Utils.F32, "eulerRotation", 3)
    );
    final static long BYTES = LAYOUT.byteSize();
    
    private static final long POSITION_OFFSET = Utils.offset(LAYOUT, "position");
    private static final long POSITION_BYTES = Utils.bytes(LAYOUT, "position");
    private static final long EULER_ROTATION_OFFSET = Utils.offset(LAYOUT, "eulerRotation");
    private static final long EULER_ROTATION_BYTES = Utils.bytes(LAYOUT, "eulerRotation");
    
    public SM64ObjectTransform {
        Utils.nonNull(segment, "segment can't be null");
        Utils.validateSize(segment, BYTES, "segment was too small");
    }
    
    public SM64ObjectTransform(SegmentAllocator allocator) {
        this(Objects.requireNonNull(allocator.allocate(LAYOUT)));
    }
    
    SM64ObjectTransform(Addressable addressable) {
        this(MemorySegment.ofAddress(
            Utils.nonNull(addressable, "addressable can't be null").address(),
            BYTES,
            MemorySession.global()
        ));
    }
    
    public MemorySegment position() {
        return segment.asSlice(POSITION_OFFSET, POSITION_BYTES);
    }
    
    public MemorySegment eulerRotation() {
        return segment.asSlice(EULER_ROTATION_OFFSET, EULER_ROTATION_BYTES);
    }
    
    public SM64ObjectTransform position(MemorySegment value) {
        Utils.nonNull(value, "value can't be null");
        Utils.validateSize(value, POSITION_BYTES, "value was too small");
        position().copyFrom(value);
        return this;
    }
    
    public SM64ObjectTransform eulerRotation(MemorySegment value) {
        Utils.nonNull(value, "value can't be null");
        Utils.validateSize(value, EULER_ROTATION_BYTES, "value was too small");
        eulerRotation().copyFrom(value);
        return this;
    }
    
    Addressable address() {
        return segment.address();
    }
}
