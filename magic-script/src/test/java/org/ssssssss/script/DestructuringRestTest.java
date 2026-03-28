package org.ssssssss.script;

import org.junit.Assert;
import org.junit.Test;

public class DestructuringRestTest extends BaseTest {

    @Test
    public void testDestructuringRest() {
        Assert.assertEquals(true, execute("grammar/destructuring_rest.ms"));
    }
}