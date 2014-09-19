package de.synyx.meter.core.aspect;

import de.synyx.meter.core.Measure;

/**
* Date: 30.07.2014
* Time: 13:46
*/
public class TestMeasure implements Measure {

    private final long value;

    public TestMeasure (long value) {
        this.value = value;
    }

    @Override
    public long determine (Object response, Throwable throwable) {
        return value;
    }

}
