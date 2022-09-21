package com.github.libsm64;

import com.github.libsm64.internal.Utils;

import java.lang.foreign.*;
import java.lang.invoke.VarHandle;
import java.util.Objects;

/*
struct SM64MarioGeometryBuffers
{
    float *position;
    float *normal;
    float *color;
    float *uv;
    uint16_t numTrianglesUsed;
};
 */
public record SM64MarioGeometryBuffers(MemorySegment segment) {
    static final MemoryLayout LAYOUT = Utils.struct(
        Utils.address("position"),
        Utils.address("normal"),
        Utils.address("color"),
        Utils.address("uv"),
        Utils.u16("numTrianglesUsed")
    );
    static final long BYTES = LAYOUT.byteSize();
    
    private static final VarHandle POSITION = Utils.varHandle(LAYOUT, "position");
    private static final VarHandle NORMAL = Utils.varHandle(LAYOUT, "normal");
    private static final VarHandle COLOR = Utils.varHandle(LAYOUT, "color");
    private static final VarHandle UV = Utils.varHandle(LAYOUT, "uv");
    private static final VarHandle NUM_TRIANGLES_USED = Utils.varHandle(LAYOUT, "numTrianglesUsed");
    
    public SM64MarioGeometryBuffers {
        Utils.nonNull(segment, "segment can't be null");
        Utils.validateSize(segment, BYTES, "segment was too small");
    }
    
    public SM64MarioGeometryBuffers(SegmentAllocator allocator) {
        this(Objects.requireNonNull(allocator.allocate(LAYOUT)));
    }
    
    SM64MarioGeometryBuffers(Addressable addressable) {
        this(MemorySegment.ofAddress(
            Utils.nonNull(addressable, "addressable can't be null").address(),
            BYTES,
            MemorySession.global()
        ));
    }
    
    public MemoryAddress position() {
        return (MemoryAddress) POSITION.get(segment);
    }
    
    public MemoryAddress normal() {
        return (MemoryAddress) NORMAL.get(segment);
    }
    
    public MemoryAddress color() {
        return (MemoryAddress) COLOR.get(segment);
    }
    
    public MemoryAddress uv() {
        return (MemoryAddress) UV.get(segment);
    }
    
    public int numTrianglesUsed() {
        return Short.toUnsignedInt((short) NUM_TRIANGLES_USED.get());
    }
    
    public SM64MarioGeometryBuffers position(Addressable value) {
        POSITION.set(segment, value);
        return this;
    }
    
    public SM64MarioGeometryBuffers normal(Addressable value) {
        NORMAL.set(segment, value);
        return this;
    }
    
    public SM64MarioGeometryBuffers color(Addressable value) {
        COLOR.set(segment, value);
        return this;
    }
    
    public SM64MarioGeometryBuffers uv(Addressable value) {
        UV.set(segment, value);
        return this;
    }
    
    public SM64MarioGeometryBuffers numTrianglesUsed(int value) {
        NUM_TRIANGLES_USED.set(segment, (short) (value & 0x0000_FFFF));
        return this;
    }
    
    Addressable address() {
        return segment.address();
    }
}
