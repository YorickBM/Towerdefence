package yorickbm.towerdefence.API.gui;

import java.util.ArrayList;
import java.util.List;

public class GuiEventRegistry {

    private static List<InventoryGui> _guis = new ArrayList<>();

    public static void ClearRegistry() { _guis.clear(); }
    public static void Register(InventoryGui gui) { _guis.add(gui); }
    public static void Destroy(InventoryGui gui) { _guis.remove(gui); }
    public static List<InventoryGui> GetAll() { return _guis; }

}
