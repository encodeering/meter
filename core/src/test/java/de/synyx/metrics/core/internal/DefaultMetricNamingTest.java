package de.synyx.metrics.core.internal;

import de.synyx.metrics.core.MetricNaming;
import de.synyx.metrics.core.Substitution;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultMetricNamingTest {

    @Test
    public void testPattern () {
        assertThat (DefaultMetricNaming.Vars.matcher ("").find (),          is (false));
        assertThat (DefaultMetricNaming.Vars.matcher (  "a").find (),       is (false));
        assertThat (DefaultMetricNaming.Vars.matcher ( "{}").find (),       is (false));
        assertThat (DefaultMetricNaming.Vars.matcher ( "{a}").find (),      is (true));
        assertThat (DefaultMetricNaming.Vars.matcher ("x{a}").find (),      is (true));
        assertThat (DefaultMetricNaming.Vars.matcher ( "{a}y").find (),     is (true));
        assertThat (DefaultMetricNaming.Vars.matcher ("x{a}y").find (),     is (true));
        assertThat (DefaultMetricNaming.Vars.matcher ("x{a}y{b}z").find (), is (true));
    }

    @Test
    public void testName () {
        String any   = UUID.randomUUID ().toString ();
        String wayne = UUID.randomUUID ().toString ();

        Substitution substitution;

              substitution = mock (Substitution.class);
        when (substitution.substitute ("yyy")).thenReturn (any);
        when (substitution.substitute ("wayne")).thenReturn (wayne);

        MetricNaming naming = new DefaultMetricNaming (substitution);

        assertThat (naming.name ("abc"),          equalTo ("abc"));

        assertThat (naming.name ("xxx{yyy}"),     equalTo ("xxx" + any));
        assertThat (naming.name ("xxx{yyy}zzz"),  equalTo ("xxx" + any + "zzz"));
        assertThat (naming.name (    "{yyy}zzz"), equalTo (        any + "zzz"));

        assertThat (naming.name ("{yyy}{wayne}"), equalTo (any + wayne));
    }

    @Test
    public void testNameException () {
        String varname = UUID.randomUUID ().toString ();

        Substitution substitution;

              substitution = mock (Substitution.class);
        when (substitution.substitute (varname)).thenThrow (new RuntimeException ());

        MetricNaming naming = new DefaultMetricNaming (substitution);

        assertThat (naming.name ("{" + varname + "}"), equalTo ("$"));
    }

}