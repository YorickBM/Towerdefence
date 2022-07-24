package yorickbm.towerdefence;

import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import yorickbm.towerdefence.API.Exceptions.PlayerNotInArenaException;
import yorickbm.towerdefence.API.Exceptions.TeamNotFoundException;
import yorickbm.towerdefence.Mobs.ArenaMob;
import yorickbm.towerdefence.arena.Arena;
import yorickbm.towerdefence.arena.Castle;
import yorickbm.towerdefence.towers.Tower;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class TowerDefenceApi {

    private TowerDefence core;

    public TowerDefenceApi(TowerDefence core) {
        this.core = core;
    }

    /**
     * Get an arena generated from JSON on load
     *
     * @param id - ID of arena
     * @return - Instance of arena
     */
    public Arena getArena(Integer id) {
        if(id == null) {
            return core.getArenas().get(ThreadLocalRandom.current().nextInt(core.getArenas().size()));
        }
        Optional<Arena> result = core.getArenas().stream().filter(a -> a.getID() == id).findFirst();

        if(result.isPresent()) return result.get();
        return null;
    }

    /**
     * Get an immutable list of all mobs registered trough reflection
     *
     * @return immutable list of ArenaMob
     */
    public ImmutableList<ArenaMob> getRegisterdMobs() { return ImmutableList.copyOf(core.getMobs()); }

    /**
     * Get an immutable list of all towers registered trough reflection
     * @return immutable list of Tower
     */
    public ImmutableList<Tower> getRegisteredTowers() { return ImmutableList.copyOf(core.getTowers()); }

    /**
     * Check if a specific player is currently in an arena
     * @param p - Player
     * @return - If p is in an arena
     */
    public boolean isPlayerInArena(Player p) {
        try {
            Arena arena = getArenaForPlayer(p);
            return arena != null;
        } catch (PlayerNotInArenaException e) {
            return false;
        }
    }

    /**
     * Get the instance of the arena where a specific player is currently in
     * @param p - Player
     * @return - Instance of the arena player is in
     * @throws PlayerNotInArenaException - If p is not in arena
     */
    public Arena getArenaForPlayer(Player p) throws PlayerNotInArenaException {
        Optional<Arena> result = core.getArenas().stream().filter(a -> a.getPlayers().contains(p.getUniqueId())).findFirst();

        if(!result.isPresent()) throw new PlayerNotInArenaException();
        return result.get();
    }

    /**
     * Get the XP of a specific player, XP is obtained through sending mobs
     * @param player - Player
     * @return - Amount of XP for player
     */
    public int getPlayerXP(Player player) {
        return 0;
    }

    /**
     * Get the amount of tokens a specific player currently has
     * @param player - Player
     * @return - Amount of tokens for player
     */
    public int getPlayerTokens(Player player) {
        return 0;
    }

    /**
     * Add an amount of tokens to a specific player
     * @param player - Player
     * @param amount - Amount to add or remove (negative)
     */
    public void addTokensToPlayer(Player player, int amount) {
        if(getPlayerTokens(player) + amount < 0) amount = -getPlayerTokens(player); //Prevent going below 0;
    }

    /**
     * Check if a player has atleast a certain amount of tokens available
     * @param player - Player
     * @param amount - Amount required
     * @return - If player has amount of tokens or more
     */
    public boolean playersHasEnoughTokens(Player player, int amount) {
        return getPlayerTokens(player) >= amount;
    }

    /**
     * Get the health of the castle of the team a specific player is in
     * @param player - Player
     * @return - Double of the castle health
     */
    public double getCastleHealth(Player player) {
        try {
            Arena arena = getArenaForPlayer(player);
            Castle castle = arena.getCastleForPlayer(player);

            return castle.getHealth();
        } catch (PlayerNotInArenaException | TeamNotFoundException e) {
            return -1;
        }

    }

}
