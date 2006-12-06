package org.seasar;

import junit.framework.TestCase;

public class AppTest extends TestCase {

    public void testHelloWorld() throws Exception {
        assertEquals("Hello, world!", new App().helloWorld());
    }
}
