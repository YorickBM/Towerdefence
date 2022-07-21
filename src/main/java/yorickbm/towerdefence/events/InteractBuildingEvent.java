package yorickbm.towerdefence.events;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import yorickbm.towerdefence.API.gui.GuiEventRegistry;
import yorickbm.towerdefence.API.gui.GuiItem;
import yorickbm.towerdefence.API.gui.InventoryGui;
import yorickbm.towerdefence.TowerDefence;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 */
public class InteractBuildingEvent implements Listener {

    private List<InventoryHolder> _activeUpgradeMenus = new ArrayList<>();

    @EventHandler
    public void onDestroy(BlockBreakEvent event) {
        if(!TowerDefence.getInstance().isPlayerInArena(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void click(EntityDamageByEntityEvent event) {

        //Pre event checks to check if its inside arena requirements.
        if(!(event.getDamager() instanceof Player)) return;
        if(!TowerDefence.getInstance().isPlayerInArena((Player)event.getDamager())) return;
        if(event.getEntityType() != EntityType.ARMOR_STAND) return; //Items is not an armorstand
        event.setCancelled(true);

        //Loop trough all towers in arena and get corresponding stuff
        AtomicBoolean foundTower = new AtomicBoolean(false);
        TowerDefence.getInstance().getArenaForPlayer((Player)event.getDamager()).getTowers().forEach(twr -> {

            if(!twr.didYouClickMe(event.getEntity())) return; //Armorstand does not belong to tower!
            InventoryGui upgradeUi = new InventoryGui(twr.getName() + " : Lvl. " + twr.getLevel(), 3) { };

            //TODO Move to config
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

        //Set cancelled false for the event if its a regular armorstand in the arena
        if(!foundTower.get()) event.setCancelled(false);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) { //TODO prevent this need by using a modifier, draggable then just cast eventholder!
        if(!_activeUpgradeMenus.contains(event.getInventory().getHolder())) return;
        GuiEventRegistry.Destroy(event.getInventory().getHolder());
        _activeUpgradeMenus.remove(event.getInventory().getHolder());
    }

}
