package net.nifheim.bukkit.util.menu;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.nifheim.bukkit.util.ItemBuilder;
import net.nifheim.bukkit.util.TextSanitizer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Beelzebu
 */
public abstract class BaseMenu {

    private static final Map<UUID, BaseMenu> inventoriesByUUID = new HashMap<>();
    private static final Map<UUID, UUID> openInventories = Collections.synchronizedMap(new HashMap<>());
    private final Inventory inv;
    private final Map<Integer, GUIAction> actions;
    private final UUID uniqueId;
    private ItemStack opener;

    public BaseMenu(int size, String name) {
        this(size, name, null);
    }

    public BaseMenu(int size, String name, InventoryType type) {
        if (size < 9) {
            size *= 9;
        }
        if ((size % 9) > 0) {
            if (size > 53) {
                size -= size - 53;
            } else {
                size += 9 - (size % 9);
            }
        }
        if (type != null && !type.equals(InventoryType.CHEST)) {
            inv = Bukkit.createInventory(null, type, TextSanitizer.inventoryTitle(name));
        } else {
            inv = Bukkit.createInventory(null, size, TextSanitizer.inventoryTitle(name));
        }
        actions = new HashMap<>();
        uniqueId = UUID.randomUUID();
        inventoriesByUUID.put(getUniqueId(), this);
    }

    public static Map<UUID, BaseMenu> getInventoriesByUUID() {
        return BaseMenu.inventoriesByUUID;
    }

    public static Map<UUID, UUID> getOpenInventories() {
        return BaseMenu.openInventories;
    }

    public final void setItem(Item item) {
        setItem(item.getSlot(), item.getItemStack(), item.getGuiAction());
    }

    public final void setItem(int slot, ItemStack is, GUIAction action) {
        inv.setItem(slot, is);
        if (action != null) {
            actions.put(slot, action);
        }
    }

    public final void setItem(int slot, ItemStack is) {
        setItem(slot, is, null);
    }

    public final synchronized void open(Player p) {
        p.closeInventory();
        p.openInventory(inv);
        openInventories.put(p.getUniqueId(), getUniqueId());
    }

    public void delete() {
        Bukkit.getOnlinePlayers().forEach((p) -> {
            UUID u = openInventories.get(p.getUniqueId());
            if (u.equals(getUniqueId())) {
                p.closeInventory();
            }
        });
        inventoriesByUUID.remove(getUniqueId());
    }

    public Item getItem(FileConfiguration config, String path) {
        Material material = Material.STONE;
        String materialPath = config.getString(path + ".Material").toUpperCase();
        if (Material.getMaterial(materialPath.split(":")[0]) != null) {
            material = Material.getMaterial(materialPath.split(":")[0]);
        }
        byte damage = Byte.parseByte(materialPath.split(":")[1]);
        int amount = Integer.parseInt(materialPath.split(":")[2]);
        String name = config.getString(path + ".Name");
        List<String> lore = config.getStringList(path + ".Lore");
        String soundPath = config.getString(path + ".Sound");
        String command = config.getString(path + ".Command");
        return new Item(new ItemBuilder(material, amount, name).damage(damage).lore(lore).build(), config.getInt(path + ".Slot"), player -> {
            if (command != null) {
                player.performCommand(command);
            }
            if (soundPath != null) {
                try {
                    player.playSound(player.getLocation(),
                            Sound.valueOf(soundPath.split(":")[0].replaceAll("\\.", "_").toUpperCase()),
                            Integer.parseInt(soundPath.split(":")[1]),
                            Integer.parseInt(soundPath.split(":")[2])
                    );
                } catch (IllegalArgumentException ignore) { // invalid sound
                }
            }
            if (config.getBoolean(path + ".Close")) {
                player.closeInventory();
            }
        });
    }

    public Inventory getInv() {
        return inv;
    }

    public Map<Integer, GUIAction> getActions() {
        return actions;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public ItemStack getOpener() {
        return opener;
    }

    public void setOpener(ItemStack opener) {
        this.opener = opener;
    }

    public interface GUIAction {

        void click(Player p);
    }

    public static class Item {

        private final ItemStack itemStack;
        private final int slot;
        private final GUIAction guiAction;

        public Item(ItemStack itemStack, int slot, BaseMenu.GUIAction guiAction) {
            this.itemStack = itemStack;
            this.slot = slot;
            this.guiAction = guiAction;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public int getSlot() {
            return slot;
        }

        public BaseMenu.GUIAction getGuiAction() {
            return guiAction;
        }
    }
}