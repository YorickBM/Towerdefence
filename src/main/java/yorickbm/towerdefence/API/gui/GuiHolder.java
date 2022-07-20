package yorickbm.towerdefence.API.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Objects;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 *
 * This class allows for basic backward checks on Gui Inventorys by getting the data from the inventory holder.
 */
public class GuiHolder implements InventoryHolder {

    private String identifier;
    private boolean draggable;

    public GuiHolder(String identifier) {
        this(identifier, false);
    }
    public GuiHolder(String identifier, boolean draggable) {
        this.draggable = false;
        this.identifier = identifier;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public String getHolder() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuiHolder guiHolder = (GuiHolder) o;
        return Objects.equals(identifier, guiHolder.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
