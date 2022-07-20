package yorickbm.towerdefence.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yorickbm.towerdefence.Core;
import yorickbm.towerdefence.arena.Arena;

import java.util.Random;

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

            if(Core.getInstance().isPlayerInArena((Player)sender)) {
                Core.getInstance().getArenaForPlayer((Player)sender).removePlayer((Player)sender);
            }

        }

        Arena arenaClass = null;
        if(args.length >= 1) arenaClass = Core.getInstance().getArena(Integer.parseInt(args[0]));
        else {
            arenaClass = Core.getInstance().getArenas().get(new Random().nextInt(Core.getInstance().getArenas().size()));
        }

        if(arenaClass == null) {
            sender.sendMessage("Could not find the arena you would like to start!");
            return true;
        }

        Player player = null;
        if(args.length < 2) player = (Player)sender;
        else player = Bukkit.getPlayer(args[1]);

        arenaClass.teleportLobby(player);
        arenaClass.addPlayer(player.getUniqueId());

        return true;
    }

}
