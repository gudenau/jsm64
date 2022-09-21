package com.github.libsm64.internal;

import java.lang.foreign.*;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;

public final class Utils {
    public static MemoryLayout struct(MemoryLayout... members) {
        var memberList = new ArrayList<MemoryLayout>();
    
        long currentAlignment = 0;
        long size = 0;
        for (var member : members) {
            var alignment = member.byteAlignment();
            if (currentAlignment < alignment) {
                long padding = size & (alignment - 1);
                if (padding != 0) {
                    size += padding;
                    memberList.add(MemoryLayout.paddingLayout(padding << 3));
                }
            }
            currentAlignment = alignment;
            
            memberList.add(member);
            size += member.byteSize();
        }
        
        return MemoryLayout.structLayout(memberList.toArray(MemoryLayout[]::new));
    }
    
    public static final MemoryLayout U8 = ValueLayout.JAVA_BYTE;
    public static MemoryLayout u8(String name) {
        return U8.withName(name);
    }
    
    public static final MemoryLayout S16 = ValueLayout.JAVA_SHORT;
    public static MemoryLayout s16(String name) {
        return S16.withName(name);
    }
    
    public static final MemoryLayout U16 = ValueLayout.JAVA_SHORT;
    public static MemoryLayout u16(String name) {
        return U16.withName(name);
    }
    
    public static final MemoryLayout S32 = ValueLayout.JAVA_INT;
    public static MemoryLayout s32(String name) {
        return S32.withName(name);
    }
    
    public static final MemoryLayout U32 = ValueLayout.JAVA_INT;
    public static MemoryLayout u32(String name) {
        return U32.withName(name);
    }
    
    public static final MemoryLayout F32 = ValueLayout.JAVA_FLOAT;
    public static MemoryLayout f32(String name) {
        return F32.withName(name);
    }
    
    public static final MemoryLayout ADDRESS = ValueLayout.ADDRESS;
    public static MemoryLayout address(String name) {
        return ADDRESS.withName(name);
    }
    
    public static VarHandle varHandle(MemoryLayout layout, String name) {
        return layout.varHandle(MemoryLayout.PathElement.groupElement(name));
    }
    
    public static MemoryLayout sequence(MemoryLayout layout, String name, int size) {
        return MemoryLayout.sequenceLayout(size, layout).withName(name);
    }
    
    public static long offset(MemoryLayout layout, String name) {
        return layout.byteOffset(MemoryLayout.PathElement.groupElement(name));
    }
    
    public static long bytes(MemoryLayout layout, String name) {
        return layout.select(MemoryLayout.PathElement.groupElement(name)).byteSize();
    }
    
    public static MemoryLayout sequence(MemoryLayout layout, String name, int... dimensions) {
        var current = layout;
        for (var dimension : dimensions) {
            current = MemoryLayout.sequenceLayout(dimension, current);
        }
        return current.withName(name);
    }
    
    public static <T extends Addressable> T nonNull(T addressable, String message) {
        if (addressable == null || MemoryAddress.NULL.equals(addressable.address())) {
            throw new NullPointerException(message);
        }
        return addressable;
    }
    
    public static void validateSize(MemorySegment segment, long size, String message) {
        if (segment.byteSize() < size) {
            throw new IllegalArgumentException(message + ", expected " + size + " and got " + segment.byteSize());
        }
    }
}
