package de.synyx.metrics.jndi;

import com.codahale.metrics.MetricRegistry;
import org.junit.Test;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

public class MetricObjectFactoryTest {

    @Test
    public void testCreate () throws Exception {
        Object reference = mock (Reference.class);

        assertThat (new MetricObjectFactory ().getObjectInstance (reference, mock (Name.class),
                                                                             mock (Context.class),
                                                                             new Hashtable<> ()),
            instanceOf (MetricRegistry.class)
        );
    }

    @Test
    public void testCreateNoReference () throws Exception {
        assertThat (new MetricObjectFactory ().getObjectInstance (null, mock (Name.class),
                                                                        mock (Context.class),
                                                                        new Hashtable<> ()),
            nullValue ()
        );
    }

}