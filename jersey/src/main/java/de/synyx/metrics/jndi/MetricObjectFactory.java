package de.synyx.metrics.jndi;

import com.codahale.metrics.MetricRegistry;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * Date: 01.08.2014
 * Time: 09:09
 */
public final class MetricObjectFactory implements ObjectFactory {

    @Override
    public final Object getObjectInstance (Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        if (! (obj instanceof Reference)) return null;

        return new MetricRegistry ();
    }

}
