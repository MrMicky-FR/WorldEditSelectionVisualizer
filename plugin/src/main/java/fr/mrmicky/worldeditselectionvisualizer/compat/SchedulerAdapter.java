package fr.mrmicky.worldeditselectionvisualizer.compat;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.TimeUnit;

@NullMarked
public interface SchedulerAdapter {

    static SchedulerAdapter create(Plugin plugin) {
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");

            return new FoliaSchedulerAdapter(plugin);
        } catch (ClassNotFoundException e) {
            return new BukkitSchedulerAdapter(plugin);
        }
    }

    void execute(Runnable task);

    ScheduledTask scheduleGlobalRepeating(Runnable task, long delay, long period);

    ScheduledTask scheduleAsyncRepeating(Runnable task, long delay, long period);

    void executeAsync(Runnable task);

    void teleport(Player player, Location location);

    String name();

    class BukkitSchedulerAdapter implements SchedulerAdapter {

        private final Plugin plugin;

        public BukkitSchedulerAdapter(Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void execute(Runnable task) {
            this.plugin.getServer().getScheduler().runTask(this.plugin, task);
        }

        @Override
        public ScheduledTask scheduleGlobalRepeating(Runnable task, long delay, long period) {
            return this.plugin.getServer().getScheduler()
                    .runTaskTimer(this.plugin, task, delay, period)::cancel;
        }

        @Override
        public ScheduledTask scheduleAsyncRepeating(Runnable task, long delay, long period) {
            return this.plugin.getServer().getScheduler()
                    .runTaskTimerAsynchronously(this.plugin, task, delay, period)::cancel;
        }

        @Override
        public void executeAsync(Runnable task) {
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, task);
        }

        @Override
        public void teleport(Player player, Location location) {
            player.teleport(location);
        }

        @Override
        public String name() {
            return "Bukkit";
        }
    }

    class FoliaSchedulerAdapter implements SchedulerAdapter {

        private final Plugin plugin;

        public FoliaSchedulerAdapter(Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void execute(Runnable task) {
            this.plugin.getServer().getGlobalRegionScheduler().run(this.plugin, t -> task.run());
        }

        @Override
        public ScheduledTask scheduleGlobalRepeating(Runnable task, long delay, long period) {
            return this.plugin.getServer().getGlobalRegionScheduler()
                    .runAtFixedRate(this.plugin, t -> task.run(), delay, period)::cancel;
        }

        @Override
        public ScheduledTask scheduleAsyncRepeating(Runnable task, long delay, long period) {
            return this.plugin.getServer().getAsyncScheduler()
                    .runAtFixedRate(this.plugin, t -> task.run(), delay * 50, period * 50, TimeUnit.MILLISECONDS)::cancel;
        }

        @Override
        public void executeAsync(Runnable task) {
            this.plugin.getServer().getAsyncScheduler().runNow(this.plugin, t -> task.run());
        }

        @Override
        public void teleport(Player player, Location location) {
            player.teleportAsync(location);
        }

        @Override
        public String name() {
            return "Folia";
        }
    }
}
