package net.nifheim.bukkit.util;

import java.util.UUID;
import net.nifheim.bukkit.util.internal.listener.GUIListener;
import net.nifheim.bukkit.util.menu.BaseMenu;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * @author Beelzebu
 */
public class BukkitCoreUtils {

    public void init(Plugin plugin) {
        CompatUtil.getInstance();
        Bukkit.getPluginManager().registerEvents(new GUIListener(), plugin);
    }

    public void disable() {
        Bukkit.getOnlinePlayers().forEach(p -> {
            UUID inv = BaseMenu.getOpenInventories().get(p.getUniqueId());
            if (inv != null) {
                p.closeInventory();
            }
        });
    }
}
