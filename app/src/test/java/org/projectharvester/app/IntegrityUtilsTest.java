package org.projectharvester.app;

import org.junit.Test;
import java.io.ByteArrayInputStream;
import static org.junit.Assert.*;

public class IntegrityUtilsTest {
    @Test
    public void calculatesKnownSha256Digest() throws Exception {
        String digest = IntegrityUtils.sha256(new ByteArrayInputStream("abc".getBytes("UTF-8")));
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", digest);
    }
}
