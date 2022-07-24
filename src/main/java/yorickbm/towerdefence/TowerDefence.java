package yorickbm.towerdefence;

import org.bstats.bukkit.Metrics;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import yorickbm.towerdefence.API.Exceptions.PlayerNotInArenaException;
import yorickbm.towerdefence.API.JsonConfig;
import yorickbm.towerdefence.API.TDLocation;
import yorickbm.towerdefence.API.gui.GuiEventRegistry;
import yorickbm.towerdefence.API.gui.GuiItem;
import yorickbm.towerdefence.API.gui.InventoryGui;
import yorickbm.towerdefence.API.gui.events.OpenGUIEvent;
import yorickbm.towerdefence.Mobs.ArenaMob;
import yorickbm.towerdefence.arena.Arena;
import yorickbm.towerdefence.commands.CreateTowerCommand;
import yorickbm.towerdefence.commands.JoinArenaCommand;
import yorickbm.towerdefence.commands.StartArenaCommand;
import yorickbm.towerdefence.commands.StopArenaCommand;
import yorickbm.towerdefence.configuration.ConfigManager;
import yorickbm.towerdefence.events.EntityDeathEventListener;
import yorickbm.towerdefence.events.InteractBuildingEvent;
import yorickbm.towerdefence.events.SomeEventListener;
import yorickbm.towerdefence.towers.Tower;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Level;

//TODO: Random location on rotate, spawn & castle (Kinda like an army right now)
//TODO: Last wave not spawning
//TODO: Armageddon implementation
//TODO: Arena wizard command
//TODO: Utility UI
//TODO: Permissions
//TODO: Tower UI dynamic

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 *
 */
public final class TowerDefence extends JavaPlugin {

    private static TowerDefenceApi _api = null;
    public static TowerDefenceApi getApi() { return _api; }

    private static TowerDefence _plugin = null;
    public static TowerDefence getPlugin() { return _plugin; }

    private ConfigManager _scfgm = null;
    public ConfigManager getConfigManager() { return _scfgm; }

    private List<Arena> _arenas = new ArrayList<>();
    private List<Tower> _towers = new ArrayList<>();
    private List<ArenaMob> _mobs = new ArrayList<>();

    @Override
    public void onEnable() { // Plugin startup logic
        //Initiate basic things
        _plugin = this;
        _api = new TowerDefenceApi(this);
        _scfgm = new ConfigManager().Initialize("arenas.yml");

        Metrics metrics = new Metrics(this, 15916);

        //Load commands
        getCommand("startArena").setExecutor(new StartArenaCommand());
        getCommand("joinArena").setExecutor(new JoinArenaCommand());
        getCommand("stopArena").setExecutor(new StopArenaCommand());
        getCommand("createTower").setExecutor(new CreateTowerCommand());

        //Create reflection load folders
        //TODO

        //Load mobs (reflection...)
        try {
            _mobs = loadCustomData(getDataFolder() + "/mobs");
        } catch (IOException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        getLogger().log(Level.INFO, String.format("Loaded (%d) mobs!!", _mobs.size()));

        //Load arenas
        loadArenas();
        getLogger().log(Level.INFO, String.format("Loaded (%d) arenas!!", _arenas.size()));

        //Load towers (reflection...)
        try {
            _towers = loadCustomData(getDataFolder() + "/towers");
        } catch (IOException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        getLogger().log(Level.INFO, String.format("Loaded (%d) towers!!", _towers.size()));

        //Load GUIS
        InventoryGui builderGui = new InventoryGui("Buildings", 3) { };
        builderGui.setInteraction((item, player) -> {
            item.onClick(player);
        });
        builderGui.setRightClick((item, event) -> {
            if(item.getType() != Material.COMPASS) return false; //Not this guis open item!
            event.setCancelled(true); //Cancel any interaction!!! That could interrupt the GUI

            return true; //Open the inventory!
        });
        int slot = 10;
        for(Tower twr : _towers) { //TODO Move to config
            builderGui.addItem(new GuiItem(twr.getIcon(), slot++, 1).setName(twr.getName()).setLore(twr.getDescription()).setOnClick(p -> {
                if(!TowerDefence.getApi().isPlayerInArena(p)) {
                    p.sendMessage("You can only place a building if you are part of an arena!");
                    return;
                }

                if(p.getLocation().clone().subtract(0, 1,0).getBlock().getType().isAir()) {
                    p.sendMessage("Please stand on the ground where you would like to build your tower!");
                    return;
                }

                try {
                    twr.<Tower>Clone().spawnTower(TowerDefence.getApi().getArenaForPlayer(p), new TDLocation(p.getLocation()), p);
                } catch (PlayerNotInArenaException e) {
                    e.printStackTrace();
                }
            }));
        }
        GuiEventRegistry.Register(builderGui);

        getLogger().log(Level.INFO, String.format("Loaded (%d) guis!!", GuiEventRegistry.GetAll().size()));

        //Register events
        getServer().getPluginManager().registerEvents(new OpenGUIEvent(), this);
        getServer().getPluginManager().registerEvents(new InteractBuildingEvent(), this);
        getServer().getPluginManager().registerEvents(new EntityDeathEventListener(), this);
        getServer().getPluginManager().registerEvents(new SomeEventListener(), this);

        //Load permissions
        //TODO

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
        File dir = new File(getDataFolder() + "/arenas/");
        int id = 0;
        for(File f : dir.listFiles()) {
            if(f.isDirectory()) continue;
            if(!f.getName().endsWith(".json")) continue;

            Arena ar = new Arena(new JsonConfig(f));
            ar.setID(id++);

            _arenas.add(ar);

        }
    }

    JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager sjfm = jc.getStandardFileManager(null, Locale.getDefault(), null);

    private <T> List<T> loadCustomData(String baseFolder) throws IOException, ClassNotFoundException, NoSuchMethodException {
        List<T> data = new ArrayList<>();
        File serverFiles = new File(getServer().getWorldContainer().getAbsolutePath());
        File pluginFiles = new File(serverFiles.getPath().substring(0, serverFiles.getPath().length()-1) + "/plugins/");
        StringBuilder pluginDependencys = new StringBuilder();

        for(File f : serverFiles.listFiles()) {
            if(f.isDirectory()) continue;
            if(!f.getName().endsWith(".jar")) continue;

            pluginDependencys.append(f.getAbsolutePath().replace("/./", "/") + File.pathSeparator);
        }

        for(File f : pluginFiles.listFiles()) {
            if(f.isDirectory()) continue;
            if(!f.getName().endsWith(".jar")) continue;

            pluginDependencys.append(f.getAbsolutePath() + File.pathSeparator);
        }

        //System.out.println("Compiler Dependencys: " + pluginDependencys.substring(0, pluginDependencys.length()-1));

        File javaDirectory = new File(baseFolder + "/");
        for(File f : javaDirectory.listFiles()) {

            if(f.isDirectory()) continue;
            if(!f.getName().endsWith(".java")) continue;

            Iterable fileObjects = sjfm.getJavaFileObjects(f);
            String[] options = new String[]{"-d", baseFolder + "/bin", "-classpath", pluginDependencys.substring(0, pluginDependencys.length()-1) };

            jc.getTask(null, null, null, Arrays.asList(options), null, fileObjects).call();
            sjfm.close();

        }

        File classes = new File(baseFolder + "/bin/");
        URL[] urls = new URL[]{ classes.toURI().toURL() };
        URLClassLoader ucl = new URLClassLoader(urls, getClassLoader());

        for(File f : classes.listFiles()) {
            if(f.isDirectory()) continue;
            if(!f.getName().endsWith(".class")) continue;

            Class clazz = ucl.loadClass(f.getName().substring(0, f.getName().lastIndexOf('.')));
            Object object = null;
            try {
                object = clazz.getConstructor().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            data.add((T) object);
        }

        return data;
    }

    public List<Arena> getArenas() { return  _arenas; }
    public List<Tower> getTowers() { return _towers; }
    public List<ArenaMob> getMobs() { return _mobs; }
}