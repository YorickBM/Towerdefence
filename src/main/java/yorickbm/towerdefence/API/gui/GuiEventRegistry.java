package yorickbm.towerdefence.API.gui;

import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GuiEventRegistry {

    private static List<InventoryGui> _guis = new ArrayList<>();

    public static void ClearRegistry() { _guis.clear(); }
    public static void Register(InventoryGui gui) { _guis.add(gui); }
    public static void Destroy(InventoryGui gui) { _guis.remove(gui); }
    public static void Destroy(InventoryHolder holder) {
        Optional<InventoryGui> res = _guis.stream().filter(ig -> ig.getHolder().equals(holder)).findFirst();
        if(res.isPresent()) Destroy(res.get());
    }
    public static List<InventoryGui> GetAll() { return _guis; }

}
