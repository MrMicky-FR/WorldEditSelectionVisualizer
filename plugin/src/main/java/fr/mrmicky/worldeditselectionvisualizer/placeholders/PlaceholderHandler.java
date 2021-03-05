package fr.mrmicky.worldeditselectionvisualizer.placeholders;

import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionType;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderHandler extends PlaceholderExpansion {
    private final WorldEditSelectionVisualizer plugin;

    public PlaceholderHandler(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "wesv";
    }

    @Override
    public String getAuthor() {
        return "Firestone82";
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        if (player == null) {
            return "Loading..";
        }

        String[] args = identifier.split("_");

        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("toggled")) {
                if (args[1].equalsIgnoreCase("selection")) {
                    return ""+ plugin.getPlayerInfos(player).isSelectionVisible(SelectionType.SELECTION);
                }

                if (args[1].equalsIgnoreCase("clipboard")) {
                    return ""+ plugin.getPlayerInfos(player).isSelectionVisible(SelectionType.CLIPBOARD);
                }
            }
        }

        return null;
    }
}
