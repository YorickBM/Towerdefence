package yorickbm.towerdefence.arena;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import yorickbm.towerdefence.API.JsonConfig;
import yorickbm.towerdefence.API.Pair;
import yorickbm.towerdefence.API.TDLocation;
import yorickbm.towerdefence.TowerDefence;
import yorickbm.towerdefence.Mobs.ArenaMob;
import yorickbm.towerdefence.Mobs.Monster;
import yorickbm.towerdefence.enums.Team;
import yorickbm.towerdefence.towers.Tower;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 */
public class Arena {

    //Some variables
    private List<Pair<Team, Material>> _buildBlock = new ArrayList<>();
    private Material _directionBlock = Material.MAGENTA_GLAZED_TERRACOTTA;
    private Material _finishBlock = Material.YELLOW_GLAZED_TERRACOTTA;

    private String _arenaWorld = "world";
    private TDLocation _spawnTeamA = new TDLocation(0, 0, 0);
    private TDLocation _spawnTeamB = new TDLocation(0, 0, 0);

    private TDLocation _lobbyLocation = new TDLocation(0, 0, 0);
    private String _lobbyWorld = "world";

    private List<Wave> _waves;
    private List<Tower> _towers;
    private List<Pair<UUID, Team>> _teams;
    private List<ArenaMob> _entities;
    private List<Block> _cornersA, _cornersB;
    private List<Chunk> _chunksA, _chunksB;
    private int waveIndex = 0;

    private BukkitRunnable timer;
    private List<Chunk> _passedChunks;

    private BlockFace _spawnDirection = BlockFace.NORTH;

    //Some constructors
    public Arena(JsonConfig jsonConfig) {
        this();

        ((JSONArray)jsonConfig.getObject("buildBlock").get("teamA")).forEach(je -> {
            String data = je.toString();
            _buildBlock.add(new Pair<>(Team.RED, Material.valueOf(data)));
        });
        ((JSONArray)jsonConfig.getObject("buildBlock").get("teamB")).forEach(je -> {
            String data = je.toString();
            _buildBlock.add(new Pair<>(Team.BLUE, Material.valueOf(data)));
        });

        _arenaWorld = jsonConfig.getString("arenaWorld");
        _lobbyWorld = jsonConfig.getString("lobbyWorld");

        _spawnTeamA = new TDLocation().fromString(jsonConfig.getString("spawnTeamA"));
        _spawnTeamB = new TDLocation().fromString(jsonConfig.getString("spawnTeamB"));
        _lobbyLocation = new TDLocation().fromString(jsonConfig.getString("lobbyLocation"));

        _spawnDirection = BlockFace.valueOf(jsonConfig.getString("spawnDirection"));
        _directionBlock = Material.valueOf(jsonConfig.getString("directionBlock"));
        _finishBlock = Material.valueOf(jsonConfig.getString("finishBlock"));
    }

    public Arena() {
        _waves = new ArrayList<>();
        _towers = new ArrayList<>();
        _teams = new ArrayList<>();
        _entities = new ArrayList<>();
        _cornersA = new ArrayList<>();
        _cornersB = new ArrayList<>();

        _chunksA = new ArrayList<>();
        _chunksB = new ArrayList<>();
    }

    /**
     * Remove all towers, entities and other related data to arena.
     * By doing this the map will revert back to its original state and nothing is left behind.
     * This makes sure everything done within the arena will be temporarily!
     *
     * (BE AWARE MAY HAVE ISSUES WHEN SERVER RELOADS OR SUDDENLY SHUTSDOWN)
     */
    public void clean() {
        if(timer != null) timer.cancel();

        //kill all mobs in arena world
        for(ArenaMob entity : _entities) entity.destroy(); //.damage(entity.getHealth());

        //remove all buildings
        for(Tower tower : _towers) tower.destroy();
        _towers.clear();

        //remove all players
        for(UUID uuid : _teams.stream().map(p -> p.getKey()).collect(Collectors.toList())) Bukkit.getPlayer(uuid).teleport(Bukkit.getServer().getWorld("world").getSpawnLocation());
        _teams.clear();

        //Unload all chunks
        for(Chunk chunk : _chunksA) chunk.unload(true);
        for(Chunk chunk : _chunksB) chunk.unload(true);
    }

    /**
     * Runs the arena update every 10 ticks!
     */
    private void update() {
        for (Tower tower : _towers) tower.checkMobs(); //Update all active tower instances!
    }


    /**
     * Find next locator block
     *
     * @param location - Location to start from
     * @param direction - Direction to look into
     * @param distance - Blocks already traversed recursively
     * @return - Next locator block
     */
    private Block findNextCorner(Location location, BlockFace direction, int distance) {
        if(distance > 75) {
            System.out.println("COULD NOT FIND BLOCK WITHIN 75 BLOCKS!! " + direction);
            return null;
        }

        if(_passedChunks != null && !_passedChunks.contains(location.getChunk())) _passedChunks.add(location.getChunk());

        switch(direction) {
            case NORTH -> location.add(0, 0, -1);
            case EAST -> location.add(1, 0, 0);
            case SOUTH -> location.add(0, 0, 1);
            case WEST -> location.add(-1, 0, 0);
        }

        for(int i = -50; i < 50; i++) {
            if (location.clone().add(0, i, 0).getBlock().getType().equals(_directionBlock)
            || location.clone().add(0, i, 0).getBlock().getType().equals(_finishBlock)) return location.add(0, i, 0).getBlock();
        }
        return findNextCorner(location, direction, distance + 1);
    }

    /**
     * Spawn a zombie for both teams!
     */
    private void spawnZombie() {
        System.out.println("Running wave task!");

        Location spawnLocationA = new Location(TowerDefence.getInstance().getServer().getWorld(getWorldName()),
                _spawnTeamA.getX(), _spawnTeamA.getY(), _spawnTeamA.getZ());
        Location spawnLocationB = new Location(TowerDefence.getInstance().getServer().getWorld(getWorldName()),
                _spawnTeamB.getX(), _spawnTeamB.getY(), _spawnTeamB.getZ());

        _entities.add(new Monster(_cornersA, spawnLocationA));
        _entities.add(new Monster(_cornersB, spawnLocationB));

    }

    /**
     * Prepare the arena and teleport all teams to the map
     * Will also load all chunks traveled by the arena mobs & the path they will need to take.
     */
    public void prepare() {
        waveIndex = 0;

        timer = new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        };
        timer.runTaskTimer(TowerDefence.getInstance(), 5*20, 10);

        //TODO: Make it fill teams evenly
        int randoms = 0;
        for(Pair<UUID, Team> data : _teams) {
            Player player = TowerDefence.getInstance().getServer().getPlayer(data.getKey());

            player.sendMessage("Game will start in 5 seconds!");

            if(data.getValue() == Team.NEUTRAL) {
                if ((randoms % 2 == 0)) {
                    data.setValue(Team.RED);
                } else {
                    data.setValue(Team.BLUE);
                }
            }

            if(data.getValue() == Team.RED) teleportTeamA(player);
            if(data.getValue() == Team.BLUE) teleportTeamB(player);
        }

        _passedChunks = new ArrayList<>();
        _cornersA = loadPath(new Location(TowerDefence.getInstance().getServer().getWorld(getWorldName()),
                _spawnTeamA.getX(), _spawnTeamA.getY(), _spawnTeamA.getZ()));
        _chunksA = _passedChunks.stream().collect(Collectors.toList());

        _passedChunks = new ArrayList<>();
        _cornersB = loadPath(new Location(TowerDefence.getInstance().getServer().getWorld(getWorldName()),
                _spawnTeamB.getX(), _spawnTeamB.getY(), _spawnTeamB.getZ()));
        _chunksB = _passedChunks.stream().collect(Collectors.toList());

        for(Chunk chunk : _chunksA) chunk.setForceLoaded(true);
        for(Chunk chunk : _chunksB) chunk.setForceLoaded(true);

        spawnZombie();
    }

    /**
     * Get the path entities have to cross in this arena from a starting location!
     * It has a max distance of 75 blocks between each path locator block!
     *
     * @param location - Location to start loading from
     * @return - List of all locator blocks
     */
    public List<Block> loadPath(Location location) {
        List<Block> blocks = new ArrayList<>();
        BlockFace direction = _spawnDirection;
        Block corner;
        do {
            corner = findNextCorner(location, direction, 0);
            blocks.add(corner.getLocation().clone().add(0, 1, 0).getBlock());

            location = corner.getLocation().clone().add(0, 1, 0);
            direction = ((int)corner.getData()) == 0 ? BlockFace.NORTH :
                    ((int)corner.getData()) == 1 ? BlockFace.EAST :
                            ((int)corner.getData()) == 2 ? BlockFace.SOUTH :
                                    ((int)corner.getData()) == 3 ? BlockFace.WEST : direction;

        } while(corner.getType() != _finishBlock);

        return blocks;
    }

    /**
     * Teleport entity to lobby
     * @param entity - The entity to be teleported
     * @return - If teleport is successful
     */
    public boolean teleportLobby(Entity entity) {
        return entity.teleport(
                new Location(
                        TowerDefence.getInstance().getServer().getWorld(_lobbyWorld),
                        _lobbyLocation.getX(), _lobbyLocation.getY(), _lobbyLocation.getZ()
                )
        );
    }

    /**
     * Teleport entity to spawn of Team A
     * @param entity - The entity to be teleported
     * @return - If teleport is successful
     */
    public boolean teleportTeamA(Entity entity) {
        Location spawn = new Location(
                TowerDefence.getInstance().getServer().getWorld(_arenaWorld),
                _spawnTeamA.getX(), _spawnTeamA.getY(), _spawnTeamA.getZ());
        if(entity instanceof Player) ((Player)entity).setCompassTarget(spawn);

        return entity.teleport(spawn);
    }

    /**
     * Teleport entity to spawn of Team B
     * @param entity - The entity to be teleported
     * @return - If teleport is successful
     */
    public boolean teleportTeamB(Entity entity) {
        Location spawn = new Location(
                TowerDefence.getInstance().getServer().getWorld(_arenaWorld),
                _spawnTeamB.getX(), _spawnTeamB.getY(), _spawnTeamB.getZ());
        if(entity instanceof Player) ((Player)entity).setCompassTarget(spawn);

        return entity.teleport(spawn);
    }

    @Override
    public String toString() {
        return "{" +
                ",buildBlock='" + _buildBlock.toString() + '\'' +
                ",arenaWorld='" + _arenaWorld + '\'' +
                ",spawnTeamA='" + _spawnTeamA.toString() + '\''+
                ",spawnTeamB='" + _spawnTeamB.toString() + '\'' +
                ",lobbyLocation='" + _lobbyLocation.toString() + '\'' +
                ",lobbyWorld='" + _lobbyWorld + '\'' +
                '}';
    }


    /**
     * Convert class to jsonConfig
     * @return JSONObject to save to JsonConfig!
     */
    public JSONObject getJsonObject() {
        JSONObject data = new JSONObject();

        data.put("arenaWorld", _arenaWorld);
        data.put("lobbyWorld", _lobbyWorld);

        data.put("spawnTeamA", _spawnTeamA.toString());
        data.put("spawnTeamB", _spawnTeamB.toString());
        data.put("lobbyLocation", _lobbyLocation.toString());

        data.put("spawnDirection", _spawnDirection.toString());
        data.put("directionBlock", _directionBlock.toString());
        data.put("finishBlock", _finishBlock.toString());

        JSONArray teamA = new JSONArray();
        teamA.addAll(_buildBlock.stream().filter(p -> p.getKey().equals(Team.RED)).map(p -> p.getValue()).collect(Collectors.toList()));

        JSONArray teamB = new JSONArray();
        teamB.addAll(_buildBlock.stream().filter(p -> p.getKey().equals(Team.BLUE)).map(p -> p.getValue()).collect(Collectors.toList()));

        JSONObject buildBlock = new JSONObject();
        buildBlock.put("teamA", teamA);
        buildBlock.put("teamB", teamB);

        data.put("buildBlock", buildBlock);

        return data;
    }

    //A few getters & Setters
    public String getWorldName() {
        return _arenaWorld;
    }
    public List<Tower> getTowers() {
        return _towers;
    }
    public boolean isAllowedMaterial(Material material, Player player) {
        if(getTeamForPlayer(player) == null) return false; //Player not in an arena
        return _buildBlock.stream().filter(p -> p.getKey().equals(getTeamForPlayer(player))).map(p -> p.getValue()).filter(m -> m.equals(material)).findAny().isPresent();
    }
    public Team getTeamForPlayer(Player player) {
        Optional<Pair<UUID, Team>> data = _teams.stream().filter(p -> p.getKey().equals(player.getUniqueId())).findFirst();
        if(!data.isPresent()) return null; //No team found so return null
        return data.get().getValue();
    }
    public void addBuilding(Tower tower) { _towers.add(tower); }

    public void addPlayer(UUID uniqueId) {
        _teams.add(new Pair<>(uniqueId, Team.NEUTRAL));
    }
    public List<UUID> getPlayers() {
        return _teams.stream().map(p -> p.getKey()).collect(Collectors.toList());
    }
    public void removePlayer(Player p) { _teams.removeIf(d -> d.getKey().equals(p.getUniqueId())); }

    public void spawnNextWave() {
        Wave waveToSpawn = _waves.get(waveIndex++);
    }

    int _id = 0;
    public void setID(int id) { _id = id;}
    public int getID() {
        return _id;
    }

}
