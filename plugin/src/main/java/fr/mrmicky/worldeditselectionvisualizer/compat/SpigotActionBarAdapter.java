package fr.mrmicky.worldeditselectionvisualizer.compat;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

final class SpigotActionBarAdapter {

    private SpigotActionBarAdapter() {
        throw new UnsupportedOperationException();
    }

    static void checkSupported() throws NoSuchMethodException {
        Player.Spigot.class.getMethod("sendMessage", ChatMessageType.class, BaseComponent.class);
    }

    static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
}
