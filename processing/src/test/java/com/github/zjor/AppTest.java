package com.github.zjor;

import org.junit.Assert;
import org.junit.Test;

public class AppTest {

    @Test
    public void shouldReturnTopFrequencies() {
        float[] spectrum = new float[]{1.0f, 3.0f, 2.0f};
        Assert.assertTrue(3.0f == App.getTopFrequencies(spectrum).get(0).a);
    }
}
