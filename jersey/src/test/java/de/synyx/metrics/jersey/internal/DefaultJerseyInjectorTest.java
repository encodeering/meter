package de.synyx.metrics.jersey.internal;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith (MockitoJUnitRunner.class)
public class DefaultJerseyInjectorTest {

    @Mock
    private ServiceLocator locator;

    @Test
    public void testCreate () throws Exception {
        InputStream stream;

                                                              stream = mock (InputStream.class);
        when (locator.create (InputStream.class)).thenReturn (stream);

        assertThat (new DefaultJerseyInjector (locator).create (InputStream.class), is (stream));
    }

    @Test (expected = MultiException.class)
    public void testCreateUnknown () {
        when (locator.create (InputStream.class)).thenThrow (new MultiException ());

        new DefaultJerseyInjector (locator).create (InputStream.class);
    }

    @Test
    public void testInject () throws Exception {
        InputStream stream;

                                                                stream = mock (InputStream.class);
        assertThat (new DefaultJerseyInjector (locator).inject (stream), is (stream));

        verify (locator).inject (stream);
    }
}