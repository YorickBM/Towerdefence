package yorickbm.towerdefence.API.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 */
public class GuiItem {

    private final ItemStack item;
    private final int slot;
    Consumer<Player> interact;

    private String name;
    private List<String> lore;

    public GuiItem(final Material material, final int slot, final int amount) {
        this.slot = slot;
        this.item  = new ItemStack(material, amount);

        interact = (p) -> {};
    }

    public GuiItem setName(final String name) {
        this.name = name;
        return this;
    }
    private void applyName(final String name) {
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);

    }

    public GuiItem setLore(final String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }
    public GuiItem applyLore(final List<String> lore) {
        final ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);

        return this;
    }

    public void onClick(Player player) { interact.accept(player); }
    public GuiItem setOnClick(Consumer<Player> onClick) { interact = onClick;
        return this;
    }

    public ItemStack getItem() {
        applyName(name);
        applyLore(lore);

        return item;
    }

    public int getSlot() {
        return slot;
    }
}
