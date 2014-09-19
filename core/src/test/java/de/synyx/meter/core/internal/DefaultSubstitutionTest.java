package de.synyx.meter.core.internal;

import de.synyx.meter.core.Substitution;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultSubstitutionTest {

    @Test
    public void testPattern () {
        assertThat (DefaultSubstitution.Vars.matcher ("").find (),          is (false));
        assertThat (DefaultSubstitution.Vars.matcher (  "a").find (),       is (false));
        assertThat (DefaultSubstitution.Vars.matcher ( "{}").find (),       is (false));
        assertThat (DefaultSubstitution.Vars.matcher ( "{a}").find (),      is (true));
        assertThat (DefaultSubstitution.Vars.matcher ("x{a}").find (),      is (true));
        assertThat (DefaultSubstitution.Vars.matcher ( "{a}y").find (),     is (true));
        assertThat (DefaultSubstitution.Vars.matcher ("x{a}y").find (),     is (true));
        assertThat (DefaultSubstitution.Vars.matcher ("x{a}y{b}z").find (), is (true));
    }

    @Test
    public void testName () {
        String any   = UUID.randomUUID ().toString ();
        String wayne = UUID.randomUUID ().toString ();

        Substitution substitution;

              substitution = mock (Substitution.class);
        when (substitution.substitute ("yyy")).thenReturn (any);
        when (substitution.substitute ("wayne")).thenReturn (wayne);

        Substitution naming = new DefaultSubstitution (substitution);

        assertThat (naming.substitute (null),           equalTo (null));
        assertThat (naming.substitute (""),             equalTo (""));

        assertThat (naming.substitute ("abc"),          equalTo ("abc"));

        assertThat (naming.substitute ("xxx{yyy}"),     equalTo ("xxx" + any));
        assertThat (naming.substitute ("xxx{yyy}zzz"),  equalTo ("xxx" + any + "zzz"));
        assertThat (naming.substitute ("{yyy}zzz"), equalTo (        any + "zzz"));

        assertThat (naming.substitute ("{yyy}{wayne}"), equalTo (any + wayne));
    }

    @Test
    public void testNameException () {
        String varname = UUID.randomUUID ().toString ();

        Substitution substitution;

              substitution = mock (Substitution.class);
        when (substitution.substitute (varname)).thenThrow (new RuntimeException ());

        Substitution naming = new DefaultSubstitution (substitution);

        assertThat (naming.substitute ("{" + varname + "}"), equalTo ("$"));
    }

}