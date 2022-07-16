package yorickbm.towerdefence;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import yorickbm.towerdefence.API.gui.GuiEventRegistry;
import yorickbm.towerdefence.API.gui.InventoryGui;
import yorickbm.towerdefence.API.gui.events.OpenGUIEvent;
import yorickbm.towerdefence.arena.Arena;
import yorickbm.towerdefence.commands.CreateTowerCommand;
import yorickbm.towerdefence.commands.JoinArenaCommand;
import yorickbm.towerdefence.commands.StartArenaCommand;
import yorickbm.towerdefence.commands.StopArenaCommand;
import yorickbm.towerdefence.configuration.ConfigManager;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

/**
 * Last modified by: YorickBM on 27-06-2022
 *
 * /createtower -83 66 69 -81 70 67
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

    JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager sjfm = jc.getStandardFileManager(null, Locale.getDefault(), null);

    private void fixTowerImports() throws IOException {

        File javaDirectory = new File(getDataFolder() + "/towers/");

        for(File f : javaDirectory.listFiles()) {
            if(f.isDirectory()) continue;
            if(!f.getName().endsWith(".java")) continue;

            List<String> newLines = new ArrayList<>();
            for(String line : Files.readAllLines(Path.of(f.getAbsolutePath()), StandardCharsets.UTF_8)) {
                if(!line.startsWith("import ")) { newLines.add(line); continue; }

                String imporz = line.split(" ")[1];
                imporz = imporz.substring(0, imporz.length()-1);
                if(imporz.startsWith("org.bukkit.craftbukkit.") && !imporz.startsWith("org.bukkit.")) continue; //Already reformatted!

                String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                imporz = imporz.replace("org.bukkit.", "org.bukkit.craftbukkit." + version);

                System.out.println("Fixed Import: " + "import " + imporz + ";");
                newLines.add("import " + imporz + ";");

            }
            Files.write(Path.of(f.getAbsolutePath()), newLines, StandardCharsets.UTF_8);

        }

    }

    private void loadTowers() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        fixTowerImports();

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

        System.out.println("Compiler Dependencys: " + pluginDependencys.substring(0, pluginDependencys.length()-1));

        File javaDirectory = new File(getDataFolder() + "/towers/");
        for(File f : javaDirectory.listFiles()) {

            Iterable fileObjects = sjfm.getJavaFileObjects(f);
            String[] options = new String[]{"-d", getDataFolder() + "/towers/bin", "-classpath", pluginDependencys.substring(0, pluginDependencys.length()-1) };

            System.out.println("getTask");
            jc.getTask(null, null, null, Arrays.asList(options), null, fileObjects).call();
            sjfm.close();

        }

        URL[] urls = new URL[]{ new URL(getDataFolder() + "/towers/bin/") };
        URLClassLoader ucl = new URLClassLoader(urls);

        Class clazz = ucl.loadClass("yorickbm.towerdefence.towers.GenericTower");
        Method method = clazz.getDeclaredMethod("trigger", null);

        Object object = clazz.getConstructor().newInstance();
        method.invoke(object, null);

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
