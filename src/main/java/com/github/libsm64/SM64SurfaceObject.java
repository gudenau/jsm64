package com.github.libsm64;

import com.github.libsm64.internal.Utils;

import java.lang.foreign.*;
import java.lang.invoke.VarHandle;
import java.util.Objects;

/*
struct SM64SurfaceObject
{
    struct SM64ObjectTransform transform;
    uint32_t surfaceCount;
    struct SM64Surface *surfaces;
};
 */
public record SM64SurfaceObject(MemorySegment segment) {
    static final MemoryLayout LAYOUT = Utils.struct(
        SM64ObjectTransform.LAYOUT.withName("transform"),
        Utils.u32("surfaceCount"),
        Utils.address("surfaces")
    );
    static final long BYTES = LAYOUT.byteSize();
    
    private static final long TRANSFORM_OFFSET = Utils.offset(LAYOUT, "transform");
    private static final long TRANSFORM_BYTES = Utils.bytes(LAYOUT, "transform");
    private static final VarHandle SURFACE_COUNT = Utils.varHandle(LAYOUT, "surfaceCount");
    private static final VarHandle SURFACES = Utils.varHandle(LAYOUT, "surfaces");
    
    public SM64SurfaceObject {
        Utils.nonNull(segment, "segment can't be null");
        Utils.validateSize(segment, BYTES, "segment was too small");
    }
    
    public SM64SurfaceObject(SegmentAllocator allocator) {
        this(Objects.requireNonNull(allocator.allocate(LAYOUT)));
    }
    
    SM64SurfaceObject(Addressable addressable) {
        this(MemorySegment.ofAddress(
            Utils.nonNull(addressable, "addressable can't be null").address(),
            BYTES,
            MemorySession.global()
        ));
    }
    
    public SM64ObjectTransform transform() {
        return new SM64ObjectTransform(segment.asSlice(TRANSFORM_OFFSET, TRANSFORM_BYTES));
    }
    
    public long surfaceCount() {
        return Integer.toUnsignedLong((int) SURFACE_COUNT.get(segment));
    }
    
    public SM64Surface.Buffer surfaces() {
        return new SM64Surface.Buffer((int) surfaceCount(), (SegmentAllocator) SURFACES.get(segment));
    }
    
    public SM64SurfaceObject transform(SM64ObjectTransform value) {
        segment.asSlice(TRANSFORM_OFFSET, TRANSFORM_BYTES).copyFrom(value.segment());
        return this;
    }
    
    public SM64SurfaceObject surfaceCount(long value) {
        SURFACE_COUNT.set(segment, (int) (value & 0x00000000_FFFFFFFFL));
        return this;
    }
    
    public SM64SurfaceObject surfaces(SM64Surface.Buffer value) {
        SURFACES.set(segment, value.address());
        return surfaceCount(value.size());
    }
    
    Addressable address() {
        return segment.address();
    }
}
