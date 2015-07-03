package de.synyx.metrics.metrics.jndi;

import com.codahale.metrics.MetricRegistry;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;

import static org.hamcrest.MatcherAssert.assertThat;

public class MetricObjectFactoryTest {

    @Test
    public void testCreate () throws Exception {
        Object reference = Mockito.mock (Reference.class);

        assertThat (new MetricObjectFactory ().getObjectInstance (reference, Mockito.mock (Name.class),
                                                                             Mockito.mock (Context.class),
                                                                             new Hashtable<> ()),
            Matchers.instanceOf (MetricRegistry.class)
        );
    }

    @Test
    public void testCreateNoReference () throws Exception {
        assertThat (new MetricObjectFactory ().getObjectInstance (null, Mockito.mock (Name.class),
                                                                        Mockito.mock (Context.class),
                                                                        new Hashtable<> ()),
            Matchers.nullValue ()
        );
    }

}