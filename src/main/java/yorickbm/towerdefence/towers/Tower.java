package yorickbm.towerdefence.towers;

import org.bukkit.Location;
import org.bukkit.Material;
import yorickbm.towerdefence.API.Annotations.TowerLevel;
import yorickbm.towerdefence.API.Pair;
import yorickbm.towerdefence.API.TDLocation;
import yorickbm.towerdefence.Core;
import yorickbm.towerdefence.arena.Arena;
import yorickbm.towerdefence.towers.Schematic.TowerSchematic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Last modified by: YorickBM on 27-06-2022
 */
public abstract class Tower {

    protected String Name = "";
    protected String Description = "";
    protected Material icon = Material.CHEST;

    protected int TowerLevel = 1;
    protected int Range = 0;
    protected int Cooldown = 5;

    private TDLocation _location;
    private Arena _activeArena;
    private Method[] _triggersForLevel;

    protected TowerSchematic schematic;

    public Tower() {
        List<Pair<Method, TowerLevel>> _collectedData = new ArrayList<>();
        int maxLevel = 0;

        for (final Method method : this.getClass().getDeclaredMethods()) {
            if(!method.isAnnotationPresent(TowerLevel.class)) continue;

            TowerLevel annotationInstance = method.getAnnotation(TowerLevel.class);
            _collectedData.add(new Pair<>(method, annotationInstance));

            if(annotationInstance.level() > maxLevel) maxLevel = annotationInstance.level();
        }

        _triggersForLevel = new Method[maxLevel];
        for(Pair<Method, TowerLevel> data : _collectedData)
            _triggersForLevel[data.getValue().level()-1] = data.getKey();
    }

    public TDLocation getLocation() { return _location; }
    public Arena getArena() { return _activeArena; }

    /**
     * Spawns a tower for an arena on a set location, if not possible it will give the arena specific indication that i cannot spawn a tower of size x
     *
     * @param arena -> The arena the tower is spawned into
     * @param location -> The top right corner you want to spawn set tower
     */
    public void spawnTower(Arena arena, TDLocation location) {
        _location = location;
        _activeArena = arena;

        //Check if building is spawnable
        Material allowedMaterial = getArena().getBuildMaterial();
        //TODO check if material below tower is correct.
        //TODO Spawn tower schematic

        //Temp icon block spawn to showcase its there
        Location relativeLocation = new Location(Core.getInstance().getServer().getWorld(getArena().getWorldName()), location.getX(), location.getY(), location.getZ());
        relativeLocation.setY(relativeLocation.getY() + 1);
        relativeLocation.getBlock().setType(icon);
        //Temp icon block spawn to showcase its there

        //Add building to arena as checkable instance
        _activeArena.addBuilding(this);
    }

    /**
     * This function checks if there are mobs in its range
     * If yes it will trigger the towers defense
     * If not it will do nothing
     */
    public void checkMobs() {
        for(int x = 0; x < 0; x++) {
            for(int z = 0; z < 0; z++) {
                //TODO: Check for mobs
            }
        }

        trigger();
    }

    /**
     * This function will invoke the created triggers from an array that will be initialized on construction with reflection
     * This function is also responsible for checking if it can fire its event again (dependent on cooldown)
     */
    public void trigger() {

        //TODO: Check if cooldown is 0

        try {
            if(TowerLevel >= _triggersForLevel.length) {
                Core.getInstance().getLogger().log(Level.SEVERE, String.format("Tower %s has reached a level higher then possible %d of %d", Name, TowerLevel, _triggersForLevel.length));
                return; //Level not in range!!!
            }

            _triggersForLevel[TowerLevel-1].invoke(this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
