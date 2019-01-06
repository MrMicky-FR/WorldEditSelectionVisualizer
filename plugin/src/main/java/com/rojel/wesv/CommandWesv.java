package com.rojel.wesv;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandWesv implements TabExecutor {

    private WorldEditSelectionVisualizer plugin;

    static final String RELOAD_CONFIG = "wesv.reloadconfig";
    static final String WORLDEDIT_SELECTION = "worldedit.selection.*";

    public CommandWesv(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!sender.hasPermission("wesv.use")) {
            sender.sendMessage(plugin.getCustomConfig().getLangNoPermission());
            return true;
        }

        if (args.length == 0) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                final boolean isEnabled = !plugin.getStorageManager().isEnabled(player);
                plugin.getStorageManager().setEnable(player, isEnabled);

                if (isEnabled) {
                    player.sendMessage(ChatColor.GREEN + plugin.getCustomConfig().getLangVisualizerEnabled());
                    if (plugin.shouldShowSelection(player)) {
                        plugin.showSelection(player);
                        plugin.showClipboard(player);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + plugin.getCustomConfig().getLangVisualizerDisabled());
                    plugin.hideSelection(player);
                    plugin.hideClipboard(player);
                }
            } else {
                sender.sendMessage(plugin.getCustomConfig().getLangPlayersOnly());
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission(RELOAD_CONFIG)) {
            plugin.getCustomConfig().reloadConfig(true);
            sender.sendMessage(plugin.getCustomConfig().getConfigReloaded());
        } else if (args.length == 1 && sender.hasPermission(WORLDEDIT_SELECTION) && args[0].equalsIgnoreCase("showForAllPlayers")) {
            if(sender instanceof Player) {
                final Player player = (Player) sender;

                plugin.getCustomConfig().setShowForAllPlayersEnabled(!plugin.getCustomConfig().isShowForAllPlayersEnabled());
                player.sendMessage((plugin.getCustomConfig().isShowForAllPlayersEnabled() ? ChatColor.GREEN : ChatColor.RED)
                        + "showForAllPlayers "
                        + (plugin.getCustomConfig().isShowForAllPlayersEnabled() ? "enabled" : "disabled"));
            } else {
                sender.sendMessage(plugin.getCustomConfig().getLangPlayersOnly());
            }
        } else if (args.length == 2) {
            if(sender instanceof Player) {
                final Player player = (Player) sender;

                if (args[0].equalsIgnoreCase("showForAllPlayers") && sender.hasPermission(WORLDEDIT_SELECTION)) {
                        if (args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("true")) {
                            plugin.getCustomConfig().setShowForAllPlayersEnabled(true);
                            player.sendMessage(ChatColor.GREEN  + "showForAllPlayers enabled");
                        } else if (args[1].equalsIgnoreCase("disable") || args[1].equalsIgnoreCase("false")) {
                            plugin.getCustomConfig().setShowForAllPlayersEnabled(false);
                            player.sendMessage(ChatColor.RED + "showForAllPlayers disabled");
                        }
                }
            } else {
                sender.sendMessage(plugin.getCustomConfig().getLangPlayersOnly());
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 1) {
            ArrayList<String> arrayList = new ArrayList<>();
            if (sender.hasPermission(RELOAD_CONFIG)) {
                arrayList.add("reload");
            }
            if (sender.hasPermission(WORLDEDIT_SELECTION)) {
                arrayList.add("showForAllPlayers");
            }
            return arrayList;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("showForAllPlayers")) {
            ArrayList<String> arrayList = new ArrayList<>();
            if (sender.hasPermission(WORLDEDIT_SELECTION)) {
                arrayList.add("enable");
                arrayList.add("disable");
            }
            return arrayList;
        }

        return Collections.emptyList();
    }
}
