package org.projectharvester.app;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Streaming integrity helpers; file contents are never loaded fully into memory. */
public final class IntegrityUtils {
    private IntegrityUtils() { }

    public static String sha256(InputStream input) throws IOException {
        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable on this platform.", exception);
        }

        byte[] buffer = new byte[64 * 1024];
        int read;
        while ((read = input.read(buffer)) != -1) {
            digest.update(buffer, 0, read);
        }

        byte[] hash = digest.digest();
        StringBuilder result = new StringBuilder(hash.length * 2);
        for (byte value : hash) result.append(String.format(java.util.Locale.ROOT, "%02x", value & 0xff));
        return result.toString();
    }
}
