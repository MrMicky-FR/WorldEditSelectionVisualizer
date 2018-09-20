package com.rojel.wesv;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandWesv implements TabExecutor {

    private WorldEditSelectionVisualizer plugin;

    public CommandWesv(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!sender.hasPermission("wesv.use")) {
            sender.sendMessage(plugin.getCustomConfig().getLangNoPermission());
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload") || !sender.hasPermission("wesv.reloadconfig")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                final boolean isEnabled = !plugin.getCustomConfig().isEnabled(player);
                plugin.getCustomConfig().setEnabled(player, isEnabled);

                if (isEnabled) {
                    player.sendMessage(ChatColor.GREEN + plugin.getCustomConfig().getLangVisualizerEnabled());
                    if (plugin.shouldShowSelection(player)) {
                        plugin.showSelection(player);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + plugin.getCustomConfig().getLangVisualizerDisabled());
                    plugin.hideSelection(player);
                }
            } else {
                sender.sendMessage(plugin.getCustomConfig().getLangPlayersOnly());
            }
        } else {
            plugin.getCustomConfig().reloadConfig();
            sender.sendMessage(plugin.getCustomConfig().getConfigReloaded());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias,
                                      final String[] args) {
        if (args.length == 1 && sender.hasPermission("wesv.reloadconfig")) {
            return StringUtil.copyPartialMatches(args[0], Collections.singletonList("reload"), new ArrayList<>());
        }

        return Collections.emptyList();
    }
}
