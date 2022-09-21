package com.github.libsm64;

import com.github.libsm64.internal.Utils;

import java.lang.foreign.*;
import java.lang.invoke.VarHandle;
import java.util.Objects;

/*
struct SM64Surface
{
    int16_t type;
    int16_t force;
    uint16_t terrain;
    int16_t vertices[3][3];
};
 */
public record SM64Surface(MemorySegment segment) {
    static final MemoryLayout LAYOUT = Utils.struct(
        Utils.s16("type"),
        Utils.s16("force"),
        Utils.u16("terrain"),
        Utils.sequence(Utils.S16, "vertices", 3, 3)
    );
    static final long BYTES = LAYOUT.byteSize();
    
    private static final VarHandle TYPE = Utils.varHandle(LAYOUT, "type");
    private static final VarHandle FORCE = Utils.varHandle(LAYOUT, "force");
    private static final VarHandle TERRAIN = Utils.varHandle(LAYOUT, "terrain");
    private static final long VERTICES_OFFSET = Utils.offset(LAYOUT, "vertices");
    private static final long VERTICES_BYTES = Utils.bytes(LAYOUT, "vertices");
    
    public SM64Surface {
        Utils.nonNull(segment, "segment can't be null");
        Utils.validateSize(segment, BYTES, "segment was too small");
    }
    
    public SM64Surface(SegmentAllocator allocator) {
        this(Objects.requireNonNull(allocator.allocate(LAYOUT)));
    }
    
    SM64Surface(Addressable addressable) {
        this(MemorySegment.ofAddress(
            Utils.nonNull(addressable, "addressable can't be null").address(),
            BYTES,
            MemorySession.global()
        ));
    }
    
    public short type() {
        return (short) TYPE.get(segment);
    }
    
    public short force() {
        return (short) FORCE.get(segment);
    }
    
    public int terrain() {
        return Short.toUnsignedInt((short) TERRAIN.get(segment));
    }
    
    public MemorySegment vertices() {
        return segment.asSlice(VERTICES_OFFSET, VERTICES_BYTES);
    }
    
    public SM64Surface type(short value) {
        TYPE.set(segment, value);
        return this;
    }
    
    public SM64Surface force(short value) {
        FORCE.set(segment, value);
        return this;
    }
    
    public SM64Surface terrain(int value) {
        TERRAIN.set(segment, (short) (value & 0xFFFF));
        return this;
    }
    
    public SM64Surface vertices(MemorySegment value) {
        Utils.nonNull(value, "value can't be null");
        Utils.validateSize(value, VERTICES_BYTES, "value was too small");
        vertices().copyFrom(value);
        return this;
    }
    
    Addressable address() {
        return segment.address();
    }
    
    public static Buffer buffer(long size, SegmentAllocator allocator) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0");
        }
        Objects.requireNonNull(allocator, "allocator can't be null");
        
        return new Buffer(size, allocator);
    }
    
    public static final class Buffer {
        private final long size;
        private final MemorySegment segment;
    
        Buffer(long size, SegmentAllocator allocator) {
            this.size = size;
            segment = allocator.allocate(bytes());
        }
        
        Buffer(long size, MemoryAddress address) {
            this.size = size;
            this.segment = MemorySegment.ofAddress(address, bytes(), MemorySession.global());
        }
        
        public SM64Surface get(int index) {
            Objects.checkIndex(index, size);
            
            return new SM64Surface(segment.asSlice(BYTES * index, BYTES));
        }
        
        public long size() {
            return size;
        }
        
        long bytes() {
            return size * BYTES;
        }
        
        Addressable address() {
            return segment.address();
        }
    }
}
