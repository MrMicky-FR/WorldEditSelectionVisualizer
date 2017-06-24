/***
 * Reflection utilities test to make sure WESV remains cross-version compatible.
 */

package com.rojel.wesv;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.rojel.wesv.Metrics.AbstractPlotter;
import com.rojel.wesv.Metrics.Graph;

import junit.framework.TestCase;

/**
* Tests for the Metrics class.
* @author martinambrus
*
*/
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("org.mockito.*")
@PrepareForTest({ Bukkit.class, Server.class, PluginManager.class, Plugin.class })
public class MetricsTest extends TestCase {

    /**
     * Text representation for any object (AbstractPlotter, Graph...)
     * that should be called "Default".
     */
    private static String enabledItemName = "Enabled";

    /**
     * A mock of the Plugin class.
     */
    private Plugin pluginMock;

    /**
     * A mock of the PluginManager class.
     */
    private PluginManager pManagerMock;

    /**
     * A mock of the Server class.
     */
    private Server serverMock;

    /**
     * An actual instance of the Metrics class to be tested.
     */
    private Metrics met;

    /**
     * A graph that we use to perform plotter operation tests on.
     */
    private Graph graph;

    /**
     * This method will setup the testing with mocks of the Server, Plugin and PluginManager
     * classes, as well as an instance of the Metric class.
     *
     * @throws Exception if any of the classes cannot be mocked or Metrics cannot be instantiated.
     */
    @Override
    @Before
    public void setUp() throws Exception {
        this.pManagerMock = PowerMockito.mock(PluginManager.class);

        this.serverMock = PowerMockito.mock(Server.class);
        PowerMockito.when(this.serverMock.getPluginManager()).thenReturn(this.pManagerMock);

        this.pluginMock = PowerMockito.mock(Plugin.class);
        PowerMockito.when(this.pluginMock.getServer()).thenReturn(this.serverMock);
        PowerMockito.when(this.pluginMock.getDataFolder()).thenReturn(new File("plugin.yml"));

        PowerMockito.mockStatic(Bukkit.class);
        PowerMockito.when(Bukkit.getLogger()).thenReturn(Logger.getLogger("test"));

        // remove the PluginMetrics/config.yml file, so our tests will test its creation as well
        File f = new File("PluginMetrics/config.yml");
        f.delete();

        // also remove the folder
        f = new File("PluginMetrics");
        f.delete();

        this.met = new Metrics(this.pluginMock);
        this.graph = this.met.createGraph("test");
    }

    /**
     * Test that we cannot call the constructor without a valid plugin.
     */
    @Test
    public void testConstructorThrowsWithInvalidPlugin() {
        try {
            new Metrics(null);
            fail("Metrics class constructor with no plugin provided didn't throw an error.");
        } catch (IllegalArgumentException | IOException e) {
        }
    }

    /**
     * Test that the Metrics class initializes correctly with the config.yml file present.
     * Testing without config file would throw exception in the setUp() method itself.
     */
    @Test
    public void testMetricsClassInitSucceedsWithConfig() {
        try {
            new Metrics(this.pluginMock);
        } catch (IllegalArgumentException | IOException e) {
            fail("Metrics class failed to initialize with an existing config file.");
        }
    }

    /**
     * Test that createGraph initialization fails without a name.
     */
    public void testCreateGraphFailsWithoutName() {
        try {
            this.met.createGraph(null);
            fail("Graph constructor didn't throw an error when no graph name was provided.");
        } catch (final IllegalArgumentException e) {
        }
    }

    /**
     * Test that we can create a new graph.
     */
    @Test
    public void testCreateGraph() {
        assertThat("Test graph is not of class Metrics.Graph", this.graph, instanceOf(Metrics.Graph.class));
    }

    /**
     * Test that adding a new graph without a name throws an exception.
     */
    @Test
    public void testAddGraphWithoutName() {
        try {
            this.met.addGraph(null);
            fail("Adding a new graph without a name failed to throw an exception.");
        } catch (final IllegalArgumentException ex) {
        }
    }

    /**
     * Test that we can add a new graph.
     */
    @Test
    public void testAddGraph() {
        try {
            this.met.addGraph(this.graph);
        } catch (final IllegalArgumentException ex) {
            fail("Adding a new graph was unsuccessful.");
        }
    }

    /**
     * Tests that the new graph retains its given name.
     */
    @Test
    public void testGraphName() {
        assertThat("Test graph did not retain its given name.", this.graph.getName(), is("test"));
    }

    /**
     * Tests that we can add a new plotter to a graph.
     */
    @Test
    public void testAddPlotter() {
        final AbstractPlotter aplotter = new Metrics.AbstractPlotter(enabledItemName) {

            @Override
            public int getValue() {
                return 1;
            }
        };

        this.graph.addPlotter(aplotter);
        assertThat("Could not add a plotter to the new graph.", this.graph.getPlotters(), contains(aplotter));
    }

    /**
     * Tests that we can add default plotter to a graph.
     */
    @Test
    public void testAddDefaultPlotter() {
        final AbstractPlotter aplotter = new Metrics.AbstractPlotter() {

            @Override
            public int getValue() {
                return 1;
            }
        };

        this.graph.addPlotter(aplotter);
        assertThat("Could not add a plotter to the new graph.", this.graph.getPlotters(), contains(aplotter));
    }

    /**
     * Tests that we can remove a plotter from the graph.
     */
    @Test
    public void testRemovePlotter() {
        final AbstractPlotter aplotter = new Metrics.AbstractPlotter(enabledItemName) {

            @Override
            public int getValue() {
                return 1;
            }
        };

        this.graph.addPlotter(aplotter);
        this.graph.removePlotter(aplotter);
        assertThat("Could not remove a plotter from the graph.", this.graph.getPlotters(), not(contains(aplotter)));
    }

    /**
     * Tests that we can compare plotters.
     */
    @Test
    public void testComparePlotters() {
        final AbstractPlotter aplotter = new Metrics.AbstractPlotter(enabledItemName) {

            @Override
            public int getValue() {
                return 1;
            }
        };

        assertThat("Same plotters are not equal.", aplotter.equals(aplotter), is(true));
    }

    /**
     * Tests that plotters compare works with plotters that differ.
     */
    @Test
    public void testComparePlottersWithDifferentPlotters() {
        final AbstractPlotter aplotter1 = new Metrics.AbstractPlotter(enabledItemName) {

            @Override
            public int getValue() {
                return 1;
            }
        };

        final AbstractPlotter aplotter2 = new Metrics.AbstractPlotter("Disabled") {

            @Override
            public int getValue() {
                return 1;
            }
        };
        assertThat("Different plotters were calculated as equal.", aplotter1.equals(aplotter2), is(false));
    }

    /**
     * Tests that plotters comparison works with AbstractPlotter against a different (Metrics) class.
     */
    @Test
    public void testComparePlottersWithDifferentClasses() {
        final AbstractPlotter aplotter1 = new Metrics.AbstractPlotter(enabledItemName) {

            @Override
            public int getValue() {
                return 1;
            }
        };

        assertThat("AbstractPlotter and Metrics classes were calculated as equal.", aplotter1.equals(this.met),
                is(false));
    }

    /**
     * Tests that we can compare graphs.
     */
    @Test
    public void testCompareGraphs() {
        assertThat("Same graphs are not equal.", this.graph.equals(this.graph), is(true));
    }

    /**
     * Tests that graphs compare works with graphs that differ.
     */
    @Test
    public void testCompareGraphsWithDifferentGraphs() {
        assertThat("Different graphs were calculated as equal.", this.graph.equals(this.met.createGraph("test2")),
                is(false));
    }

    /**
     * Tests that graphs comparison works with Graph vs Metrics class.
     */
    @Test
    public void testCompareGraphsWithDifferentClass() {
        assertThat("Graph and Metrics classes were calculated as equal.", this.graph.equals(this.met), is(false));
    }

    /**
     * Tests that the opt-out value is false by default.
     */
    @Test
    public void testOptOutDefault() {
        assertThat("Opt-Out value is not false by default.", this.met.isOptOut(), is(false));
    }

    /**
     * Tests that the opt-out method throws when no valid config file was set.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testOptOutThrowsWithoutValidConfigFile() {
        Metrics m = null;
        try {
            // a spy is needed here because getConfigFile() is private in Metrics
            m = PowerMockito.spy(new Metrics(this.pluginMock));
        } catch (final IOException e) {
            fail("Couldn't create new spy for the Metrics class.");
        }

        try {
            // fake the response of the private method getConfigFile()
            PowerMockito.when(m, PowerMockito.method(Metrics.class, "getConfigFile")).withNoArguments()
                    .thenThrow(IOException.class);
        } catch (final IOException ioEx) {
            // the expected exception thrown
        } catch (final Exception e) {
            fail("An unexpected exception (" + e.getClass().getSimpleName()
                    + ") was thrown from Metrics.isOptOut() when getConfigFile() shoud have returned IOException.");
        }

        assertThat("The isOptOut() method of Metrics returned FALSE instead of TRUE without a valid config file.",
                m.isOptOut(), is(true));
    }

    /**
     * Tests that the opt-out method throws when no valid config file was set (debug flag ON).
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testOptOutThrowsWithoutValidConfigFileWithDebugOn() {
        Metrics m = null;
        try {
            m = PowerMockito.spy(new Metrics(this.pluginMock));
            try {
                setFinalStatic(Metrics.class.getDeclaredField("debug"), Boolean.TRUE, m);
            } catch (final SecurityException e) {
                fail("Could not set debug flag to TRUE due to security exception.");
            } catch (final NoSuchFieldException e) {
                fail("Could not set debug flag to TRUE because the field was not found.");
            } catch (final Exception e) {
                fail("Could not set debug flag to TRUE because of an unexpected exception ("
                        + e.getClass().getSimpleName() + ").");
            }
        } catch (final IOException e) {
            fail("Couldn't create new spy for the Metrics class.");
        }

        try {
            PowerMockito.when(m, PowerMockito.method(Metrics.class, "getConfigFile")).withNoArguments()
                    .thenThrow(IOException.class);
        } catch (final IOException ioEx) {
            // the expected exception thrown
        } catch (final Exception e) {
            fail("An unexpected exception (" + e.getClass().getSimpleName()
                    + ") was thrown from Metrics.isOptOut() when getConfigFile() shoud have returned IOException.");
        }

        assertThat("The isOptOut() method of Metrics returned FALSE instead of TRUE without a valid config file.",
                m.isOptOut(), is(true));
    }

    /**
     * Tests that the opt-out method throws for invalid configuration.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testOptOutThrowsWithInvalidConfig() {
        Metrics m = null;
        try {
            // a spy is needed here because getConfigFile() is private in Metrics
            m = PowerMockito.spy(new Metrics(this.pluginMock));
        } catch (final IOException e) {
            fail("Couldn't create new spy for the Metrics class.");
        }

        try {
            // fake the response of the private method getConfigFile()
            PowerMockito.when(m, PowerMockito.method(Metrics.class, "getConfigFile")).withNoArguments()
                    .thenThrow(InvalidConfigurationException.class);
        } catch (final InvalidConfigurationException icEx) {
            // the expected exception thrown
        } catch (final Exception e) {
            fail("An unexpected exception (" + e.getClass().getSimpleName()
                    + ") was thrown from Metrics.isOptOut() when getConfigFile() "
                    + "shoud have returned InvalidConfigurationException.");
        }

        assertThat("The isOptOut() method of Metrics returned FALSE instead of TRUE without a valid config structure.",
                m.isOptOut(), is(true));
    }

    /**
     * Tests that the opt-out method throws for invalid configuration (debug flag ON).
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testOptOutThrowsWithInvalidConfigWithDebugOn() {
        Metrics m = null;
        try {
            m = PowerMockito.spy(new Metrics(this.pluginMock));
            try {
                setFinalStatic(Metrics.class.getDeclaredField("debug"), Boolean.TRUE, m);
            } catch (final SecurityException e) {
                fail("Could not set debug flag to TRUE due to security exception.");
            } catch (final NoSuchFieldException e) {
                fail("Could not set debug flag to TRUE because the field was not found.");
            } catch (final Exception e) {
                fail("Could not set debug flag to TRUE because of an unexpected exception ("
                        + e.getClass().getSimpleName() + ").");
            }
        } catch (final IOException e) {
            fail("Couldn't create new spy for the Metrics class.");
        }

        try {
            PowerMockito.when(m, PowerMockito.method(Metrics.class, "getConfigFile")).withNoArguments()
                    .thenThrow(InvalidConfigurationException.class);
        } catch (final InvalidConfigurationException ecEx) {
            // the expected exception thrown
        } catch (final Exception e) {
            fail("An unexpected exception (" + e.getClass().getSimpleName()
                    + ") was thrown from Metrics.isOptOut() when getConfigFile() "
                    + "should have returned InvalidConfigurationException.");
        }

        assertThat("The isOptOut() method of Metrics returned FALSE instead of TRUE without a valid config structure.",
                m.isOptOut(), is(true));
    }

    /**
     * Removes the final modifier of a a final static field in the given object.
     *
     * @param field Field to remove final modifier for.
     * @param newValue New value to set for the final field.
     * @param targetObject Object on which to perform the adjustment.
     * @throws Exception If there was a problem augmenting the final field, various exceptions may be thrown by Java.
     */
    public static void setFinalStatic(final Field field, final Object newValue, final Object targetObject)
            throws Exception {
        field.setAccessible(true);
        // remove final modifier from field
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(targetObject, newValue);
    }
}