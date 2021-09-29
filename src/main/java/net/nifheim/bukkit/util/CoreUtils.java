package net.nifheim.bukkit.util;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Beelzebu
 */
public class CoreUtils extends JavaPlugin {

    @Override
    public void onEnable() {
        new BukkitCoreUtils().init(this);
    }
}
