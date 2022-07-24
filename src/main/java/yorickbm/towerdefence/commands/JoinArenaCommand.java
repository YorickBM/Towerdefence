package yorickbm.towerdefence.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yorickbm.towerdefence.API.Exceptions.PlayerNotInArenaException;
import yorickbm.towerdefence.TowerDefence;
import yorickbm.towerdefence.arena.Arena;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 */
public class JoinArenaCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if(!(sender instanceof Player)) {

            if(args.length < 2) {
                sender.sendMessage("Please provide a player to join to the arena!");
                return false;
            }

            if(!Bukkit.getPlayer(args[1]).isOnline()) {
                sender.sendMessage("Player is not online, try again later!");
                return true;
            }
        } else {

            //TODO Check permission

            if(TowerDefence.getApi().isPlayerInArena((Player)sender)) {
                try {
                    TowerDefence.getApi().getArenaForPlayer((Player)sender).removePlayer((Player)sender);
                } catch (PlayerNotInArenaException e) {
                    e.printStackTrace();
                }
            }

        }

        Arena arenaClass = null;
        if(args.length >= 1) arenaClass = TowerDefence.getApi().getArena(Integer.parseInt(args[0]));
        else {
            arenaClass = TowerDefence.getApi().getArena(null);
        }

        if(arenaClass == null) {
            sender.sendMessage("Could not find the arena you would like to start!");
            return true;
        }

        Player player = null;
        if(args.length < 2) player = (Player)sender;
        else player = Bukkit.getPlayer(args[1]);

        if(arenaClass.teleportLobby(player) && args.length >= 2) sender.sendMessage("Successfully added " + args[1] + " to the arena!");
        arenaClass.addPlayer(player.getUniqueId());

        return true;
    }

}
