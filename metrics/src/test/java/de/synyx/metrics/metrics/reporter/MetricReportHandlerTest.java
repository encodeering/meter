package de.synyx.metrics.metrics.reporter;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class MetricReportHandlerTest {

    private MetricReportHandler noop = new MetricReportHandlerNoop ();

    @Test
    public void testTimeunit () throws Exception {
        assertThat (noop.timeunit ("ns"), equalTo (TimeUnit.NANOSECONDS));
        assertThat (noop.timeunit ("µs"), equalTo (TimeUnit.MICROSECONDS));
        assertThat (noop.timeunit ("ms"), equalTo (TimeUnit.MILLISECONDS));
        assertThat (noop.timeunit ( "s"), equalTo (TimeUnit.SECONDS));
        assertThat (noop.timeunit ( "m"), equalTo (TimeUnit.MINUTES));
        assertThat (noop.timeunit ( "h"), equalTo (TimeUnit.HOURS));
        assertThat (noop.timeunit ( "d"), equalTo (TimeUnit.DAYS));
    }

    @Test (expected = UnsupportedOperationException.class)
    public void testTimeunitEmpty () throws Exception {
        noop.timeunit ("");
    }

    @Test (expected = UnsupportedOperationException.class)
    public void testTimeunitUnknown () throws Exception {
        noop.timeunit (UUID.randomUUID ().toString ());
    }

    @Test
    public void testPattern () {
        assertThat (noop.rpattern.matcher ("s").matches (),     is (false));
        assertThat (noop.rpattern.matcher ("1s").matches (),    is (true));
        assertThat (noop.rpattern.matcher ("1 s").matches (),   is (true));
        assertThat (noop.rpattern.matcher ("1\ts").matches (),  is (true));

        /* first group must be numeric */
        assertThat (noop.rpattern.matcher ("a a").matches (),   is (false));

        /* second group must be two letters only*/
        assertThat (noop.rpattern.matcher ("1 a").matches (),   is (true));
        assertThat (noop.rpattern.matcher ("1 aa").matches (),  is (true));
        assertThat (noop.rpattern.matcher ("1 aaa").matches (), is (false));
        assertThat (noop.rpattern.matcher ("1 1a").matches (),  is (false));
        assertThat (noop.rpattern.matcher ("1 a1").matches (),  is (false));
    }

    @Test
    public void testRefreshRate () {
        assertThat (noop.rdefault, equalTo ("20s"));
    }

    @Test
    public void testQueryToMap () {
        assertQuery (URI.create ("test://me"),              Collections.<String, String>emptyMap ());
        assertQuery (URI.create ("test://me?"),             Collections.<String, String>emptyMap ());
        assertQuery (URI.create ("test://me?x=123"),        ImmutableMap.of ("x", "123"));
        assertQuery (URI.create ("test://me?x=123&y=234"),  ImmutableMap.of ("x", "123", "y", "234"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testQueryToMapNoDuplicates () {
        noop.parameters (URI.create ("test://me?x=123&y=234&x=345"));
    }

    @Test
    public void testOr () {
        String valid     = UUID.randomUUID ().toString ();
        String anystring = UUID.randomUUID ().toString ();

        assertThat (noop.or (null,  null),      nullValue ());
        assertThat (noop.or (null,  anystring), equalTo (anystring));
        assertThat (noop.or ("",    anystring), equalTo (""));
        assertThat (noop.or (valid, anystring), equalTo (valid));
    }

    private void assertQuery (URI uri, Map<String, String> output) {
        assertThat (noop.parameters (uri), equalTo (output));
    }

}