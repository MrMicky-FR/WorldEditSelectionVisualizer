/***
 * Reflection utilities test to make sure WESV remains cross-version compatible.
 */

package test.java.com.darkblade12.particleeffect;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.comphenix.protocol.metrics.Metrics;

/**
* Tests for the Metrics class.
* @author martinambrus
*
*/
@RunWith(PowerMockRunner.class)
@PrepareForTest(Metrics.class)
public class MetricsTest {

    private Plugin        plugin;
    private PluginManager pManager;
    private Server        server;
    private Metrics       met;

    @Before
    public void setUp() throws Exception {
        this.plugin = PowerMockito.mock(Plugin.class);
        this.pManager = PowerMockito.mock(PluginManager.class);

        this.server = PowerMockito.mock(Server.class);
        PowerMockito.when(this.server.getPluginManager()).thenReturn(this.pManager);

        PowerMockito.when(this.plugin.getServer()).thenReturn(this.server);
        PowerMockito.when(this.plugin.getDataFolder()).thenReturn(new File("plugin.yml"));

        this.met = new Metrics(this.plugin);
    }

    @Test
    public void testGraph() throws Exception {
        final Metrics.Graph graph = this.met.createGraph("test");
        assertThat(this.met.createGraph("test"), instanceOf(Metrics.Graph.class));
        assertThat(graph.getName(), is("test"));
    }
}