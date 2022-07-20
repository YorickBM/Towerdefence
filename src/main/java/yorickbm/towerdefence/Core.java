package yorickbm.towerdefence;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import yorickbm.towerdefence.API.TDLocation;
import yorickbm.towerdefence.API.gui.GuiEventRegistry;
import yorickbm.towerdefence.API.gui.GuiItem;
import yorickbm.towerdefence.API.gui.InventoryGui;
import yorickbm.towerdefence.API.gui.events.OpenGUIEvent;
import yorickbm.towerdefence.arena.Arena;
import yorickbm.towerdefence.commands.CreateTowerCommand;
import yorickbm.towerdefence.commands.JoinArenaCommand;
import yorickbm.towerdefence.commands.StartArenaCommand;
import yorickbm.towerdefence.commands.StopArenaCommand;
import yorickbm.towerdefence.configuration.ConfigManager;
import yorickbm.towerdefence.events.InteractBuildingEvent;
import yorickbm.towerdefence.towers.Tower;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 *
 * /createtower -83 66 69 -81 70 67
 */
public final class Core extends JavaPlugin {

    private static Core instance = null;
    public static Core getInstance() { return instance; }

    private ConfigManager _scfgm = null;
    public ConfigManager getConfigManager() { return _scfgm; }

    private List<Arena> _arenas = new ArrayList<>();
    private List<Tower> _towers = new ArrayList<>();

    @Override
    public void onEnable() { // Plugin startup logic
        //Initiat basic things
        instance = this;
        _scfgm = new ConfigManager().Initialize("arenas.yml");

        //Load commands
        getCommand("startArena").setExecutor(new StartArenaCommand());
        getCommand("joinArena").setExecutor(new JoinArenaCommand());
        getCommand("stopArena").setExecutor(new StopArenaCommand());
        getCommand("createTower").setExecutor(new CreateTowerCommand());

        //Load arenas
        loadArenas();
        getLogger().log(Level.INFO, String.format("Loaded (%d) arenas!!", _arenas.size()));

        //Load towers (reflection...)
        try {
            loadTowers();
        } catch (IOException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
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
        for(Tower twr : _towers) {
            builderGui.addItem(new GuiItem(twr.getIcon(), slot++, 1).setName(twr.getName()).setLore(twr.getDescription()).setOnClick(p -> {
                try {
                    twr.getClass().cast(twr.getClass().getConstructor().newInstance())
                            .spawnTower(_arenas.get(0), new TDLocation(p.getLocation()));
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                //TODO get arena player is part of
            }));
        }
        GuiEventRegistry.Register(builderGui);

        getLogger().log(Level.INFO, String.format("Loaded (%d) guis!!", GuiEventRegistry.GetAll().size()));

        //Register events
        getServer().getPluginManager().registerEvents(new OpenGUIEvent(), this);
        getServer().getPluginManager().registerEvents(new InteractBuildingEvent(), this);

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
    public List<Tower> getTowers() { return _towers; }

    JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager sjfm = jc.getStandardFileManager(null, Locale.getDefault(), null);

    private void loadTowers() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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

        File javaDirectory = new File(getDataFolder() + "/towers/");
        for(File f : javaDirectory.listFiles()) {

            if(f.isDirectory()) continue;
            if(!f.getName().endsWith(".java")) continue;

            Iterable fileObjects = sjfm.getJavaFileObjects(f);
            String[] options = new String[]{"-d", getDataFolder() + "/towers/bin", "-classpath", pluginDependencys.substring(0, pluginDependencys.length()-1) };

            jc.getTask(null, null, null, Arrays.asList(options), null, fileObjects).call();
            sjfm.close();

        }

        URL[] urls = new URL[]{ new File(getDataFolder() + "/towers/bin/").toURI().toURL() };
        URLClassLoader ucl = new URLClassLoader(urls, getClassLoader());

        Class clazz = ucl.loadClass("BombTower");
        Object object = clazz.getConstructor().newInstance();
        _towers.add((Tower) object);
    }

}


//    // Create a File object on the root of the directory containing the class file
//
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
