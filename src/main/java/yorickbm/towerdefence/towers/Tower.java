package yorickbm.towerdefence.towers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import yorickbm.towerdefence.API.Annotations.TowerLevel;
import yorickbm.towerdefence.API.Pair;
import yorickbm.towerdefence.API.TDLocation;
import yorickbm.towerdefence.TowerDefence;
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
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
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
    private TowerSchematic[] _schematics;

    private List<ArmorStand> _armorStands;
    private List<Pair<Method, TowerLevel>> _collectedData;
    private List<EntityType> _entityBlacklist;

    public Tower() {
        int maxLevel = 0;
        _armorStands = new ArrayList<>();
        _collectedData = new ArrayList<>();
        _entityBlacklist = new ArrayList<>();

        for (final Method method : this.getClass().getDeclaredMethods()) {
            if(!method.isAnnotationPresent(TowerLevel.class)) continue;

            TowerLevel annotationInstance = method.getAnnotation(TowerLevel.class);
            _collectedData.add(new Pair<>(method, annotationInstance));

            if(annotationInstance.level() > maxLevel) maxLevel = annotationInstance.level();
        }

        _triggersForLevel = new Method[maxLevel];
        _schematics = new TowerSchematic[maxLevel];
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
        int relativeLevel = TowerLevel-1;
        if(_schematics[relativeLevel] == null)
            for (int i = relativeLevel-1; i >= 0; i--) {
                if(_schematics[i] == null) continue;
                relativeLevel = i; break;
            }
        _schematics[relativeLevel].destroy(relativeLocation);

        for(ArmorStand armorStand : _armorStands) armorStand.remove();

    }

    /**
     * Spawns a tower for an arena on a set location, if not possible it will give the arena specific indication that i cannot spawn a tower of size x
     *
     * @param arena -> The arena the tower is spawned into
     * @param location -> The center of where you want to spawn the tower
     */
    public void spawnTower(Arena arena, TDLocation location, Player player) {
        _location = location;
        _activeArena = arena;
        TowerLevel = 0;
        if(_activeArena  == null || _location == null) return; //Prevent null execution

        Location relativeLocation = new Location(Bukkit.getWorld(getArena().getWorldName()), location.getX(), location.getY(), location.getZ());

        List<Pair<Block, Material>> _blocksBelow = new ArrayList<>();
        for(int x = -1; x <= 1; x++) {
            for(int z = -1; z <= 1; z++) {
                Block block = relativeLocation.clone().add(x, -1, z).getBlock();
                _blocksBelow.add(new Pair<>(block, block.getType())); //Add block to array of 3x3
            }
        }

        if(_blocksBelow.stream()
                .map((d) -> d.getKey())
                .filter(block ->  !getArena().isAllowedMaterial(block.getType(), player) || //Make sure its part of allowed Material
                        !block.getLocation().clone().add(0, 1,0).getBlock().getType().isAir()) //Check if block above is not air
                .findAny().isPresent()) {

            List<Pair<Block,Material>> altered = _blocksBelow.stream()
                    .filter(d -> !getArena().isAllowedMaterial(d.getKey().getType(), player) || //Make sure its part of allowed Material
                            !d.getKey().getLocation().clone().add(0, 1,0).getBlock().getType().isAir()) //Check if block above is not air
                    .map(d -> { d.setKey(getFirstBelowAir(d.getKey())); d.setValue(d.getKey().getType()); return d;}).collect(Collectors.toList());

            List<BukkitRunnable> errorsRunnables = new ArrayList<>();
            for(int i = 0; i < 4; i++) { //Run a checkers pattern!
                errorsRunnables.add(new BukkitRunnable() {
                    @Override
                    public void run() {
                        int index = 0;
                        for (Block block : altered.stream().map(d -> d.getKey()).collect(Collectors.toList())) {
                            block.setType(index++ % 2 == 0 ? Material.BLACK_WOOL : Material.YELLOW_WOOL);
                        }
                    }
                });
                errorsRunnables.add(new BukkitRunnable() {
                    @Override
                    public void run() {
                        int index = 0;
                        for (Block block : altered.stream().map(d -> d.getKey()).collect(Collectors.toList())) {
                            block.setType(index++ % 2 == 1 ? Material.BLACK_WOOL : Material.YELLOW_WOOL);
                        }
                    }
                });
            }

            //Reset materials
            errorsRunnables.add(new BukkitRunnable() {
                @Override
                public void run() {
                    for(Pair<Block, Material> data : altered) {
                        data.getKey().setType(data.getValue());
                    }
                }
            });

            int delay = 0;
            for(BukkitRunnable runnable : errorsRunnables) {
                runnable.runTaskLater(TowerDefence.getInstance(), delay);
                delay += 8;
            }
            errorsRunnables.clear();

            return; //Found not allowedblock!!
        }

        //Add building to arena as checkable instance
        _activeArena.addBuilding(this);

        Upgrade(player);
        player.teleport(relativeLocation.clone().add(0, 6, 0));
    }

    /**
     * Load a tower schematic into the tower for a specific level!
     *
     * @param level - Level to assign schematic too
     * @param schematic - Schematic to assign
     * @return - Returns if schematic has been added or not
     */
    public boolean loadSchematic(int level, TowerSchematic schematic) {
        if(level >= _schematics.length) return false; //Level is above max level detected in reflection
        _schematics[level-1] = schematic;
        return true; //Schematic is set to level!
    }

    /**
     * Get first block above current block that is below an air block!
     *
     * @param block - Block you want to check if air is above
     * @return - Block below an air block
     */
    private Block getFirstBelowAir(Block block) {
        if(block.getLocation().clone().add(0, 1, 0).getBlock().getType().isAir())
            return block;
        return getFirstBelowAir(block.getLocation().clone().add(0, 1,0).getBlock());
    }

    /**
     * Spawn an armorstand in the world, related to this tower!
     */
    private void spawnArmorStand(Location location) {
        ArmorStand origin = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5, 0, 0.5), EntityType.ARMOR_STAND);
        origin.setInvulnerable(true);
        origin.setGravity(false);
        origin.setCustomNameVisible(false);
        origin.setVisible(false);

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
                && !_entityBlacklist.contains(entity.getType()) //Blacklist for player customization
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
        if(Cooldown > 0) return; //Cooldown active!!

        try {
            if(TowerLevel > _triggersForLevel.length) {
                TowerDefence.getInstance().getLogger().log(Level.SEVERE, String.format("Tower %s has reached a level higher then possible %d of %d", Name, TowerLevel, _triggersForLevel.length));
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
        final float costs = getUpgradeCosts();
        final Location relativeLocation = new Location(Bukkit.getWorld(getArena().getWorldName()), _location.getX(), _location.getY(), _location.getZ());

        //TODO Move to config
        if(TowerLevel > 0) p.sendMessage("We have upgraded " + Name + " to level " + TowerLevel);
        else p.sendMessage("We have build your tower!");
        TowerLevel += 1;

        try {
            if (_schematics[TowerLevel - 1] != null) _schematics[TowerLevel - 1].build(relativeLocation);
        }catch(IndexOutOfBoundsException e) {} //Nothing to do but pray

        spawnArmorStand(getFirstBelowAir(relativeLocation.getBlock()).getLocation().clone().subtract(0, 0.5, 0));
        _armorStands.get(0).setCustomName(getName() + " Lvl. " + getLevel());
        _armorStands.get(0).setCustomNameVisible(true);
    }

    public <T> T Clone() {
        Object instance = null;
        try {
            instance = this.getClass().getConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return (T) instance;
    }

    /**
     * Blacklist an entity from beeing a trigger for this tower.
     * For example a Bomb Tower does not attack blazes....
     *
     * @param type - EntityType to blacklist
     */
    public void blackListEntity(EntityType type) {
        _entityBlacklist.add(type);
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
