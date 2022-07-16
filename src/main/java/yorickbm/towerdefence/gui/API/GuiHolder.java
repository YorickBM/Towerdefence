package yorickbm.towerdefence.gui.API;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Objects;

public class GuiHolder implements InventoryHolder {

    private String identifier;

    public GuiHolder(String identifier) {
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
