package yorickbm.towerdefence;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import yorickbm.towerdefence.arena.Arena;
import yorickbm.towerdefence.commands.JoinArenaCommand;
import yorickbm.towerdefence.commands.StartArenaCommand;
import yorickbm.towerdefence.commands.StopArenaCommand;
import yorickbm.towerdefence.configuration.ConfigManager;
import yorickbm.towerdefence.gui.API.GuiEventRegistry;
import yorickbm.towerdefence.gui.API.InventoryGui;
import yorickbm.towerdefence.gui.API.events.OpenGUIEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/**
 * Last modified by: YorickBM on 27-06-2022
 */
public final class Core extends JavaPlugin {

    private static Core instance = null;
    public static Core getInstance() { return instance; }

    private ConfigManager _scfgm = null;
    public ConfigManager getConfigManager() { return _scfgm; }

    private List<Arena> _arenas = new ArrayList<>();

    @Override
    public void onEnable() { // Plugin startup logic
        //Initiat basic things
        instance = this;
        _scfgm = new ConfigManager().Initialize("arenas.yml");

        //Load commands
        getCommand("startArena").setExecutor(new StartArenaCommand());
        getCommand("joinArena").setExecutor(new JoinArenaCommand());
        getCommand("stopArena").setExecutor(new StopArenaCommand());

        //Load arenas
        loadArenas();
        getLogger().log(Level.INFO, String.format("Loaded (%d) arenas!!", _arenas.size()));

        //Load towers (reflection...)
        getLogger().log(Level.INFO, String.format("Loaded (%d) towers!!", 0));

        //Load GUIS
        InventoryGui builderGui = new InventoryGui("Buildings", 3) { };
        builderGui.setInteraction((item, player) -> {
            item.setName("You seem to have clicked on me?!");
            item.onClick(player);
        });
        builderGui.setRightClick((item, event) -> {
            if(item.getType() != Material.COMPASS) return false; //Not this guis open item!
            event.setCancelled(true); //Cancel any interaction!!! That could interrupt the GUI

            return true; //Open the inventory!
        });
        GuiEventRegistry.Register(builderGui);

        getLogger().log(Level.INFO, String.format("Loaded (%d) guis!!", GuiEventRegistry.GetAll().size()));

        //Register events
        getServer().getPluginManager().registerEvents(new OpenGUIEvent(), this);

        //Log that we started up!
        getLogger().log(Level.INFO, String.format("You can start building your towers :D", _arenas.size()));
    }

    @Override
    public void onDisable() { // Plugin shutdown logic
        //Remove all mobs & buildings
        for (Arena arena: _arenas ) arena.clean();

        _scfgm.Save();
    }

    public void loadArenas() {
        List<String> arenasFromConfig = _scfgm.GetData().getStringList("arenas");
        for(String arenaString : arenasFromConfig) {
            _arenas.add(new Arena().fromString(arenaString));
        }
    }

    public List<Arena> getArenas() { return  _arenas; }
}


//    // Create a File object on the root of the directory containing the class file
//    File file = new File("c:\\myclasses\\");
//
//try {
//        // Convert File to a URL
//        URL url = file.toURI().toURL();          // file:/c:/myclasses/
//        URL[] urls = new URL[]{url};
//
//        // Create a new class loader with the directory
//        ClassLoader cl = new URLClassLoader(urls);
//
//        // Load in the class; MyClass.class should be located in
//        // the directory file:/c:/myclasses/com/mycompany
//        Class cls = cl.loadClass("com.mycompany.MyClass");
//        } catch (MalformedURLException e) {
//        } catch (ClassNotFoundException e) {
//        }
