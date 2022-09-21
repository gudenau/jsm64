/* Very basic test code.
package com.github.libsm64.internal;

import com.github.libsm64.*;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Test {
    public static void main(String[] args) throws Throwable {
        try (var session = MemorySession.openConfined()) {
            var rom = loadFile(session, Path.of("SuperMario64.z64"));
            if(!"17CE077343C6133F8C9F2D6D6D9A4AB62C8CD2AA57C40AEA1F490B4C8BB21D91".equals(hash(rom))) {
                throw new RuntimeException("ROM hash mismatch, is it the US z64 SM64 ROM?");
            }
            var texture = session.allocate(LibSM64.SM64_TEXTURE_WIDTH * LibSM64.SM64_TEXTURE_HEIGHT * 4, Long.BYTES);
            LibSM64.sm64_global_init(
                rom, texture,
                SM64DebugPrintFunctionPtr.allocate(System.out::println, session)
            );
            
            try {
                /*
                These use full package names just so we don't need the desktop requirement.
                try (var stream = Files.newOutputStream(Path.of("texture.png"))){
                    var pixels = new int[LibSM64.SM64_TEXTURE_WIDTH * LibSM64.SM64_TEXTURE_HEIGHT];
                    texture.asByteBuffer()
                        .order(java.nio.ByteOrder.BIG_ENDIAN)
                        .asIntBuffer()
                        .get(0, pixels);
                    
                    for (int i = 0; i < pixels.length; i++) {
                        var pixel = pixels[i];
                        pixels[i] = (pixel >>> 8) | (pixel << 24);
                    }
                    
                    var image = new java.awt.image.BufferedImage(LibSM64.SM64_TEXTURE_WIDTH, LibSM64.SM64_TEXTURE_HEIGHT, java.awt.image.BufferedImage.TYPE_4BYTE_ABGR);
                    image.setRGB(
                        0, 0,
                        LibSM64.SM64_TEXTURE_WIDTH, LibSM64.SM64_TEXTURE_HEIGHT,
                        pixels, 0,
                        LibSM64.SM64_TEXTURE_WIDTH
                    );
                    javax.imageio.ImageIO.write(image, "PNG", stream);
                }
                 * /
            } finally {
                LibSM64.sm64_global_terminate();
            }
        }
    }
    
    private static MemorySegment loadFile(MemorySession session, Path path) throws IOException {
        try (var channel = Files.newByteChannel(path, StandardOpenOption.READ)) {
            var segment = session.allocate(channel.size(), Long.BYTES);
            var buffer = segment.asByteBuffer();
            while (channel.size() > 0 && buffer.hasRemaining()) {
                channel.read(buffer);
            }
            return segment.asReadOnly();
        }
    }
    
    private static String hash(MemorySegment segment) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is mandatory, if it's missing there are larger problems.
            throw new AssertionError();
        }
        
        digest.update(segment.asByteBuffer());
        var hash = digest.digest();
        StringBuilder builder = new StringBuilder();
        for (var datum : hash) {
            builder.append("%02X".formatted(datum));
        }
        return builder.toString();
    }
}
*/
