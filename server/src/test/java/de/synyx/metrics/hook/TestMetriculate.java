package de.synyx.metrics.hook;

import de.synyx.metrics.Metriculate;

/**
* Date: 30.07.2014
* Time: 13:46
*/
public class TestMetriculate implements Metriculate {

    private final long value;

    public TestMetriculate (long value) {
        this.value = value;
    }

    @Override
    public long determine (Object response, Throwable throwable) {
        return value;
    }

}
