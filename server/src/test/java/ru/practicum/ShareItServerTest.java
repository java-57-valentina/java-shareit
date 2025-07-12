package ru.practicum;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ShareItServerTest extends TestCase {
    public ShareItServerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ShareItServerTest.class);
    }

    public void testApp() {
        assertTrue(true);
    }
}