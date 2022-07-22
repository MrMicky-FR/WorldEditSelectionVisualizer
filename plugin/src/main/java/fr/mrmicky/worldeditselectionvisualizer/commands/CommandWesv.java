package fr.mrmicky.worldeditselectionvisualizer.commands;

import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.selection.PlayerVisualizerData;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionType;
import fr.mrmicky.worldeditselectionvisualizer.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandWesv implements TabExecutor {

    private final WorldEditSelectionVisualizer plugin;

    public CommandWesv(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {
        if (!sender.hasPermission("wesv.use")) {
            sender.sendMessage(this.plugin.getMessage("no-permissions"));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("wesv.reload")) {
            this.plugin.reloadConfig();

            sender.sendMessage(this.plugin.getMessage("config-reloaded"));
            return true;
        }

        if (args[0].equalsIgnoreCase("toggle")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            handleToggle((Player) sender, args);

            return true;
        }

        if (args[0].equalsIgnoreCase("lock")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            handleLock((Player) sender, args);

            return true;
        }

        sendUsage(sender);

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      @NotNull String[] args) {
        if (args.length > 2 || !sender.hasPermission("wesv.use")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();

            if (sender instanceof Player) {
                completions.add("toggle");
                completions.add("lock");
            }

            if (sender.hasPermission("wesv.reload")) {
                completions.add("reload");
            }

            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("toggle")
                && StringUtil.startsWithIgnoreCase("clipboard", args[1])) {
            return Collections.singletonList("clipboard");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("lock")
                && StringUtil.startsWithIgnoreCase("tp", args[1])) {
            return Collections.singletonList("tp");
        }

        return Collections.emptyList();
    }

    private void handleToggle(Player player, String[] args) {
        PlayerVisualizerData playerData = this.plugin.getPlayerData(player);
        SelectionType type = SelectionType.SELECTION;

        if (args.length > 1 && args[1].equalsIgnoreCase("clipboard")) {
            type = SelectionType.CLIPBOARD;
        }

        boolean enabled = !playerData.isSelectionVisible(type);
        String key = enabled ? "enabled" : "disabled";
        playerData.toggleSelectionVisibility(type, enabled);

        if (type == SelectionType.SELECTION) {
            player.sendMessage(this.plugin.getMessage("visualizer-" + key));
        } else {
            player.sendMessage(this.plugin.getMessage("visualizer-clipboard-" + key));
        }

        if (this.plugin.getConfig().getBoolean("save-toggle")) {
            this.plugin.getStorageManager().setEnable(player, type, enabled);
        }
    }

    private void handleLock(Player player, String[] args) {
        PlayerVisualizerData playerData = this.plugin.getPlayerData(player);
        Location location = playerData.getClipboardLockLocation();

        if (args.length > 1 && args[1].equalsIgnoreCase("tp")) {
            if (location == null) {
                player.sendMessage(this.plugin.getMessage("lock-no-position"));
                return;
            }

            player.teleport(location);
            return;
        }

        if (location != null) {
            playerData.setClipboardLockLocation(null);
            player.sendMessage(this.plugin.getMessage("lock-disabled"));
            return;
        }

        playerData.setClipboardLockLocation(player.getLocation());

        player.sendMessage(this.plugin.getMessage("lock-enabled"));
    }

    private void sendUsage(CommandSender sender) {
        String version = this.plugin.getDescription().getVersion();
        sender.sendMessage(ChatUtils.color("&6WorldEditSelectionVisualizer v" + version + "&7 by &6MrMicky&7."));

        if (sender instanceof Player) {
            sender.sendMessage(ChatUtils.color("&7- /wesv lock"));
            sender.sendMessage(ChatUtils.color("&7- /wesv lock tp"));
            sender.sendMessage(ChatUtils.color("&7- /wesv toggle"));
            sender.sendMessage(ChatUtils.color("&7- /wesv toggle clipboard"));
        }

        if (sender.hasPermission("wesv.reload")) {
            sender.sendMessage(ChatUtils.color("&7- /wesv reload"));
        }
    }
}
