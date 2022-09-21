package com.github.libsm64.internal;

import java.lang.foreign.*;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;

/**
 * Some utilities.
 */
public final class Utils {
    /**
     * Creates a new {@link MemoryLayout MemoryLayout} from the provided members. Ensures that the elements are
     * correctly aligned. Providing alignment members is not required.
     *
     * @param members The members of the new {@link MemoryLayout MemoryLayout}
     * @return The new {@link MemoryLayout MemoryLayout}
     */
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
    
    /**
     * An unsigned byte.
     */
    public static final MemoryLayout U8 = ValueLayout.JAVA_BYTE;
    
    /**
     * Returns an unsigned byte with the provided name.
     *
     * @param name The name of the byte
     * @return The {@link MemoryLayout MemoryLayout} unsigned byte layout
     */
    public static MemoryLayout u8(String name) {
        return U8.withName(name);
    }
    
    /**
     * A signed short.
     */
    public static final MemoryLayout S16 = ValueLayout.JAVA_SHORT;
    
    /**
     * Returns a signed short with the provided name.
     *
     * @param name The name of the short
     * @return The {@link MemoryLayout MemoryLayout} signed short layout
     */
    public static MemoryLayout s16(String name) {
        return S16.withName(name);
    }
    
    /**
     * An unsigned short.
     */
    public static final MemoryLayout U16 = ValueLayout.JAVA_SHORT;
    
    /**
     * Returns an unsigned short with the provided name.
     *
     * @param name The name of the short
     * @return The {@link MemoryLayout MemoryLayout} unsigned short layout
     */
    public static MemoryLayout u16(String name) {
        return U16.withName(name);
    }
    
    /**
     * A signed int.
     */
    public static final MemoryLayout S32 = ValueLayout.JAVA_INT;
    
    /**
     * An unsigned int.
     */
    public static final MemoryLayout U32 = ValueLayout.JAVA_INT;
    
    /**
     * Returns an unsigned int with the provided name.
     *
     * @param name The name of the int
     * @return The {@link MemoryLayout MemoryLayout} unsigned int layout
     */
    public static MemoryLayout u32(String name) {
        return U32.withName(name);
    }
    
    /**
     * A float.
     */
    public static final MemoryLayout F32 = ValueLayout.JAVA_FLOAT;
    
    /**
     * Returns a float with the provided name.
     *
     * @param name The name of the float
     * @return The {@link MemoryLayout MemoryLayout} float layout
     */
    public static MemoryLayout f32(String name) {
        return F32.withName(name);
    }
    
    /**
     * An address.
     */
    public static final MemoryLayout ADDRESS = ValueLayout.ADDRESS;
    
    /**
     * Returns an address with the provided name.
     *
     * @param name The name of the address
     * @return The {@link MemoryLayout MemoryLayout} address layout
     */
    public static MemoryLayout address(String name) {
        return ADDRESS.withName(name);
    }
    
    /**
     * Gets the {@link VarHandle VarHandle} for a member in a {@link MemoryLayout MemoryLayout}.
     *
     * @param layout The layout that contains the member
     * @param name The name of the member
     * @return The {@link VarHandle VarHandle}
     */
    public static VarHandle varHandle(MemoryLayout layout, String name) {
        return layout.varHandle(MemoryLayout.PathElement.groupElement(name));
    }
    
    /**
     * Creates a sequence layout for the provided {@link MemoryLayout MemoryLayout}.
     *
     * @param layout The base {@link MemoryLayout MemoryLayout}
     * @param name The name of the new {@link MemoryLayout MemoryLayout}
     * @param count The element count
     * @return The new {@link MemoryLayout MemoryLayout}
     */
    public static MemoryLayout sequence(MemoryLayout layout, String name, int count) {
        return MemoryLayout.sequenceLayout(count, layout).withName(name);
    }
    
    /**
     * Gets the byte offset into a layout of a named member.
     *
     * @param layout The {@link MemoryLayout MemoryLayout} with the member
     * @param name The name of the member
     * @return The offset in bytes into the {@link MemoryLayout MemoryLayout}
     */
    public static long offset(MemoryLayout layout, String name) {
        return layout.byteOffset(MemoryLayout.PathElement.groupElement(name));
    }
    
    /**
     * Gets the byte size of a named member.
     *
     * @param layout The {@link MemoryLayout MemoryLayout} with the member
     * @param name The name of the member
     * @return The size in bytes of the member
     */
    public static long bytes(MemoryLayout layout, String name) {
        return layout.select(MemoryLayout.PathElement.groupElement(name)).byteSize();
    }
    
    /**
     * Creates a sequence layout for the provided {@link MemoryLayout MemoryLayout}.
     *
     * @param layout The base {@link MemoryLayout MemoryLayout}
     * @param name The name of the new {@link MemoryLayout MemoryLayout}
     * @param dimensions The element counts
     * @return The new {@link MemoryLayout MemoryLayout}
     */
    public static MemoryLayout sequence(MemoryLayout layout, String name, int... dimensions) {
        var current = layout;
        for (var dimension : dimensions) {
            current = MemoryLayout.sequenceLayout(dimension, current);
        }
        return current.withName(name);
    }
    
    /**
     * Throws a {@link NullPointerException NPE} if the addressable instance is null or the address it points to is
     * null.
     *
     * @param addressable The address to check
     * @param message The message of the exception
     * @return The provided object
     * @param <T> The type of the addressable object
     * @throws NullPointerException If the object was null or it points to null
     */
    public static <T extends Addressable> T nonNull(T addressable, String message) {
        if (addressable == null || MemoryAddress.NULL.equals(addressable.address())) {
            throw new NullPointerException(message);
        }
        return addressable;
    }
    
    /**
     * Ensures a memory segment is at least size bytes large.
     *
     * @param segment The segment to check
     * @param size The required segment size
     * @param message The message of the exception
     * @throws IllegalArgumentException if the segment was too small
     */
    public static void validateSize(MemorySegment segment, long size, String message) {
        if (segment.byteSize() < size) {
            throw new IllegalArgumentException(message + ", expected " + size + " and got " + segment.byteSize());
        }
    }
}
