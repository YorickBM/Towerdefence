package yorickbm.towerdefence.gui.API;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.function.Consumer;

public class GuiItem {

    final ItemStack item;
    final int slot;
    Consumer<Player> interact;

    public GuiItem(final Material material, final int slot, final int amount) {
        this.slot = slot;
        this.item  = new ItemStack(material, amount);

        interact = (p) -> {};
    }

    public void setName(final String name) {
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
    }

    public void setLore(final String... lore) {
        final ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
    }

    public void onClick(Player player) { interact.accept(player); }
    public void setOnClick(Consumer<Player> onClick) { interact = onClick; }

}
