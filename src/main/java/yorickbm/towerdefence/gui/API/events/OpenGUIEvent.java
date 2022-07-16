package yorickbm.towerdefence.gui.API.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import yorickbm.towerdefence.gui.API.GuiEventRegistry;
import yorickbm.towerdefence.gui.API.InventoryGui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OpenGUIEvent implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Action action = event.getAction(); // Instance of action
        final Player player = event.getPlayer(); // Instance of player.
        final ItemStack itemClickedWith = player.getInventory().getItemInMainHand();

        GuiEventRegistry.GetAll().forEach(gui -> gui.open(itemClickedWith, action, event));
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!GuiEventRegistry.GetAll().stream().map(inv -> inv.getHolder()).collect(Collectors.toList()).contains(e.getInventory().getHolder())) return;

        e.setCancelled(true);
        final ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;
        final Player player = (Player) e.getWhoClicked();
        final int slot = e.getRawSlot();

        //Get inventory from Library and execute callback trigger of clicking on item slot
        Optional<InventoryGui> activeGui = GuiEventRegistry.GetAll().stream()
                .filter(gui -> gui.getHolder().equals(e.getClickedInventory().getHolder())).findFirst();
        if(activeGui.isEmpty()) return; //Not inside of inventory! (Expected: Clicks item in his own inventory)

        activeGui.get().onInteract(clickedItem, slot, player);
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (!GuiEventRegistry.GetAll().stream().map(inv -> inv.getHolder()).collect(Collectors.toList()).contains(e.getInventory().getHolder())) return;

        e.setCancelled(true);
    }

}
