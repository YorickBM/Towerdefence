package yorickbm.towerdefence.towers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Last modified by: YorickBM on 27-06-2022
 */
public abstract class Tower {

    protected String Name = "";
    protected String Description = "";
    protected Material icon = Material.CHEST;

    protected int TowerLevel = 0;
    protected int Range = 0;
    protected float Cooldown = 5;

    private TDLocation _location;
    private Arena _activeArena;
    private Method[] _triggersForLevel;

    protected TowerSchematic schematic;
    private List<ArmorStand> _armorStands;
    private List<Pair<Method, TowerLevel>> _collectedData;

    public Tower() {
        int maxLevel = 0;
        _armorStands = new ArrayList<>();
        _collectedData = new ArrayList<>();

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

    /**
     * Get the costs for the next level of the tower through annotations placed in towers class
     * @return - Float containing the cost to upgrade, or -1 if no costs where found!
     */
    public float getUpgradeCosts() {
        Optional<Float> cost = _collectedData.stream().map(p -> p.getValue()).filter(lvl -> lvl.level() == TowerLevel+1).map(d -> d.costs()).findFirst();

        if(!cost.isPresent()) return -1;
        return cost.get();
    }

    /**
     * Removes all objects related to tower!
     */
    public void destroy() {

        Location relativeLocation = new Location(Bukkit.getWorld(getArena().getWorldName()), _location.getX(), _location.getY(), _location.getZ());
        relativeLocation.getBlock().setType(Material.AIR);

        for(ArmorStand armorStand : _armorStands) armorStand.remove();

    }

    /**
     * Spawns a tower for an arena on a set location, if not possible it will give the arena specific indication that i cannot spawn a tower of size x
     *
     * @param arena -> The arena the tower is spawned into
     * @param location -> The center of where you want to spawn the tower
     */
    public void spawnTower(Arena arena, TDLocation location) {
        _location = location;
        _activeArena = arena;
        TowerLevel = 1;

        //Check if building is spawnable
        Material allowedMaterial = getArena().getBuildMaterial();
        //TODO check if material below tower is correct.
        //TODO Spawn tower schematic

        //Temp icon block spawn to showcase its there
        Location relativeLocation = new Location(Bukkit.getWorld(getArena().getWorldName()), location.getX(), location.getY(), location.getZ());
        relativeLocation.getBlock().setType(icon);
        //Temp icon block spawn to showcase its there

        spawnArmorStand(relativeLocation);

        //Add building to arena as checkable instance
        _activeArena.addBuilding(this);
    }

    /**
     * Spawn an armorstand in the world, related to this tower!
     */
    private void spawnArmorStand(Location location) {
        ArmorStand origin = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        origin.setInvulnerable(true);
        origin.setGravity(false);

        _armorStands.add(origin);
    }

    /**
     * This function checks if there are mobs in its range
     * If yes it will trigger the towers defense
     * If not it will do nothing
     */
    public void checkMobs() {
        Cooldown -= 0.5f;
        List<Entity> entitiesNearby = _armorStands.get(0).getNearbyEntities(Range, Range, Range);

        List<Entity> targetsNearby = entitiesNearby.stream().filter(entity ->
                entity.getType() != EntityType.ARROW
                && entity.getType() != EntityType.SPLASH_POTION
                && entity.getType() != EntityType.AREA_EFFECT_CLOUD
                && entity.getType() != EntityType.ARMOR_STAND
                && entity.getType() != EntityType.DROPPED_ITEM
                && entity.getType() != EntityType.PLAYER
        ).collect(Collectors.toList()); //Get all entities NOT blacklisted

        if(targetsNearby.size() >= 1)
            trigger(targetsNearby);
    }

    /**
     * This function will invoke the created triggers from an array that will be initialized on construction with reflection
     * This function is also responsible for checking if it can fire its event again (dependent on cooldown)
     */
    public void trigger(List<Entity> entitiesNearby) {
        if(TowerLevel < 1) return; //Its not build LOL!
        if(Cooldown >= 1) return; //Cooldown active!!

        try {
            if(TowerLevel > _triggersForLevel.length) {
                Core.getInstance().getLogger().log(Level.SEVERE, String.format("Tower %s has reached a level higher then possible %d of %d", Name, TowerLevel, _triggersForLevel.length));
                return; //Level not in range!!!
            }

            _triggersForLevel[TowerLevel-1].invoke(this, entitiesNearby);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if the given clicked entity belongs to this tower by checking it against all its armorstands entitys.
     *
     * @param clickedEntity - Entity that has been clicked
     * @return - If clicked entity is within buildings armorstands collection
     */
    public boolean didYouClickMe(Entity clickedEntity) {
        Optional<ArmorStand> matchingEntity = _armorStands.stream().filter(as -> { return as.equals(clickedEntity); }).findFirst();
        return matchingEntity.isPresent();
    }

    /**
     * Upgrade the tower to its next level! By building its next level schematic.
     * Also removes required costs from players balance!
     *
     * @param p - Player that requests the building to be upgraded!
     */
    public void Upgrade(Player p) {
        float costs = getUpgradeCosts();

        //TODO Get Schematic!

        p.sendMessage("We have upgraded " + Name + " to level " + TowerLevel);
        TowerLevel += 1;
    }

    ///A few getters & setters
    protected void setCooldown(int seconds) {
        Cooldown = seconds;
    }
    public Material getIcon() { return icon; }
    public String getName() { return Name; }
    public String getDescription() { return Description; }
    public int getLevel() { return TowerLevel; }

    public TDLocation getLocation() { return _location; }
    public Arena getArena() { return _activeArena; }
}
