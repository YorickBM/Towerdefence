package yorickbm.towerdefence.events;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import yorickbm.towerdefence.API.gui.GuiEventRegistry;
import yorickbm.towerdefence.API.gui.GuiItem;
import yorickbm.towerdefence.API.gui.InventoryGui;
import yorickbm.towerdefence.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class InteractBuildingEvent implements Listener {

    private List<InventoryHolder> _activeUpgradeMenus = new ArrayList<>();

    @EventHandler
    public void click(EntityDamageByEntityEvent event) {
        if(event.getEntityType() != EntityType.ARMOR_STAND) return; //Items is not an armorstand
        event.setCancelled(true);

        AtomicBoolean foundTower = new AtomicBoolean(false);
        Core.getInstance().getArenas().get(0).getTowers().forEach(twr -> {
            //System.out.println("Did we click on a tower armorstand?!");
            if(!twr.didYouClickMe(event.getEntity())) return; //Armorstand does not belong to tower!

            InventoryGui upgradeUi = new InventoryGui(twr.getName() + " : Lvl. " + twr.getLevel(), 3) { };

            //TODO Make this configurable!
            Float costs = twr.getUpgradeCosts();
            if(costs >= 0) upgradeUi.addItem(new GuiItem(Material.ANVIL, 10, 1)
                    .setName("Upgrade to Lvl. "  + twr.getLevel() + 1)
                    .setLore("Upgrade costs: " + costs).setOnClick(p -> {
                        p.sendMessage("Upgrading tower to " + twr.getLevel() + 1);
                        p.closeInventory();
                        twr.Upgrade(p);
                    }));
            else upgradeUi.addItem(new GuiItem(Material.DAMAGED_ANVIL, 10, 1)
                    .setName("Max level reached!"));

            upgradeUi.addItem(new GuiItem(Material.CRAFTING_TABLE, 13, 1)
                    .setName(twr.getName())
                    .setLore(twr.getDescription()));

            upgradeUi.addItem(new GuiItem(Material.BARRIER, 16, 1)
                    .setName("Remove tower!").setOnClick(p -> {
                        p.closeInventory();
                        twr.destroy();
                    }));
            GuiEventRegistry.Register(upgradeUi);
            _activeUpgradeMenus.add(upgradeUi.getHolder());

            if(event.getDamager() instanceof Player)
                upgradeUi.forceOpen((Player)event.getDamager());
            foundTower.set(true);

        });

        if(!foundTower.get()) event.setCancelled(false);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) { //TODO prevent this need by using a modifier, draggable then just cast eventholder!
        if(!_activeUpgradeMenus.contains(event.getInventory().getHolder())) return;
        GuiEventRegistry.Destroy(event.getInventory().getHolder());
        _activeUpgradeMenus.remove(event.getInventory().getHolder());
    }

}
