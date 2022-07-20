package yorickbm.towerdefence.API.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import yorickbm.towerdefence.API.RandomString;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 */
public abstract class InventoryGui {

    protected final static RandomString indentifierPool = new RandomString();

    protected BiFunction<ItemStack, PlayerInteractEvent, Boolean> rightClick, leftClick;
    protected final Inventory gui;
    protected final GuiHolder identifier;
    protected BiConsumer<GuiItem, Player> interact;

    protected GuiItem[] items;

    public void open(final ItemStack mainHand, final Action action, final PlayerInteractEvent e) {
        if(rightClick != null) if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) if(rightClick.apply(mainHand, e)) renderGui(e.getPlayer());
        if(leftClick != null) if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) if(leftClick.apply(mainHand, e)) renderGui(e.getPlayer());
    }
    public void onInteract(ItemStack clickedItem, int slot, Player player) {
        if(interact != null) interact.accept(items[slot], player);
        else items[slot].interact.accept(player);
    }

    protected void renderGui(Player player) {
        //remove all items
        gui.clear();

        //add items
        for(int i = 0; i < items.length; i++) {
            if(items[i] == null) continue; //No item in slot

            gui.setItem(i, items[i].getItem());
        }

        player.openInventory(gui);
    }

    public void setLeftClick(BiFunction<ItemStack, PlayerInteractEvent, Boolean> onClick) { leftClick = onClick; }
    public void setRightClick(BiFunction<ItemStack, PlayerInteractEvent, Boolean> onClick) { rightClick = onClick; }
    public void setInteraction(BiConsumer<GuiItem, Player> onInteract) { interact = onInteract; }

    public InventoryGui(final String title, final int rows) {
        final int slots = 9*rows;

        items = new GuiItem[slots];

        identifier = new GuiHolder(indentifierPool.nextString());
        gui = Bukkit.createInventory(identifier, slots, title);
        setInteraction((i, p) -> { i.onClick(p);});
    }

    public InventoryHolder getHolder() { return identifier; }

    public void addItem(GuiItem item) {items[item.getSlot()] = item;}
    public void removeItem(GuiItem item) {removeItem(item.getSlot());}
    public void removeItem(int slot) {items[slot] = null;}

    public void forceOpen(Player player) {
        renderGui(player);
    }
}
