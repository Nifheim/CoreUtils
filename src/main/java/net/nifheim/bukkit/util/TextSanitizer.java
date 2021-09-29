package net.nifheim.bukkit.util;

import net.md_5.bungee.api.ChatColor;

/**
 * @author Beelzebu
 */
public final class TextSanitizer {

    public static String inventoryTitle(String input) {
        return ChatColor.translateAlternateColorCodes('&', input).replaceAll("[^a-zA-Z0-9&_¿?()/\\- ]", "");
    }
}
