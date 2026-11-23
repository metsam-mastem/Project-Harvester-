package org.projectharvester.app;

import org.junit.Test;
import static org.junit.Assert.*;

public class PreviewGateTest {
    @Test
    public void newerPreviewInvalidatesOlderResponse() {
        PreviewGate gate = new PreviewGate();
        int first = gate.next();
        int second = gate.next();
        assertFalse(gate.isCurrent(first));
        assertTrue(gate.isCurrent(second));
    }

    @Test
    public void editingUrlInvalidatesPendingPreview() {
        PreviewGate gate = new PreviewGate();
        int request = gate.next();
        gate.invalidate();
        assertFalse(gate.isCurrent(request));
    }
}
