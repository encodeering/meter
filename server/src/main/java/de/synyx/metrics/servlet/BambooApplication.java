package de.synyx.metrics.servlet;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

public class BambooApplication extends Application {

    @Override
    public Set<Object> getSingletons () {
        Set<Object> singletons = new HashSet<> ();
                    singletons.add (new BambooBinder ());

        return singletons;
    }

    @Override
    public Set<Class<?>> getClasses () {
        Set<Class<?>> classes = new HashSet<> ();
                      classes.add (Bamboo.class);

        return classes;
    }

}
