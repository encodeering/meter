package de.synyx.metrics.jersey.internal;

import de.synyx.metrics.core.Substitution;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DefaultJerseySubstitutionTest extends JerseyTest {

    @Override
    protected Application configure () {
        ResourceConfig config = new ResourceConfig ();

        config.register (Testlet.class);
        config.register (new AbstractBinder () {

            @Override
            protected void configure () {
                bind (DefaultJerseySubstitution.class).to (Substitution.class).in (RequestScoped.class);
            }

        });

        return config;
    }

    @Test
    public void testSubstituteMissing () {
        assertThat (target ("test").path ("xx").request ().get (String.class), equalTo ("xx"));
    }

    @Test
    public void testSubstitutePath () {
        String anypath   = UUID.randomUUID ().toString ();

        assertThat (target ("test").path ("pp").path (anypath).request ().get (String.class), equalTo (anypath));
    }

    @Test
    public void testSubstituteQuery () {
        String anyquery  = UUID.randomUUID ().toString ();

        assertThat (target ("test").path ("qp").request ().get (String.class),                             equalTo (""));
        assertThat (target ("test").path ("qp").queryParam ("qp", anyquery).request ().get (String.class), equalTo (anyquery));

        assertThat (target ("test").path ("qpdefault").request ().get (String.class),                             equalTo ("w00t"));
        assertThat (target ("test").path ("qpdefault").queryParam ("qp", anyquery).request ().get (String.class), equalTo (anyquery));
    }

    @Test
    public void testSubstituteHeader () {
        String anyheader  = UUID.randomUUID ().toString ();

        assertThat (target ("test").path ("hp").request ().get (String.class),                          equalTo (""));
        assertThat (target ("test").path ("hp").request ().header ("hp", anyheader).get (String.class), equalTo (anyheader));

        assertThat (target ("test").path ("hpdefault").request ().get (String.class),                          equalTo ("w00t"));
        assertThat (target ("test").path ("hpdefault").request ().header ("hp", anyheader).get (String.class), equalTo (anyheader));
    }

    @Test
    public void testSubstituteCookie () {
        String anycookie  = UUID.randomUUID ().toString ();

        assertThat (target ("test").path ("cp").request ().get (String.class),                          equalTo (""));
        assertThat (target ("test").path ("cp").request ().cookie ("cp", anycookie).get (String.class), equalTo (anycookie));

        assertThat (target ("test").path ("cpdefault").request ().get (String.class),                          equalTo ("w00t"));
        assertThat (target ("test").path ("cpdefault").request ().cookie ("cp", anycookie).get (String.class), equalTo (anycookie));
    }

    @Test
    public void testSubstituteSubResource () {
        String anypath    = UUID.randomUUID ().toString ();
        String anyquery   = UUID.randomUUID ().toString ();
        String anyheader  = UUID.randomUUID ().toString ();
        String anycookie  = UUID.randomUUID ().toString ();

        assertThat (target ("test").path ("sr").path (anypath).request ().get (String.class),                                                                               equalTo (anypath));
        assertThat (target ("test").path ("sr").path (anypath).queryParam ("qp", anyquery).request ().get (String.class),                                                   equalTo (anypath + anyquery));
        assertThat (target ("test").path ("sr").path (anypath).queryParam ("qp", anyquery).request ().header ("hp", anyheader).get (String.class),                          equalTo (anypath + anyquery + anyheader));
        assertThat (target ("test").path ("sr").path (anypath).queryParam ("qp", anyquery).request ().header ("hp", anyheader).cookie ("cp", anycookie).get (String.class), equalTo (anypath + anyquery + anyheader + anycookie));
    }

    @Path ("test")
    public static class Testlet {

        @Inject
        private Substitution substitution;

        @GET
        @Path ("xx")
        public String xx () {
            return substitution.substitute ("xx");
        }

        @GET
        @Path ("pp/{pp}")
        public String pp (@PathParam ("pp") String pathparam) {
            return substitution.substitute ("pp");
        }

        @GET
        @Path ("qp")
        public String qp (@QueryParam ("qp") String queryparam) {
            return substitution.substitute ("qp");
        }

        @GET
        @Path ("qpdefault")
        public String qpdefault (@DefaultValue ("w00t") @QueryParam ("qp") String queryparam) {
            return substitution.substitute ("qp");
        }

        @GET
        @Path ("cp")
        public String cp (@CookieParam ("cp") String cookieparam) {
            return substitution.substitute ("cp");
        }

        @GET
        @Path ("cpdefault")
        public String cpdefault (@DefaultValue ("w00t") @CookieParam ("cp") String cookieparam) {
            return substitution.substitute ("cp");
        }

        @GET
        @Path ("hp")
        public String hp (@HeaderParam ("hp") String headerparam) {
            return substitution.substitute ("hp");
        }

        @GET
        @Path ("hpdefault")
        public String hpdefault (@DefaultValue ("w00t") @HeaderParam ("hp") String headerparam) {
            return substitution.substitute ("hp");
        }

        @Path ("sr/{pp}")
        public SubRes sr (@PathParam ("pp") String pathparam, @QueryParam ("qp") String queryparam) {
            return new SubRes (substitution);
        }

        public static class SubRes {

            private final Substitution substitution;

            public SubRes (Substitution substitution) {
                this.substitution = substitution;
            }

            @GET
            public String sr (@CookieParam ("cp") String cookieparam, @HeaderParam ("hp") String headerparam) {
                return substitution.substitute ("pp") + substitution.substitute ("qp") + substitution.substitute ("hp") + substitution.substitute ("cp");
            }

        }

    }

}